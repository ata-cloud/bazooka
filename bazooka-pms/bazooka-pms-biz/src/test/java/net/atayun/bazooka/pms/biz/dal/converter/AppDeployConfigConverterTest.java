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
package net.atayun.bazooka.pms.biz.dal.converter;

import net.atayun.bazooka.base.enums.deploy.DeployModeEnum;
import net.atayun.bazooka.base.utils.JsonUtil;
import net.atayun.bazooka.pms.api.dto.AppDeployConfigDto;
import net.atayun.bazooka.pms.api.param.HealthCheck;
import net.atayun.bazooka.pms.api.param.PortMapping;
import net.atayun.bazooka.pms.api.param.VolumeMount;
import net.atayun.bazooka.pms.biz.dal.entity.AppDeployConfigEntity;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.*;

/**
 * @author WangSongJun
 * @date 2019-07-16
 */
public class AppDeployConfigConverterTest {

    @Test
    public void test() {
        List<PortMapping> portMappings = Arrays.asList(
                new PortMapping(8080, 28080),
                new PortMapping(9999, 28081)
        );

        Map<String, Object> env = new HashMap<>();
        env.put("active", "test");
        env.put("ENV", "test");

        List<VolumeMount> volumeMounts = Arrays.asList(
                new VolumeMount("/etc/hosts", "/data/etc/hosts", VolumeMount.Mode.ReadAndWrite),
                new VolumeMount("/export", "/data/export", VolumeMount.Mode.ReadAndWrite)
        );

        List<HealthCheck> healthChecks = Arrays.asList(
                new HealthCheck(60, 30, 3, 0, 30, 15, HealthCheck.Protocol.HTTP, HealthCheck.IpProtocol.IPv4, "/actuator/health")
        );

        AppDeployConfigDto appDeployConfigDto = new AppDeployConfigDto(
                1L,
                10L,
                3L,
                "TEST",
                "发布配置",
                "构建发布",
                DeployModeEnum.BUILD,
                null,
                "master",
                "mvn clean package -DskipTests=true -U",
                "docker/Dockerfile",
                0.4D,
                1024,
                0,
                1,
                "java -jar app.jar",
                null,
                portMappings,
                env,
                volumeMounts,
                healthChecks,
                "admin",
                LocalDateTime.now()
        );

        AppDeployConfigEntity deployConfigEntity = AppDeployConfigConverter.appDeployConfigEntity(appDeployConfigDto);

        System.out.println(JsonUtil.toJson(appDeployConfigDto));
        System.out.println(JsonUtil.toJson(deployConfigEntity));

        AppDeployConfigDto deployConfigDto = AppDeployConfigConverter.appDeployConfigDto(deployConfigEntity);
        System.out.println(JsonUtil.toJson(deployConfigDto));
    }
}