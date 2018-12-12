package com.gempukku.secsy.gaming.physics.basic2d;

public class Sensor {
    public final int entityId;
    public final String type;
    public float left;
    public float right;
    public float down;
    public float up;

    public Sensor(int entityId, String type, float left, float right, float down, float up) {
        this.entityId = entityId;
        this.type = type;
        this.left = left;
        this.right = right;
        this.down = down;
        this.up = up;
    }
}
