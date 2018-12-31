package com.gempukku.secsy.gaming.animation;

import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.gaming.rendering.sprite.AnimatedSpriteProvider;
import com.gempukku.secsy.gaming.time.TimeManager;

@RegisterSystem(profiles = "frameAnimation", shared = AnimatedSpriteProvider.class)
public class FrameAnimationSystem implements AnimatedSpriteProvider {
    @Inject
    private TimeManager timeManager;

    @Override
    public String getSpriteForEntity(EntityRef entity) {
        long time = timeManager.getTime();

        FrameAnimationComponent frameAnimation = entity.getComponent(FrameAnimationComponent.class);
        AnimationResolver animationResolver = frameAnimation.getAnimationResolver();
        AnimationResolver.AnimationFrame animationFrame = animationResolver.getAnimationFrame(time);
        return frameAnimation.getAnimations().get(animationFrame.getAnimation()).getAnimationFrame(animationFrame.getAnimationIndex());
    }
}
