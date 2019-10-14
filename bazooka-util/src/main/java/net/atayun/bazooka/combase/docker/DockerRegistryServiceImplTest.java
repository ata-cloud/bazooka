///*
// *    Copyright 2018-2019 the original author or authors.
// *
// *    Licensed under the Apache License, Version 2.0 (the "License");
// *    you may not use this file except in compliance with the License.
// *    You may obtain a copy of the License at
// *
// *        http://www.apache.org/licenses/LICENSE-2.0
// *
// *    Unless required by applicable law or agreed to in writing, software
// *    distributed under the License is distributed on an "AS IS" BASIS,
// *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// *    See the License for the specific language governing permissions and
// *    limitations under the License.
// */
//package net.atayun.bazooka.combase.docker;
//
//import lombok.extern.slf4j.Slf4j;
//
///**
// * @author WangSongJun
// * @date 2019-07-19
// */
//@Slf4j
//public class DockerRegistryServiceImplTest {
//
//    private DockerRegistryService dockerRegistryService = new DockerRegistryServiceImpl();
//
//    /**
//     * 镜像列表
//     */
//    @Test
//    public void catalog() {
//        String registry = "http://registry.gs.youyuwo.com";
//        dockerRegistryService.catalog(registry, 999)
//                .getRepositories()
//                .stream()
//                .forEach(imageName -> log.info("imageName:[{}]", imageName));
//    }
//
//    /**
//     * 镜像tags
//     */
//    @Test
//    public void listImageTags() {
//        String registry = "http://registry.gs.youyuwo.com";
//        String imageName = "platform/ops-pms";
//        dockerRegistryService.listImageTags(registry, imageName)
//                .getTags()
//                .stream()
//                .forEach(tag -> log.info("tag:[{}]", tag));
//    }
//
//    /**
//     * 删除镜像tag
//     */
//    @Test
//    public void deleteTag() {
//        String registry = "http://registry.gs.youyuwo.com";
//        String imageName = "platform/ops-pms";
//        String imageTag = "feature_pms_181018_146";
//
//        dockerRegistryService.deleteTag(registry, imageName, imageTag);
//    }
//}