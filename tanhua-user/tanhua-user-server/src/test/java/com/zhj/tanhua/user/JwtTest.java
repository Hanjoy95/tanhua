package com.zhj.tanhua.user;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author huanjie.zhuang
 * @date 2021/6/4
 */
public class JwtTest {

    @Test
    public void jwt() {
        String secret = "zhj-yoho";

        Map<String, Object> claims = new HashMap<>();
        claims.put("mobile", "12345789");
        claims.put("id", "1");

        // 生成token
        String jwt = Jwts.builder()
                .setClaims(claims) //设置响应数据体
                .signWith(SignatureAlgorithm.HS256, secret) //设置加密方法和加密盐
                .compact();

        System.out.println(jwt);

        // 通过token解析数据
        Map<String, Object> body = Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(jwt)
                .getBody();

        System.out.println(body);
    }
}
