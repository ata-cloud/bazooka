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
package net.atayun.bazooka.deploy.biz.log;

import net.atayun.bazooka.combase.enums.deploy.AppOperationEventLogTypeEnum;
import net.atayun.bazooka.deploy.biz.constants.DeployConstants;
import net.atayun.bazooka.deploy.biz.dal.entity.app.AppOperationEventDetailEntity;
import net.atayun.bazooka.deploy.biz.service.app.AppOperationEventDetailService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Ping
 * @date 2019-08-01
 */
public class AppOperationEventLogFile implements AppOperationEventLog {

    @Autowired
    private AppOperationEventDetailService appOperationEventDetailService;

    @Override
    public void save(Long eventId, AppOperationEventLogTypeEnum appOperationEventLogTypeEnum, Integer part, String content) {
        String fileStr = getFileFullPath(eventId, appOperationEventLogTypeEnum);
        if (part != null) {
            fileStr = fileStr + "." + DeployConstants.ATA_PART + part;
        }
        File file = new File(fileStr);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
        }
        try (PrintWriter printWriter =
                     new PrintWriter(
                             new OutputStreamWriter(
                                     new FileOutputStream(fileStr, true), StandardCharsets.UTF_8), true)) {
            printWriter.println(content);
            printWriter.println();
        } catch (IOException e) {
            //...
        }
    }

    @Override
    public String get(Long eventId, AppOperationEventLogTypeEnum appOperationEventLogTypeEnum) {
        String fileFullPath = getFileFullPath(eventId, appOperationEventLogTypeEnum);
        File file = new File(fileFullPath);
        String log = "";
        if (file.exists()) {
            try {
                log = Files.lines(file.toPath()).collect(Collectors.joining("\n"));
            } catch (IOException e) {
                //...
            }
            return log;
        }
        List<File> partFiles = getPartFiles(eventId, appOperationEventLogTypeEnum);
        return partFiles.stream()
                .map(partFile -> {
                    Stream<String> stream = null;
                    try {
                        stream = Files.lines(partFile.toPath());
                    } catch (IOException e) {
                        //..
                    }
                    return stream;
                })
                .filter(Objects::nonNull)
                .flatMap(stream -> stream)
                .collect(Collectors.joining("\n"));
    }

    @Override
    public void mergePartFile(Long eventId, AppOperationEventLogTypeEnum appOperationEventLogTypeEnum) {
        String fileFullPath = getFileFullPath(eventId, appOperationEventLogTypeEnum);
        List<File> partFiles = getPartFiles(eventId, appOperationEventLogTypeEnum);
        try (FileChannel rfc = new FileOutputStream(fileFullPath, true).getChannel()) {
            for (File partFile : partFiles) {
                FileChannel pfc = new FileInputStream(partFile).getChannel();
                rfc.transferFrom(pfc, rfc.size(), pfc.size());
                pfc.close();
            }
        } catch (IOException e) {
            //...
        }
    }

    private String getFilename(Long eventId, AppOperationEventLogTypeEnum appOperationEventLogTypeEnum) {
        return eventId + "_" + appOperationEventLogTypeEnum.name();
    }

    private List<File> getPartFiles(Long eventId, AppOperationEventLogTypeEnum appOperationEventLogTypeEnum) {
        String logDir = getLogDir(eventId);
        String fileName = getFilename(eventId, appOperationEventLogTypeEnum);
        File file = new File(logDir);
        File[] list = file.listFiles();
        if (list == null) {
            return new ArrayList<>();
        }
        String prefix = fileName + "." + DeployConstants.ATA_PART;
        return Arrays.stream(list)
                .filter(f -> f.getName().startsWith(prefix))
                .sorted((l, r) -> {
                    Integer lInt = Integer.parseInt(l.getName().substring(prefix.length()));
                    Integer rInt = Integer.parseInt(r.getName().substring(prefix.length()));
                    return lInt.compareTo(rInt);
                })
                .collect(Collectors.toList());
    }

    private String getLogDir(Long eventId) {
        AppOperationEventDetailEntity appOperationEventDetailEntity = appOperationEventDetailService.selectByEventId(eventId);
        return appOperationEventDetailEntity.getLogDir();
    }

    private String getFileFullPath(Long eventId, AppOperationEventLogTypeEnum appOperationEventLogTypeEnum) {
        return getLogDir(eventId) + File.separator + getFilename(eventId, appOperationEventLogTypeEnum);
    }
}
