package net.atayun.bazooka.deploy.biz.v2.dto.app;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.atayun.bazooka.deploy.biz.v2.enums.AppOptEnum;

/**
 * @author Ping
 */
@Getter
@Setter
@AllArgsConstructor
public class AppActionDto {

    private Long eventId;

    private AppOptEnum event;
}
