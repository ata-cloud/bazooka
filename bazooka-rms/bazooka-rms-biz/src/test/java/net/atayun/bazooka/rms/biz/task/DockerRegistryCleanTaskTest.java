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

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author WangSongJun
 * @date 2019-07-19
 */
@Slf4j
public class DockerRegistryCleanTaskTest {

    @Test
    public void cleanDockerImageTag() {
        List<String> imageTags = this.imageTag();
        log.info("tags:{}", imageTags);

        Map<String, List<String>> listMap = imageTags.stream()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.groupingBy(tag -> {
                    String t = tag.substring(0, tag.indexOf("_"));
                    log.info(t);
                    return t;
                }));


        log.info("listMap:{}", listMap);

    }

    private List<String> imageTag() {
        return new ArrayList<>(Arrays.asList(
                "feature_pms_180921_34",
                "feature_pms_180921_36",
                "feature_pms_180921_37",
                "feature_pms_180921_38",
                "feature_pms_180925_39",
                "feature_pms_180925_40",
                "feature_pms_180925_41",
                "feature_pms_180926_42",
                "feature_pms_180926_43",
                "feature_pms_180926_45",
                "feature_pms_180926_46",
                "feature_pms_180926_47",
                "feature_pms_180926_48",
                "feature_pms_180927_49",
                "feature_pms_180927_50",
                "f3.0_190703_46",
                "f3.0_190703_47",
                "master_190703_39",
                "master_190703_40",
                "master_190703_41",
                "f3.0_190705_48",
                "master_190705_1",
                "master_190708_2",
                "master_190708_3",
                "f3.0_190711_50",
                "master_190711_5",
                "f3.0_190712_51",
                "master_190712_6"
        ));
    }
}