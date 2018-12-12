package com.gempukku.secsy.gaming.component;

public class PositionResolver {
    public static float getLeft(Size2DComponent size, Bounds2DComponent bounds) {
        float leftPerc = bounds.getLeftPerc();
        return getLeft(size, leftPerc);
    }

    public static float getLeft(Size2DComponent size, float leftPerc) {
        float width = size.getWidth();
        return width * (leftPerc - size.getAnchorX());
    }

    public static float getRight(Size2DComponent size, Bounds2DComponent bounds) {
        float rightPerc = bounds.getRightPerc();
        return getRight(size, rightPerc);
    }

    public static float getRight(Size2DComponent size, float rightPerc) {
        float width = size.getWidth();
        return width * ((1 - size.getAnchorX()) + rightPerc);
    }

    public static float getDown(Size2DComponent size, Bounds2DComponent bounds) {
        float downPerc = bounds.getDownPerc();
        return getDown(size, downPerc);
    }

    public static float getDown(Size2DComponent size, float downPerc) {
        float height = size.getHeight();
        return height * (downPerc - size.getAnchorY());
    }

    public static float getUp(Size2DComponent size, Bounds2DComponent bounds) {
        float upPerc = bounds.getUpPerc();
        return getUp(size, upPerc);
    }

    public static float getUp(Size2DComponent size, float upPerc) {
        float height = size.getHeight();
        return height * ((1 - size.getAnchorY()) + upPerc);
    }

    public static float getWidth(Size2DComponent size, Bounds2DComponent bounds) {
        float width = size.getWidth();
        return width * (1 + bounds.getRightPerc() - bounds.getLeftPerc());
    }

    public static float getHeight(Size2DComponent size, Bounds2DComponent bounds) {
        float height = size.getHeight();
        return height * (1 + bounds.getUpPerc() - bounds.getDownPerc());
    }
}
