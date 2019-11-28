package net.atayun.bazooka.deploy.biz.v2.service.app.step.log;

import com.youyu.common.exception.BizException;
import org.springframework.util.StringUtils;

import java.util.Optional;

/**
 * @author Ping
 */
public class StepLogBuilder {

    private final StringBuilder stringBuilder = new StringBuilder();

    public StepLogBuilder append(String content) {
        if (StringUtils.hasText(content)) {
            stringBuilder.append(content).append("\n");
        }
        return this;
    }

    public StepLogBuilder append(Throwable throwable) {
        String content = "异常: ";
        if (throwable instanceof BizException) {
            BizException bizException = (BizException) throwable;
            content += "[Code: " + bizException.getCode() + "] " + bizException.getDesc();
        } else {
            content += Optional.ofNullable(throwable.getMessage())
                    .orElseGet(() -> Optional.ofNullable(throwable.getCause())
                            .map(Throwable::getMessage)
                            .orElse("异常信息未知"));

        }
        return append(content);
    }

    public String build() {
        return stringBuilder.toString();
    }
}
