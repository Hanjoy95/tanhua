package com.zhj.tanhua.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author huanjie.zhuang
 * @date 2021/6/3
 */
@Configuration
@EnableSwagger2
public class Knife4jConfig {

    @Bean(value = "defaultApi2")
    public Docket defaultApi2() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(new ApiInfoBuilder()
                        .title("tanhua API文档")
                        .description("tanhua项目是一个校园交友平台")
                        .termsOfServiceUrl("http://localhost:9000")
                        .contact(new Contact("huanjie.zhuang", "www.zhj.com", "651113906@qq.com"))
                        .version("1.0")
                        .build())
                // 分组名称
//                .groupName("1.0版本")
                .select()
                // 指定Controller扫描包路径
                .apis(RequestHandlerSelectors.basePackage("com.zhj.tanhua.server.controller"))
                .paths(PathSelectors.any())
                .build();
    }
}
