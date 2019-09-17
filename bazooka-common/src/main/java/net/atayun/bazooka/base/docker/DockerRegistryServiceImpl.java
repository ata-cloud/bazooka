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

import net.atayun.bazooka.base.constant.CommonConstants;
import net.atayun.bazooka.base.docker.domain.DockerCatalogResp;
import net.atayun.bazooka.base.docker.domain.DockerImageTags;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/**
 * @author WangSongJun
 * @date 2019-07-19
 */
@Slf4j
public class DockerRegistryServiceImpl implements DockerRegistryService {

    private final OkHttpClient client = new OkHttpClient();

    /**
     * 查询镜像列表
     *
     * @param registry
     * @param number
     * @return
     */
    @Override
    public DockerCatalogResp catalog(String registry, Integer number) {
        return DockerImageHubClient.getInstance(registry).catalog(number);
    }

    /**
     * 查询镜像tags
     *
     * @param registry
     * @param imageName
     * @return
     */
    @Override
    public DockerImageTags listImageTags(String registry, String imageName) {
        return DockerImageHubClient.getInstance(registry).listImageTags(imageName);
    }

    /**
     * 删除镜像tag
     *
     * @param registry
     * @param name
     * @param tag
     */
    @Override
    public void deleteTag(String registry, String name, String tag) {
        if (!registry.startsWith(CommonConstants.PROTOCOL)) {
            registry = CommonConstants.PROTOCOL + registry;
        }
        String dockerDigestUrl = new StringBuilder(registry).append("/v2/").append(name).append("/manifests/").append(tag).toString();

        Request request = new Request.Builder()
                .url(dockerDigestUrl)
                .addHeader("accept", "application/vnd.docker.distribution.manifest.v2+json")
                .get()
                .build();


        try {
            Response response = client.newCall(request).execute();
            String finalRegistry = registry;
            response.headers("Docker-Content-Digest")
                    .stream()
                    .forEach(digest -> {
                        String deleteUrl = new StringBuilder(finalRegistry).append("/v2/").append(name).append("/manifests/").append(digest).toString();
                        try {
                            Response executeResponse = client.newCall(new Request.Builder().delete().url(deleteUrl).build()).execute();
                            log.info("execute response message:{}", executeResponse.message());
                            log.info("删除镜像tag结果：[{}]", executeResponse.isSuccessful() ? "成功" : "失败");
                        } catch (IOException e) {
                            log.warn("delete docker image digest error!", e);
                        }
                    });
        } catch (IOException e) {
            log.warn("get docker image digest error!", e);
        }


    }
}
