package net.atayun.bazooka.delpoy2.component.strategy.flow;

/**
 * @Author: xiongchengwei
 * @Date: 2019/9/27 上午10:42
 */
public enum FlowExecuteResultEnum {

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


    FlowExecuteResultEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }
}
