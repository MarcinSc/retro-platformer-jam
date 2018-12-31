package com.gempukku.secsy.gaming.animation;

import java.util.Deque;
import java.util.LinkedList;

public class ChainedAnimationResolver implements AnimationResolver {
    private Deque<DefaultAnimationResolver> animationResolvers = new LinkedList<DefaultAnimationResolver>();

    public ChainedAnimationResolver(DefaultAnimationResolver animationResolver) {
        animationResolvers.push(animationResolver);
    }

    @Override
    public AnimationFrame getAnimationFrame(long animationIndex) {
        return null;
    }
}
