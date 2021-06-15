package com.zhj.tanhua.user.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhj.tanhua.common.exception.BaseRunTimeException;
import com.zhj.tanhua.user.api.UserApi;
import com.zhj.tanhua.user.config.RabbitmqConfig;
import com.zhj.tanhua.user.dao.UserDao;
import com.zhj.tanhua.user.dao.UserInfoDao;
import com.zhj.tanhua.user.enums.SexEnum;
import com.zhj.tanhua.user.po.UserInfo;
import com.zhj.tanhua.user.dto.UserDto;
import com.zhj.tanhua.user.po.User;
import com.zhj.tanhua.user.dto.UserInfoDto;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author huanjie.zhuang
 * @date 2021/6/12
 */
@DubboService(version = "1.0.0")
@Slf4j
public class UserService implements UserApi {

    @Autowired
    private UserDao userDao;
    @Autowired
    private UserInfoDao userInfoDao;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
//    @Autowired
//    private PicUploadService picUploadService;
//    @Autowired
//    private FaceEngineService faceEngineService;

    @Value("${jwt.secret}")
    private String secret;

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    /**
     * 登录
     *
     * @param phone 用户手机号
     * @param checkCode  验证码
     * @return UserDto
     */
    @Override
    public UserDto login(String phone, String checkCode) {

        // 校验验证码是否正确
        String redisKey = "CHECK_CODE_" + phone;
        String value = redisTemplate.opsForValue().get(redisKey);

        if (StringUtils.isEmpty(value)) {
            throw new BaseRunTimeException("验证码失效");
        }

        if (!StringUtils.equals(value, checkCode)) {
            throw new BaseRunTimeException("验证码输入错误");
        }

        // 默认是已注册
        boolean isNew = false;

        // 校验该手机号是否已经注册，如果没有注册，需要注册一个账号，如果已经注册，直接登录
        User user = userDao.selectOne(Wrappers.<User>lambdaQuery().eq(User::getPhone, phone));

        if (null == user) {
            // 该手机号未注册
            user = new User();
            user.setPhone(phone);
            // 默认密码
            user.setPassword(DigestUtils.md5Hex("123456"));
            userDao.insert(user);
            log.info("用户[手机号:{}], 新注册", phone);
            isNew = true;
        }

        Map<String, Object> claims = new HashMap<>(2);
        claims.put("id", user.getId());
        claims.put("phone", phone);

        // 生成token
        String token = Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();

        // 将token存储到redis中
        String redisTokenKey = "TOKEN_" + token;
        String redisTokenValue;
        try {
            redisTokenValue = MAPPER.writeValueAsString(user);
        } catch (JsonProcessingException e) {
            log.error("用户[手机号:{}], 存储token出错", phone, e);
            throw new BaseRunTimeException("存储token出错");
        }
        if (null == redisTokenValue) {
            throw new BaseRunTimeException("存储token出错");
        }
        redisTemplate.opsForValue().set(redisTokenKey, redisTokenValue, Duration.ofHours(1));

        // 发送消息
        try {
            Map<String, Object> msg = new HashMap<>(3);
            msg.put("id", user.getId());
            msg.put("phone", phone);
            msg.put("date", new Date());
            rabbitTemplate.convertAndSend(RabbitmqConfig.EXCHANGE, RabbitmqConfig.ROUTING_KEY, msg);
        } catch (Exception e) {
            log.error("用户[手机号:{}], 发送消息出错", phone, e);
            throw new BaseRunTimeException("发送消息出错");
        }

        return UserDto.builder().id(user.getId()).phone(phone).isNew(isNew).token(token).build();
    }

    /**
     * 发送验证码
     *
     * @param phone 用户手机号
     * @return UserDto
     */
    @Override
    public UserDto sentCheckCode(String phone) {

        String redisKey = "CHECK_CODE_" + phone;
        String value = redisTemplate.opsForValue().get(redisKey);
        if (StringUtils.isNotEmpty(value)) {
            throw new BaseRunTimeException("上一次发送的验证码还未失效");
        }

        String checkCode = sendSms(phone);
        if (null == checkCode) {
            log.error("用户[手机号:{}], 发送短信验证码出错", phone);
            throw new BaseRunTimeException("发送短信验证码失败");
        }

        // 将验证码存储到redis,2分钟后失效
        redisTemplate.opsForValue().set(redisKey, checkCode, Duration.ofMinutes(2));

        return UserDto.builder().phone(phone).checkCode(checkCode).build();
    }

