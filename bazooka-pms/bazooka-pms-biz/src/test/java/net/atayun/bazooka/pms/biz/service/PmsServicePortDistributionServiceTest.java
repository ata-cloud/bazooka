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
package net.atayun.bazooka.pms.biz.service;

import net.atayun.bazooka.pms.api.dto.PmsServicePortDistributionDto;
import net.atayun.bazooka.pms.api.enums.ServicePortState;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author WangSongJun
 * @date 2019-07-31
 */
@Slf4j
public class PmsServicePortDistributionServiceTest {

    @Test
    public void distributionServicePortTest() {
        List<PmsServicePortDistributionDto> servicePortDistributionDtoList = new ArrayList<>(Arrays.asList(
                new PmsServicePortDistributionDto(1L, 1L, 1L, 3L, 8080, 18080, Boolean.TRUE, ServicePortState.Saved),
                new PmsServicePortDistributionDto(1L, 1L, 1L, 3L, 8088, 18088, Boolean.FALSE, ServicePortState.Saved),
                new PmsServicePortDistributionDto(1L, 1L, 1L, 3L, 80, 18082, Boolean.TRUE, ServicePortState.Saved),
                new PmsServicePortDistributionDto(1L, 1L, 1L, 3L, 9999, 18081, Boolean.TRUE, ServicePortState.Saved),
                new PmsServicePortDistributionDto(1L, 1L, 1L, 3L, 8083, 18083, Boolean.TRUE, ServicePortState.Saved)
        ));

        //是一个新的容器端口，需要分配新的服务端口

        List<PmsServicePortDistributionDto> collect = servicePortDistributionDtoList.stream()
                .filter(dto -> dto.getContinuous())
                .collect(Collectors.toList());

        log.info("collect:{}", collect);

        List<PmsServicePortDistributionDto> collect1 = collect.stream()
                .sorted(Comparator.comparing(PmsServicePortDistributionDto::getServicePort).reversed())
                .collect(Collectors.toList());
        log.info("collect1:{}", collect1);

    }

}