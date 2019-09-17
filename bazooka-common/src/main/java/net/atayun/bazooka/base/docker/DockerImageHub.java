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

import net.atayun.bazooka.base.docker.domain.DockerCatalogResp;
import net.atayun.bazooka.base.docker.domain.DockerImageTags;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

/**
 * docker 镜像库 接口
 *
 * @author WangSongJun
 * @date 2018-09-26 3:48 PM Wednesday
 */
public interface DockerImageHub {

    /**
     * 镜像tags
     *
     * @param name
     * @return
     * @throws DockerImageHubException
     */
    @RequestLine("GET /v2/{name}/tags/list")
    @Headers({"X-API-Source: feign/client"})
    DockerImageTags listImageTags(@Param("name") String name) throws DockerImageHubException;


    @RequestLine("GET /v2/_catalog?n={n}")
    @Headers({"X-API-Source: feign/client"})
    DockerCatalogResp catalog(@Param("n") Integer n) throws DockerImageHubException;
}
