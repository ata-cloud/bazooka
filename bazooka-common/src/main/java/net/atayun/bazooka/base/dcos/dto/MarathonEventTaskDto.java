package net.atayun.bazooka.base.dcos.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Ping
 */
@Getter
@Setter
public class MarathonEventTaskDto {

    private String taskStatus;

    private String message;

    private String appId;

    private String timestamp;

    private String version;
}
