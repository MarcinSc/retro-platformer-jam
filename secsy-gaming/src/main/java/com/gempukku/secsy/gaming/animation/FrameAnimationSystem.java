package com.gempukku.secsy.gaming.animation;

import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.gaming.rendering.sprite.AnimatedSpriteProvider;
import com.gempukku.secsy.gaming.time.TimeManager;

import java.util.Map;

@RegisterSystem(profiles = "frameAnimation", shared = {AnimatedSpriteProvider.class, AnimationManager.class})
public class FrameAnimationSystem implements AnimatedSpriteProvider, AnimationManager {
    @Inject
    private TimeManager timeManager;

    @Override
    public String getSpriteForEntity(EntityRef entity) {
        long time = timeManager.getTime();

        FrameAnimationComponent frameAnimation = entity.getComponent(FrameAnimationComponent.class);
        Map<String, AnimationFrames> animations = frameAnimation.getAnimations();
        AnimationResolver animationResolver = frameAnimation.getAnimationResolver();
        AnimationResolver.AnimationFrame animationFrame = animationResolver.getAnimationFrame(animations, time);

        return frameAnimation.getAnimations().get(animationFrame.getAnimation()).getAnimationFrame(animationFrame.getAnimationIndex());
    }

    @Override
    public void queueAnimation(EntityRef entity, String animation) {
        FrameAnimationComponent frameAnimation = entity.getComponent(FrameAnimationComponent.class);
        ChainedAnimationResolver animationResolver = (ChainedAnimationResolver) frameAnimation.getAnimationResolver();
        String lastAnimation = animationResolver.getLastAnimation();

        if (!lastAnimation.equals(animation)) {
            Map<String, AnimationFrames> animations = frameAnimation.getAnimations();
            String transitionAnimation = lastAnimation + "_" + animation;
            if (animations.containsKey(transitionAnimation))
                animationResolver.appendAnimation(transitionAnimation);
            animationResolver.appendAnimation(animation);
        }
        entity.saveChanges();
    }

    @Override
    public void setAnimation(EntityRef entity, String animation) {
        FrameAnimationComponent frameAnimation = entity.getComponent(FrameAnimationComponent.class);
        ChainedAnimationResolver animationResolver = (ChainedAnimationResolver) frameAnimation.getAnimationResolver();
        animationResolver.replaceAnimations(animation);
        entity.saveChanges();
    }
}
