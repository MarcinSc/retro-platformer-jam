package com.gempukku.secsy.gaming.animation;

import java.util.Map;

public interface AnimationResolver {
    AnimationFrame getAnimationFrame(Map<String, AnimationFrames> animationFramesMap, long currentTime);

    interface AnimationFrame {
        String getAnimation();

        long getAnimationIndex();
    }
}
