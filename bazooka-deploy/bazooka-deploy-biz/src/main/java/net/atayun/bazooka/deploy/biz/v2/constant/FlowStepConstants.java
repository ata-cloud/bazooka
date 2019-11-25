package net.atayun.bazooka.deploy.biz.v2.constant;

/**
 * @author Ping
 */
public class FlowStepConstants {

    /**
     * 启动服务
     */
    public static final String START_APP = "START_APP";

    /**
     * 重启服务
     */
    public static final String RESTART_APP = "RESTART_APP";

    /**
     * 停止服务
     */
    public static final String STOP_APP = "STOP_APP";

    /**
     * 扩容服务
     */
    public static final String SCALE_APP = "SCALE_APP";

    /**
     * 回滚
     */
    public static final String ROLLBACK_APP = "ROLLBACK_APP";

    /**
     * 准备
     */
    public static final String SET_UP = "SET_UP";

    /**
     * 拉取代码
     */
    public static final String PULL_CODE = "PULL_CODE";

    /**
     * 打包项目
     */
    public static final String PACKAGE_PROJECT = "PACKAGE_PROJECT";

    /**
     * 构建镜像
     */
    public static final String BUILD_DOCKER_IMAGE = "BUILD_DOCKER_IMAGE";

    /**
     * 发布服务
     */
    public static final String DEPLOY = "DEPLOY";

    /**
     * 推送镜像
     */
    public static final String PUSH_DOCKER_IMAGE = "PUSH_DOCKER_IMAGE";

    /**
     * 删除镜像
     */
    public static final String DELETE_DOCKER_IMAGE = "DELETE_DOCKER_IMAGE";

    /**
     * 健康检查
     */
    public static final String HEALTH_CHECK = "HEALTH_CHECK";
}
