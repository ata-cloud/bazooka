package net.atayun.bazooka.deploy.biz.v2.enums;

import net.atayun.bazooka.deploy.biz.v2.constant.FlowStepConstants;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static net.atayun.bazooka.deploy.biz.v2.constant.FlowStepConstants.*;

/**
 * @author Ping
 */
public enum AppOptEnum {

    /**
     * 启动App
     */
    START {
        @Override
        public List<String> defaultFlowStepTypes() {
            return Arrays.asList(FlowStepConstants.START_APP, HEALTH_CHECK);
        }
    },

    /**
     * 关闭App
     */
    STOP {
        @Override
        public List<String> defaultFlowStepTypes() {
            return Arrays.asList(FlowStepConstants.STOP_APP, HEALTH_CHECK);
        }
    },

    /**
     * 扩容App
     */
    SCALE {
        @Override
        public List<String> defaultFlowStepTypes() {
            return Arrays.asList(FlowStepConstants.SCALE_APP, HEALTH_CHECK);
        }
    },

    /**
     * 重启服务
     */
    RESTART {
        @Override
        public List<String> defaultFlowStepTypes() {
            return Arrays.asList(FlowStepConstants.RESTART_APP, HEALTH_CHECK);
        }
    },

    /**
     * 回滚
     */
    ROLLBACK {
        @Override
        public List<String> defaultFlowStepTypes() {
            return Arrays.asList(FlowStepConstants.ROLLBACK_APP, HEALTH_CHECK);
        }
    },

    /**
     * 通过镜像发布
     */
    IMAGE_DEPLOY {
        @Override
        public List<String> defaultFlowStepTypes() {
            return Arrays.asList(SET_UP, DEPLOY, HEALTH_CHECK);
        }
    },

    /**
     * 通过镜像发布
     */
    BUILD_DEPLOY {
        @Override
        public List<String> defaultFlowStepTypes() {
            return Arrays.asList(
                    SET_UP,
                    PULL_CODE,
                    PACKAGE_PROJECT,
                    BUILD_DOCKER_IMAGE,
                    DEPLOY,
                    HEALTH_CHECK);
        }
    },

    /**
     * 通过镜像发布
     */
    BUILD_SINGLE_DEPLOY {
        @Override
        public List<String> defaultFlowStepTypes() {
            return Arrays.asList(
                    SET_UP,
                    PULL_CODE,
                    PACKAGE_PROJECT,
                    BUILD_DOCKER_IMAGE,
                    DEPLOY);
        }
    },

    /**
     * 推送镜像
     */
    PUSH_IMAGE {
        @Override
        public List<String> defaultFlowStepTypes() {
            return Collections.singletonList(PUSH_DOCKER_IMAGE);
        }
    },

    /**
     * 删除镜像
     */
    DELETE_IMAGE {
        @Override
        public List<String> defaultFlowStepTypes() {
            return Collections.singletonList(DELETE_DOCKER_IMAGE);
        }
    };

    public abstract List<String> defaultFlowStepTypes();
}
