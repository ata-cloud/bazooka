/*
 *    Copyright 2018-2019 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package net.atayun.bazooka.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

/**
 * @author rache
 * @date 2019-05-06
 */
@Configuration
@EnableSwagger2
@Profile({"dev", "test"})
public class Swagger2Configuration {

    @Bean
    public Docket createRestApi() {
        List<Parameter> list = new ArrayList<>();
        list.add(new ParameterBuilder().name("X-Uid").description("user id").modelRef(new ModelRef("string")).parameterType("header").required(false).build());
        list.add(new ParameterBuilder().name("X-Rn").description("user name").modelRef(new ModelRef("string")).parameterType("header").required(false).build());
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(new ApiInfoBuilder()
                        .title("阿塔云")
                        .description("ops")
                        .contact(new Contact("刘剑鸣", "", "liujianming@yofish.com"))
                        .version("1.0")
                        .build())
                .select()

                .apis(RequestHandlerSelectors.basePackage("net.atayun.bazooka"))
                .paths(PathSelectors.any())
                .build()
                .globalOperationParameters(list);
    }

}
