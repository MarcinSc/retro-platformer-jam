package com.gempukku.secsy.gaming.physics.basic2d;

public class SensorTrigger {
    public final int entityId;
    public float left;
    public float right;
    public float down;
    public float up;

    public float x;
    public float y;

    public boolean isAABB;

    public float[] nonAABBVertices;

    public SensorTrigger(int entityId, float left, float right, float down, float up) {
        this.entityId = entityId;
        this.left = left;
        this.right = right;
        this.down = down;
        this.up = up;
        this.isAABB = true;
    }

    public SensorTrigger(int entityId, float left, float right, float down, float up, float[] vertices) {
        this.entityId = entityId;
        this.isAABB = false;
        this.nonAABBVertices = vertices;
    }

    public void updatePosition(float x, float y) {
        this.x = x;
        this.y = y;
    }
}
