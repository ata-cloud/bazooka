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
package net.atayun.bazooka.base.docker;

import feign.*;
import feign.codec.ErrorDecoder;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import feign.slf4j.Slf4jLogger;
import mesosphere.client.common.ModelUtils;

import static java.util.Arrays.asList;

/**
 * @author WangSongJun
 * @date 2018-09-26 4:08 PM Wednesday
 */
public class DockerImageHubClient {

    public static final String DEBUG_JSON_OUTPUT = "DEBUG_JSON_OUTPUT";

    public static DockerImageHub getInstance(String endpoint) {
        return getInstance(endpoint, null);
    }

    /**
     * The generalized version of the method that allows more in-depth customizations via
     * {@link RequestInterceptor}s.
     *
     * @param endpoint     URL of DockerImageHub
     * @param interceptors optional request interceptors
     * @return DockerImageHub client
     */
    public static DockerImageHub getInstance(String endpoint, RequestInterceptor... interceptors) {

        Feign.Builder b = Feign.builder()
                .encoder(new GsonEncoder(ModelUtils.GSON))
                .decoder(new GsonDecoder(ModelUtils.GSON))
                .errorDecoder(new DockerHubErrorDecoder());
        if (interceptors != null) {
            b.requestInterceptors(asList(interceptors));
        }
        String debugOutput = System.getenv(DEBUG_JSON_OUTPUT);
        if ("System.out".equals(debugOutput)) {
            System.setProperty("org.slf4j.simpleLogger.logFile", "System.out");
            System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");
            b.logger(new Slf4jLogger()).logLevel(Logger.Level.FULL);
        } else if (debugOutput != null) {
            b.logger(new Logger.JavaLogger().appendToFile(debugOutput)).logLevel(Logger.Level.FULL);
        }
        b.requestInterceptor(new DockerImageHubClient.DockerHubHeadersInterceptor());
        return b.target(DockerImageHub.class, endpoint);
    }

    static class DockerHubHeadersInterceptor implements RequestInterceptor {
        @Override
        public void apply(RequestTemplate template) {
            template.header("Accept", "application/json");
            template.header("Content-Type", "application/json");
        }
    }

    static class DockerHubErrorDecoder implements ErrorDecoder {
        @Override
        public Exception decode(String methodKey, Response response) {
            return new DockerImageHubException(response.status(), response.reason());
        }
    }

}
