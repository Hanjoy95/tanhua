package com.zhj.tanhua.user;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

/**
 * @author huanjie.zhuang
 * @date 2021/6/14
 */
public class JacksonTest {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Test
    @SneakyThrows
    public void str2Object() {
        String str = "{\"id\":1,\"phone\":\"18219111295\"}";
        UserDto user = MAPPER.readValue(str, UserDto.class);
        System.out.println("");
    }
}
