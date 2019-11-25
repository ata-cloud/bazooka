package net.atayun.bazooka.deploy.biz.v2.service.app.step;

import net.atayun.bazooka.deploy.biz.v2.service.app.platform.Platform;

/**
 * @author Ping
 */
public abstract class StepTool4Platform implements Step {

    protected Platform platform;

    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

}
