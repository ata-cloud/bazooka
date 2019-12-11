package net.atayun.bazooka.deploy.biz.v2.enums;

/**
 * @author Ping
 */
public enum FlowStepStatusEnum {

    /**
     * 准备中
     */
    STAND_BY("未开始"),

    /**
     * 进行中
     */
    PROCESS("进行中"),

    /**
     * 完成
     */
    SUCCESS("成功"),

    /**
     * 失败
     */
    FAILURE("失败"),

    /**
     * 失败
     */
    CANCEL("取消");

    private String description;

    FlowStepStatusEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

}
