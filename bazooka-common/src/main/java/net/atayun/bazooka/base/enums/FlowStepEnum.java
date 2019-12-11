package net.atayun.bazooka.base.enums;

/**
 * 流程可能的步骤, 实例名需要与{@link net.atayun.bazooka.base.constant.FlowStepConstants}保存一致
 *
 * @author Ping
 * @see net.atayun.bazooka.base.constant.FlowStepConstants
 */
public enum FlowStepEnum {

    /**
     *
     */
    START_APP("启动"),
    RESTART_APP("重启"),
    STOP_APP("停止"),
    SCALE_APP("扩容"),
    ROLLBACK_APP("回滚"),
    SET_UP("准备"),
    PULL_CODE("拉取代码"),
    PACKAGE_PROJECT("打包项目"),
    BUILD_DOCKER_IMAGE("构建镜像"),
    DEPLOY("发布"),
    PUSH_DOCKER_IMAGE("推送镜像"),
    DELETE_DOCKER_IMAGE("删除镜像"),
    HEALTH_CHECK("健康检查");


    private String description;

    FlowStepEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
