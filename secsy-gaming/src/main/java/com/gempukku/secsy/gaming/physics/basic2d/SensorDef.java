package com.gempukku.secsy.gaming.physics.basic2d;

public class SensorDef {
    public final String type;
    public final float left;
    public final float right;
    public final float down;
    public final float up;

    public SensorDef(String type, float left, float right, float down, float up) {
        this.type = type;
        this.left = left;
        this.right = right;
        this.down = down;
        this.up = up;
    }
}
