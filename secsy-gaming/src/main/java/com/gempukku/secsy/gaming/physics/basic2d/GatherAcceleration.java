package com.gempukku.secsy.gaming.physics.basic2d;

import com.gempukku.secsy.entity.event.Event;

public class GatherAcceleration extends Event {
    private float accelerationX;
    private float accelerationY;
    private long physicsTime;

    public GatherAcceleration(long physicsTime) {
        this.physicsTime = physicsTime;
    }

    public long getPhysicsTime() {
        return physicsTime;
    }

    public void addAcceleration(float accelerationX, float accelerationY) {
        this.accelerationX += accelerationX;
        this.accelerationY += accelerationY;
    }

    public float getAccelerationX() {
        return accelerationX;
    }

    public float getAccelerationY() {
        return accelerationY;
    }
}
