package com.gempukku.secsy.gaming.physics.basic2d;

public class Obstacle {
    public final int entityId;
    public float left;
    public float right;
    public float down;
    public float up;

    public float oldX;
    public float oldY;
    public float newX;
    public float newY;

    public boolean isAABB;

    public float[] nonAABBVertices;

    public Obstacle(int entityId, float left, float right, float down, float up) {
        this.entityId = entityId;
        this.left = left;
        this.right = right;
        this.down = down;
        this.up = up;
        this.isAABB = true;
    }

    public Obstacle(int entityId, float left, float right, float down, float up, float[] vertices) {
        this(entityId, left, right, down, up);
        this.isAABB = false;
        this.nonAABBVertices = vertices;
    }

    public void updatePositions(float oldX, float oldY, float newX, float newY) {
        this.oldX = oldX;
        this.oldY = oldY;
        this.newX = newX;
        this.newY = newY;
    }
}
