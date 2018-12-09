package com.gempukku.secsy.gaming.physics.basic2d;

import com.gempukku.secsy.entity.event.Event;

public class EntityMoved extends Event {
    private boolean hadCollision;
    private float oldX;
    private float oldY;
    private float newX;
    private float newY;

    public EntityMoved(boolean hadCollision, float oldX, float oldY, float newX, float newY) {
        this.hadCollision = hadCollision;
        this.oldX = oldX;
        this.oldY = oldY;
        this.newX = newX;
        this.newY = newY;
    }

    public EntityMoved(float oldX, float oldY, float newX, float newY) {
        this(false, oldX, oldY, newX, newY);
    }

    public float getOldX() {
        return oldX;
    }

    public float getOldY() {
        return oldY;
    }

    public float getNewX() {
        return newX;
    }

    public float getNewY() {
        return newY;
    }

    public boolean hadCollision() {
        return hadCollision;
    }
}
