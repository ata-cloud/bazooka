package net.atayun.bazooka.rms.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * @author Ping
 */
@Getter
@Setter
public class NodeAvailableResourceDto {

    private Map<Long, ResourceSumDto> availableResource;

    public NodeAvailableResourceDto() {
    }

    public NodeAvailableResourceDto(Map<Long, ResourceSumDto> availableResource) {
        this.availableResource = availableResource;
    }
}
