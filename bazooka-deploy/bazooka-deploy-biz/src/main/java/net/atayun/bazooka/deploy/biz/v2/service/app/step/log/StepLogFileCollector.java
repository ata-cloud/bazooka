package net.atayun.bazooka.deploy.biz.v2.service.app.step.log;

import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOptFlowStep;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Ping
 */
@Component
public class StepLogFileCollector implements StepLogCollector {

    @Override
    public void collect(AppOptFlowStep appOptFlowStep, String log) {
        String logPath = appOptFlowStep.getLogPath();
        File file = new File(logPath + "_" + System.currentTimeMillis());
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        try (PrintWriter printWriter = new PrintWriter(
                new OutputStreamWriter(
                        new FileOutputStream(file, true), StandardCharsets.UTF_8), true)) {
            printWriter.println(log);
            printWriter.println();
            printWriter.println();
        } catch (FileNotFoundException e) {
            //...
        }

    }

    @Override
    public void merge(AppOptFlowStep appOptFlowStep) {
        File file = new File(appOptFlowStep.getLogPath());
        if (!file.getParentFile().exists()) {
            return;
        }
        List<File> parts = getPartFiles(file);
        if (CollectionUtils.isEmpty(parts)) {
            return;
        }
        try (FileChannel rfc = new FileOutputStream(file, true).getChannel()) {
            for (File partFile : parts) {
                try (FileChannel pfc = new FileInputStream(partFile).getChannel()) {
                    rfc.transferFrom(pfc, rfc.size(), pfc.size());
                }
            }
        } catch (IOException e) {
            //...
        }
    }

    @Override
    public String get(AppOptFlowStep appOptFlowStep) {
        String log = "";
        File file = new File(appOptFlowStep.getLogPath());
        if (!file.getParentFile().exists()) {
            return log;
        }
        if (file.exists()) {
            try {
                log = Files.lines(file.toPath()).collect(Collectors.joining("\n"));
            } catch (IOException e) {
                //...
            }
            return log;
        }
        List<File> partFiles = getPartFiles(file);
        if (CollectionUtils.isEmpty(partFiles)) {
            return log;
        }
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

    private List<File> getPartFiles(File file) {
        File[] list = file.getParentFile().listFiles();
        if (list == null || list.length == 0) {
            return null;
        }
        String prefix = file.getName() + "_";
        return Arrays.stream(list)
                .filter(f -> f.getName().startsWith(prefix))
                .sorted((l, r) -> {
                    Integer lInt = Integer.parseInt(l.getName().substring(prefix.length()));
                    Integer rInt = Integer.parseInt(r.getName().substring(prefix.length()));
                    return lInt.compareTo(rInt);
                })
                .collect(Collectors.toList());
    }
}
