package net.atayun.bazooka.base.enums;

import net.atayun.bazooka.base.constant.FlowStepConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static net.atayun.bazooka.base.constant.FlowStepConstants.*;

/**
 * @author Ping
 */
public enum AppOptEnum {

    /**
     * 启动App
     */
    START("启动App", true) {
        @Override
        public List<String> defaultFlowStepTypes() {
            return Arrays.asList(FlowStepConstants.START_APP, HEALTH_CHECK);
        }
    },

    /**
     * 关闭App
     */
    STOP("关闭App", true) {
        @Override
        public List<String> defaultFlowStepTypes() {
            return Arrays.asList(FlowStepConstants.STOP_APP, HEALTH_CHECK);
        }
    },

    /**
     * 扩容App
     */
    SCALE("扩容App", true) {
        @Override
        public List<String> defaultFlowStepTypes() {
            return Arrays.asList(FlowStepConstants.SCALE_APP, HEALTH_CHECK);
        }
    },

    /**
     * 重启服务
     */
    RESTART("重启服务", true) {
        @Override
        public List<String> defaultFlowStepTypes() {
            return Arrays.asList(FlowStepConstants.RESTART_APP, HEALTH_CHECK);
        }
    },

    /**
     * 回滚
     */
    ROLLBACK("回滚", true) {
        @Override
        public List<String> defaultFlowStepTypes() {
            return Arrays.asList(FlowStepConstants.ROLLBACK_APP, HEALTH_CHECK);
        }
    },

    DEPLOY("发布", true) {
        @Override
        public List<String> defaultFlowStepTypes() {
            return new ArrayList<>();
        }
    },

    /**
     * 通过镜像发布
     */
    IMAGE_DEPLOY("镜像发布", true) {
        @Override
        public List<String> defaultFlowStepTypes() {
            return Arrays.asList(SET_UP, FlowStepConstants.DEPLOY, HEALTH_CHECK);
        }
    },

    /**
     * 通过构建发布
     */
    MARATHON_BUILD_DEPLOY("构建发布(Marathon)", true) {
        @Override
        public List<String> defaultFlowStepTypes() {
            return Arrays.asList(
                    SET_UP,
                    PULL_CODE,
                    PACKAGE_PROJECT,
                    BUILD_DOCKER_IMAGE,
                    FlowStepConstants.DEPLOY,
                    HEALTH_CHECK);
        }
    },

    /**
     * 通过构建单节点发布
     */
    NODE_BUILD_DEPLOY("构建发布(Node)", true) {
        @Override
        public List<String> defaultFlowStepTypes() {
            return Arrays.asList(
                    SET_UP,
                    PULL_CODE,
                    PACKAGE_PROJECT,
                    BUILD_DOCKER_IMAGE,
                    FlowStepConstants.DEPLOY);
        }
    },

    /**
     * 推送镜像
     */
    PUSH_IMAGE("推送镜像", true) {
        @Override
        public List<String> defaultFlowStepTypes() {
            return Collections.singletonList(PUSH_DOCKER_IMAGE);
        }
    },

    /**
     * 删除镜像
     */
    DELETE_IMAGE("删除镜像", true) {
        @Override
        public List<String> defaultFlowStepTypes() {
            return Collections.singletonList(DELETE_DOCKER_IMAGE);
        }
    };

    public abstract List<String> defaultFlowStepTypes();

    private String description;

    private boolean mutex;

    AppOptEnum(String description, boolean mutex) {
        this.description = description;
        this.mutex = mutex;
    }

    public String getDescription() {
        return description;
    }

    public boolean isMutex() {
        return mutex;
    }

    public static List<AppOptEnum> deployList() {
        return Arrays.asList(IMAGE_DEPLOY, MARATHON_BUILD_DEPLOY, NODE_BUILD_DEPLOY);
    }

    public static List<AppOptEnum> lastSuccessList() {
        return Arrays.asList(IMAGE_DEPLOY, MARATHON_BUILD_DEPLOY, NODE_BUILD_DEPLOY, ROLLBACK, SCALE);
    }
}