    private String sendSms(String phone) {
//        String url = "https://open.ucpaas.com/ol/sms/sendsms";
//        Map<String, Object> params = new HashMap<>();
//        params.put("sid", "56f6523e8f50c85fe92d5d12a8dabd6f");
//        params.put("token", "41fabadd9a221ab4a439548b4dc88433");
//        params.put("appid", "dd7d74e604284a6b9cc668c6591c84c7");
//        params.put("templateid", "487656");
//        params.put("phone", mobile);
//        params.put("param", RandomUtils.nextInt(100000, 999999));
//        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, params, String.class);
//        String body = responseEntity.getBody();
//
//        try {
//            JsonNode jsonNode = MAPPER.readTree(body);
//            //000000 表示发送成功
//            if (StringUtils.equals(jsonNode.get("code").textValue(), "000000")) {
//                return String.valueOf(params.get("param"));
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return null;

        int checkCode = RandomUtils.nextInt(100000, 999999);
        System.out.println("--------------验证码: " + checkCode + "--------------");

        return String.valueOf(checkCode);
    }

    /**
     * 根据token查询用户数据
     *
     * @param token 用户token
     * @return UserDto
     */
    @Override
    @SneakyThrows
    public UserDto getUserByToken(String token) {

        String redisTokenKey = "TOKEN_" + token;
        String cacheData = redisTemplate.opsForValue().get(redisTokenKey);
        if (StringUtils.isEmpty(cacheData)) {
            return null;
        }
        // 刷新时间
        redisTemplate.expire(redisTokenKey, 1, TimeUnit.HOURS);

        return MAPPER.readValue(cacheData, UserDto.class);
    }

    /**
     * 完善个人信息
     *
     * @param userInfoDto 用户信息
     */
    @Override
    public void saveUserInfo(UserInfoDto userInfoDto) {

        UserInfo userInfo = new UserInfo();
        BeanUtils.copyProperties(userInfoDto, userInfo);
        userInfo.setSex(userInfoDto.getSex().getValue());

        try {
            userInfoDao.insert(userInfo);
        } catch (DuplicateKeyException e) {
            userInfoDao.update(userInfo, Wrappers.<UserInfo>lambdaQuery()
                    .eq(UserInfo::getUserId, userInfoDto.getUserId()));
        }

    }

    /**
     * 上传用户头像
     *
     * @param userId 用户ID
     * @param file 用户头像图片文件
     */
    @Override
    @SneakyThrows
    public void saveAvatar(Long userId, MultipartFile file) {

//        // 校验图片是否为人像
//        if (!faceEngineService.checkIsPortrait(file.getBytes())) {
//           throw new BaseRunTimeException("图片非人像，请重新上传!");
//        }
//
//        // 图片上传到阿里云OSS
//        PicUploadResult uploadResult = picUploadService.upload(file);
//        if (null == uploadResult.getName()) {
//            throw new BaseRunTimeException("上传头像失败");
//        }
//
//        userInfoDao.update(null, Wrappers.<UserInfo>lambdaUpdate()
//                .set(UserInfo::getLogo, uploadResult.getName())
//                .eq(UserInfo::getId, result.getData().getId()));

        userInfoDao.update(null, Wrappers.<UserInfo>lambdaUpdate()
                .set(UserInfo::getAvatar, UUID.randomUUID())
                .eq(UserInfo::getId, userId));
    }

    /**
     * 获取用户详细信息
     *
     * @param userId 用户ID
     * @return UserInfoDto
     */
    @Override
    public UserInfoDto getUserInfo(Long userId) {

        UserInfoDto userInfoDto = new UserInfoDto();
        UserInfo userInfo = userInfoDao.selectOne(Wrappers.<UserInfo>lambdaQuery().eq(UserInfo::getUserId, userId));
        BeanUtils.copyProperties(userInfo, userInfoDto);
        userInfoDto.setPhone(userDao.selectById(userId).getPhone());
        userInfoDto.setSex(SexEnum.getType(userInfo.getSex()));

        return userInfoDto;
    }

    /**
     * 获取用户详细信息列表
     *
     * @param userIds 用户ID列表
     * @param sex 性别
     * @param age 年龄
     * @param city 城市
     * @return List<UserInfoDto>
     */
    @Override
    public List<UserInfoDto> getUserInfos(List<Long> userIds, String sex, Integer age, String city) {

        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("user_id", userIds);
        if (StringUtils.isNotEmpty(sex)) {
            queryWrapper.eq("sex", sex);
        }
        if (null != age) {
            queryWrapper.lt("age", age);
        }
        if (StringUtils.isNotEmpty(sex)) {
            queryWrapper.eq("city", city);
        }

        List<UserInfo> userInfos = userInfoDao.selectList(queryWrapper);
        Map<Long, String> userMap = userDao.selectBatchIds(userIds)
                .stream().collect(Collectors.toMap(User::getId, User::getPhone));
        List<UserInfoDto> userInfoDtos = new ArrayList<>();
        for (UserInfo userInfo : userInfos) {
            UserInfoDto userInfoDto = new UserInfoDto();
            BeanUtils.copyProperties(userInfo, userInfoDto);
            userInfoDto.setPhone(userMap.get(userInfo.getUserId()));
            userInfoDto.setSex(SexEnum.getType(userInfo.getSex()));
            userInfoDtos.add(userInfoDto);
        }

        return userInfoDtos;
    }
}
