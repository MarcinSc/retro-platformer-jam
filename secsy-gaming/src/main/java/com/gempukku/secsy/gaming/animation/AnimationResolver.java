package com.gempukku.secsy.gaming.animation;

public interface AnimationResolver {
    AnimationFrame getAnimationFrame(long currentTime);

    interface AnimationFrame {
        String getAnimation();

        long getAnimationIndex();
    }
}
