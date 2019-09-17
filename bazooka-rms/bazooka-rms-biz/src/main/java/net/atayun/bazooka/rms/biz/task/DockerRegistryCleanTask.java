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
package net.atayun.bazooka.rms.biz.task;

import net.atayun.bazooka.base.docker.DockerRegistryService;
import net.atayun.bazooka.rms.api.dto.RmsDockerImageDto;
import net.atayun.bazooka.rms.api.enums.DockerImageSource;
import net.atayun.bazooka.rms.biz.dal.entity.RmsDockerImageEntity;
import net.atayun.bazooka.rms.biz.service.RmsDockerImageService;
import com.youyu.common.enums.IsDeleted;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 镜像库清理
 * <p>
 * 镜像库需要配置允许删除tag！
 * <p>
 * 镜像定时清理规则：
 * 查询镜像信息表，单个服务在单个环境的镜像保留最新（按镜像更新时间）的n（配置项）个
 * 单个服务在单个环境的镜像有两种情况：自行构建的、别的环境推送来的。
 * 分别在数据库中记录这个镜像的更新时间（镜像进入此镜像库的时间，包括构建或者推送时间），
 * 镜像清理时按照更新时间进行清理。
 * 默认用户是从ATA页面进行镜像推送，非法操作不处理。
 *
 * @author WangSongJun
 * @date 2019-07-19
 */
@Slf4j
@Component
public class DockerRegistryCleanTask {

    /**
     * 保留的个数
     */
    @Value("${ops.registry.reserve:10}")
    private Integer reserveSize;

    @Autowired
    private DockerRegistryService dockerRegistryService;
    @Autowired
    private RmsDockerImageService dockerImageService;

    @Scheduled(cron = "0 0 3 * * ? ")
    public void cleanDockerImageTag() {
        log.info("DockerRegistry执行清洗计划...");
        try {
            List<DockerImage> cleanList = this.getCleanTags(reserveSize);
            log.info("需要清理的镜像[{}]个.", cleanList.size());
            cleanList.forEach(dockerImage -> {
                log.info("清理镜像：[{}/{}:{}]", dockerImage.getRegistry(), dockerImage.imageName, dockerImage.imageTag);

                dockerRegistryService.deleteTag(dockerImage.getRegistry(), dockerImage.imageName, dockerImage.imageTag);

                List<RmsDockerImageEntity> rmsDockerImageEntityList = this.dockerImageService.selectEntity(new RmsDockerImageEntity(dockerImage.getRegistry(), dockerImage.imageName, dockerImage.imageTag, IsDeleted.NOT_DELETED));
                if (!ObjectUtils.isEmpty(rmsDockerImageEntityList)) {
                    rmsDockerImageEntityList.forEach(rmsDockerImageEntity -> this.dockerImageService.updateByPrimaryKeySelective(new RmsDockerImageEntity(rmsDockerImageEntity.getId(), IsDeleted.DELETED)));
                }
            });
        } catch (Exception e) {
            log.warn("DockerRegistry清洗计划执行失败", e);
        }
    }


    /**
     * 需要清理的镜像信息列表
     * <p>
     * 所有镜像信息根据环境分组；
     * 环境下每个应用保留N个tag；
     * 如果镜像是推送过来的，且在同一个镜像库，那么原镜像也不可被清除；
     *
     * @param reserve
     * @return
     */
    private List<DockerImage> getCleanTags(int reserve) {
        log.info("开始统计需要清理的镜像列表...");
        List<DockerImage> cleanList = new ArrayList<>(16);
        List<DockerImage> reserveList = new ArrayList<>(16);

        List<RmsDockerImageDto> all = this.dockerImageService.select(new RmsDockerImageEntity(IsDeleted.NOT_DELETED));
        if (!ObjectUtils.isEmpty(all)) {
            //根据环境分组的列表
            Map<Long, List<RmsDockerImageDto>> envImageListMap = all.stream().collect(Collectors.groupingBy(rmsDockerImageDto -> rmsDockerImageDto.getEnvId()));

            for (Map.Entry<Long, List<RmsDockerImageDto>> entry : envImageListMap.entrySet()) {
                log.info("开始处理环境[{}]的镜像", entry.getKey());
                //环境下根据应用分组的列表
                Map<String, List<RmsDockerImageDto>> appImageListMap = entry.getValue()
                        .stream()
                        .sorted(Comparator.comparing(RmsDockerImageDto::getImageCreateTime).reversed())
                        .collect(Collectors.groupingBy(dockerImageDto -> dockerImageDto.getImageName()));

                for (Map.Entry<String, List<RmsDockerImageDto>> appEntry : appImageListMap.entrySet()) {
                    log.info("镜像名：[{}]", appEntry.getKey());
                    //每个应用保留最新的{reserve}个镜像
                    List<RmsDockerImageDto> imageDtoList = appEntry.getValue();
                    for (int i = 0; i < imageDtoList.size(); i++) {
                        RmsDockerImageDto image = imageDtoList.get(i);
                        if (i < reserve && DockerImageSource.Push.equals(image.getSource())) {
                            reserveList.add(new DockerImage(image.getRegistry(), image.getImageName(), image.getImageTag()));
                        } else if (i >= reserve) {
                            cleanList.add(new DockerImage(image.getRegistry(), image.getImageName(), image.getImageTag()));
                        }
                    }
                }
            }
        }

        //如果镜像被推送到其他环境，且用的是同一个镜像库中那就不能删除
        if (!ObjectUtils.isEmpty(reserveList)) {
            List<DockerImage> cleanListCopy = new ArrayList<>(cleanList);
            for (DockerImage d : cleanListCopy) {
                if (reserveList.contains(d)) {
                    cleanList.remove(d);
                }
            }
        }

        log.info("镜像列表统计完毕！");
        return cleanList;
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class DockerImage {
        private String registry;
        private String imageName;
        private String imageTag;
    }
}
