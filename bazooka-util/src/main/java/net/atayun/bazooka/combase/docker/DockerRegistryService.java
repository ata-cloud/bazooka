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
package net.atayun.bazooka.combase.docker;

import net.atayun.bazooka.combase.docker.domain.DockerCatalogResp;
import net.atayun.bazooka.combase.docker.domain.DockerImageTags;

/**
 * 部分 docker registry api
 *
 * @author superwen
 * @date 2018/12/14 下午3:23
 */
public interface DockerRegistryService {

    /**
     * 查询镜像列表
     *
     * @param registry
     * @param number
     * @return
     */
    DockerCatalogResp catalog(String registry, Integer number);

    /**
     * 查询镜像tags
     *
     * @param registry
     * @param imageName
     * @return
     */
    DockerImageTags listImageTags(String registry, String imageName);

    /**
     * 删除镜像tag
     *
     * @param registry
     * @param name
     * @param tag
     */
    void deleteTag(String registry, String name, String tag);
}
