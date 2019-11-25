package net.atayun.bazooka.deploy.biz.v2.service.app.step.example;

import lombok.Getter;

/**
 * @author Ping
 */
public abstract class Pen {

    @Getter
    private Color color;

    public void setColor(Color color) {
        this.color = color;
    }

    public abstract void draw();
}
