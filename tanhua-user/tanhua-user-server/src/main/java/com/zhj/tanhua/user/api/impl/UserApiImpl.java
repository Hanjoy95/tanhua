package com.zhj.tanhua.user.api.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhj.tanhua.common.exception.*;
import com.zhj.tanhua.user.api.UserApi;
import com.zhj.tanhua.user.config.RabbitmqConfig;
import com.zhj.tanhua.user.dao.UserDao;
import com.zhj.tanhua.user.pojo.po.User;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 用户dubbo接口实现
 *
 * @author huanjie.zhuang
 * @date 2021/6/12
 */
@DubboService(version = "1.0")
@Slf4j
public class UserApiImpl implements UserApi {

    @Autowired
    private UserDao userDao;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;


    @Value("${jwt.secret}")
    private String secret;

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
            throw new CheckCodeExpiredException("验证码已过期，请重新发送");
        }

        if (!StringUtils.equals(value, checkCode)) {
            throw new ParameterInvalidException("验证输入错误");
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
            throw new ResourceHasExistException("上一次发送的验证还未过期");
        }

        String checkCode = sendSms(phone);
        if (null == checkCode) {
            log.error("phone: {}, sent check code fail", phone);
            throw new BaseException("发送验证码失败");
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
            throw new TokenExpiredException("token已过期，请重新登录");
        }

        // 刷新时间
        redisTemplate.expire(redisTokenKey, 1, TimeUnit.HOURS);

        return MAPPER.readValue(cacheData, User.class);
    }

    /**
     * 修改密码
     *
     * @param userId 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    @Override
    public void modifyPassword(Long userId, String oldPassword, String newPassword) {

        User user = userDao.selectById(userId);
        if (!user.getPassword().equals(DigestUtils.md5Hex(oldPassword))) {
            throw new ParameterInvalidException("用户密码错误");
        }
        user.setPassword(DigestUtils.md5Hex(newPassword));
        userDao.update(user, Wrappers.<User>lambdaUpdate().eq(User::getId, userId));
    }
}
