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
package net.atayun.bazooka.base.mlb;

import com.youyu.common.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import net.atayun.bazooka.base.enums.code.OpsCommonExceptionCode;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static net.atayun.bazooka.base.constant.CommonConstants.PROTOCOL;
import static java.lang.Integer.valueOf;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.*;

/**
 * @Author: hanxiaorui
 * @Date: 2019/7/18 15:31
 * @Description: MLB服务bean组件
 */
@Slf4j
public class MlbServiceBean {

    /**
     * mlb端口
     */
    private static final String MARATHON_LB_PORT = "9090";

    /**
     * mlb地址
     */
    private static final String MARATHON_LB_STATS_CSV = "/haproxy?stats;csv";

    private static final String BACKEND = "BACKEND";
    private static final String FRONTEND = "FRONTEND";

    private RestTemplate restTemplate;

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * 获取MarathonLb详细
     *
     * @param url
     * @return
     */
    public List<MlbServiceDto> getMlbServices(String url) {
        try {
            String mlbUrl = MlbBuilder.builder()
                    .url(url)
                    .port(MARATHON_LB_PORT)
                    .action(MARATHON_LB_STATS_CSV)
                    .build();
            String[] mlbBodyArr = split(restTemplate.getForObject(mlbUrl, String.class), "\n");
            if (mlbBodyArr == null || mlbBodyArr.length <= 1) {
                return null;
            }
            return stream(mlbBodyArr, 1, mlbBodyArr.length) // exclusive first row
                    .filter(body -> !containsAny(body, BACKEND, FRONTEND))
                    .map(this::parseMlbService)
                    .collect(toList());
        } catch (Exception e) {
            log.error("MlbServiceBean.getMlbServices() got exception. ", e);
            throw new BizException(OpsCommonExceptionCode.MLB_QRY_SERVICE_ERROR);
        }
    }

    private MlbServiceDto parseMlbService(String mlbBody) {

        MlbServiceDto mlb = new MlbServiceDto();
        String mlbServiceAndPort = substringBefore(mlbBody, ",");
        mlb.setService(mlbServiceAndPort.substring(0, mlbServiceAndPort.lastIndexOf("_")));
        mlb.setPort(valueOf(mlbServiceAndPort.substring(mlbServiceAndPort.lastIndexOf("_") + 1)));
        return mlb;
    }

    private static class MlbBuilder {

        static MlbBuilder builder() {
            return new MlbBuilder();
        }

        private String url;
        private String port;
        private String action;

        MlbBuilder url(String url) {
            this.url = url;
            return this;
        }

        MlbBuilder port(String port) {
            this.port = port;
            return this;
        }

        MlbBuilder action(String action) {
            this.action = action;
            return this;
        }

        public String build() {
            return new StringBuilder()
                    .append(PROTOCOL)
                    .append(url)
                    .append(":")
                    .append(port)
                    .append(action)
                    .toString();
        }
    }
}
