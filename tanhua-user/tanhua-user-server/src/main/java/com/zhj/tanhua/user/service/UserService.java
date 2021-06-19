package com.zhj.tanhua.user.service;

import com.aliyun.oss.OSS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhj.tanhua.common.enums.ImageTypeEnum;
import com.zhj.tanhua.common.exception.*;
import com.zhj.tanhua.user.api.UserApi;
import com.zhj.tanhua.user.config.RabbitmqConfig;
import com.zhj.tanhua.user.dao.UserDao;
import com.zhj.tanhua.user.dao.UserInfoDao;
import com.zhj.tanhua.user.enums.SexEnum;
import com.zhj.tanhua.user.pojo.dto.UserInfoDto;
import com.zhj.tanhua.user.pojo.po.UserInfo;
import com.zhj.tanhua.user.pojo.po.User;
import com.zhj.tanhua.user.pojo.to.UserInfoTo;
import com.zhj.tanhua.user.pojo.to.UserTo;
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

import java.io.ByteArrayInputStream;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 用户模块的服务层
 *
 * @author huanjie.zhuang
 * @date 2021/6/12
 */
@DubboService(version = "1.0")
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
    @Autowired
    private OSS oss;

    @Value("${jwt.secret}")
    private String secret;
    @Value("${aliyun.bucketName}")
    private String bucketName;
    @Value("${aliyun.urlPrefix}")
    private String urlPrefix;

    private static final String DEFAULT_PASSWORD = "123456";
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    /**
     * 登录
     *
     * @param phone 用户手机号
     * @param checkCode  验证码
     * @return UserTo
     */
    @Override
    @SneakyThrows
    public UserTo login(String phone, String checkCode) {

        // 校验验证码是否正确
        String value = redisTemplate.opsForValue().get("CHECK_CODE_" + phone);

        if (StringUtils.isEmpty(value)) {
            throw new CheckCodeExpiredException("checkCode expired, please sent again");
        }

        if (!StringUtils.equals(value, checkCode)) {
            throw new ParameterInvalidException("checkCode input error");
        }

        // 校验该手机号是否已经注册，如果没有注册，需要注册一个账号，如果已经注册，直接登录
        User user = userDao.selectOne(Wrappers.<User>lambdaQuery().eq(User::getPhone, phone));

        // 默认是已注册
        boolean isNew = false;

        if (null == user) {
            // 该手机号未注册
            user = new User();
            user.setPhone(phone);
            // 默认密码
            user.setPassword(DigestUtils.md5Hex(DEFAULT_PASSWORD));
            userDao.insert(user);
            log.info("phone: {}, new registration", phone);
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
        String redisTokenValue = MAPPER.writeValueAsString(user);
        redisTemplate.opsForValue().set("TOKEN_" + token, redisTokenValue, Duration.ofHours(1));

        // 发送消息
        try {
            Map<String, Object> msg = new HashMap<>(3);
            msg.put("id", user.getId());
            msg.put("phone", phone);
            msg.put("created", new Date());
            rabbitTemplate.convertAndSend(RabbitmqConfig.EXCHANGE, RabbitmqConfig.ROUTING_KEY, msg);
        } catch (Exception e) {
            log.error("phone: {}, sent message error", phone, e);
            throw new SentMessageException("phone: {}, sent message error", e);
        }

        return UserTo.builder().isNew(isNew).userId(user.getId()).token(token).build();
    }

    /**
     * 发送验证码
     *
     * @param phone 用户手机号
     * @return UserDto
     */
    @Override
    public String sentCheckCode(String phone) {

        String redisKey = "CHECK_CODE_" + phone;
        String value = redisTemplate.opsForValue().get(redisKey);
        if (StringUtils.isNotEmpty(value)) {
            throw new ResourceDuplicateException("the last sent checkCode has not expired");
        }

        String checkCode = sendSms(phone);
        if (null == checkCode) {
            log.error("phone: {}, sent check code error", phone);
            throw new BaseException("sent checkCode error");
        }

        // 将验证码存储到redis,2分钟后失效
        redisTemplate.opsForValue().set(redisKey, checkCode, Duration.ofMinutes(2));

        return checkCode;
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
     * @return User
     */
    @Override
    @SneakyThrows
    public User getUserByToken(String token) {

        String redisTokenKey = "TOKEN_" + token;
        String cacheData = redisTemplate.opsForValue().get(redisTokenKey);
        if (StringUtils.isEmpty(cacheData)) {
            throw new TokenExpiredException("token expired, please login again");
        }

        // 刷新时间
        redisTemplate.expire(redisTokenKey, 1, TimeUnit.HOURS);

        return MAPPER.readValue(cacheData, User.class);
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

        // 校验图片文件后缀名
        if (Arrays.stream(ImageTypeEnum.values()).noneMatch(image ->
                StringUtils.endsWithIgnoreCase(file.getOriginalFilename(), image.getType()))) {
            throw new BaseException("image type error, only support jpg, jpeg, gif, png");
        }

        // 文件路径, avatar/{userId}/{currentTimeMillis}.{imageType}
        String fileUrl = "avatar/" + userId + "/" + System.currentTimeMillis() +
                StringUtils.substringAfterLast(file.getOriginalFilename(), ".");

        // 上传阿里云OSS
        try {
            oss.putObject(bucketName, fileUrl, new ByteArrayInputStream(file.getBytes()));
        } catch (Exception e) {
            throw new BaseException(e.getMessage());
        }

        userInfoDao.update(null, Wrappers.<UserInfo>lambdaUpdate()
                .set(UserInfo::getAvatar, urlPrefix + fileUrl)
                .eq(UserInfo::getUserId, userId));
    }

    /**
     * 获取用户详细信息
     *
     * @param userId 用户ID
     * @return UserInfoTo
     */
    @Override
    public UserInfoTo getUserInfo(Long userId) {

        UserInfo userInfo = userInfoDao.selectOne(Wrappers.<UserInfo>lambdaQuery().eq(UserInfo::getUserId, userId));
        UserInfoTo userInfoTo = new UserInfoTo();
        BeanUtils.copyProperties(userInfo, userInfoTo);
        userInfoTo.setPhone(userDao.selectById(userId).getPhone());
        userInfoTo.setGender(SexEnum.getType(userInfo.getSex()));

        return userInfoTo;
    }

    /**
     * 获取用户详细信息列表
     *
     * @param userIds 用户ID列表
     * @param sex 性别
     * @param age 年龄
     * @param city 城市
     * @return List<UserInfoTo>
     */
    @Override
    public List<UserInfoTo> getUserInfos(List<Long> userIds, Integer sex, Integer age, String city) {

        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("user_id", userIds);
        if (null != sex) {
            queryWrapper.eq("sex", sex);
        }
        if (null != age) {
            queryWrapper.lt("age", age);
        }
        if (StringUtils.isNotEmpty(city)) {
            queryWrapper.eq("city", city);
        }

        List<UserInfo> userInfos = userInfoDao.selectList(queryWrapper);
        Map<Long, String> userMap = userDao.selectBatchIds(userIds)
                .stream().collect(Collectors.toMap(User::getId, User::getPhone));
        List<UserInfoTo> userInfoToList = new ArrayList<>();
        for (UserInfo userInfo : userInfos) {
            UserInfoTo userInfoTo = new UserInfoTo();
            BeanUtils.copyProperties(userInfo, userInfoTo);
            userInfoTo.setPhone(userMap.get(userInfo.getUserId()));
            userInfoTo.setGender(SexEnum.getType(userInfo.getSex()));
            userInfoToList.add(userInfoTo);
        }

        return userInfoToList;
    }
}
