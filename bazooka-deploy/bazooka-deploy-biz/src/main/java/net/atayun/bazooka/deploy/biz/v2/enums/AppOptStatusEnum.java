package net.atayun.bazooka.deploy.biz.v2.enums;

/**
 * @author Ping
 */
public enum AppOptStatusEnum {

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
    FAILURE("失败");

    private String description;

    AppOptStatusEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }
}
