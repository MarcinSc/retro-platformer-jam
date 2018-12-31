package com.gempukku.secsy.gaming.animation;

import com.gempukku.secsy.entity.Component;
import com.gempukku.secsy.entity.component.Container;
import com.gempukku.secsy.entity.component.DefaultValue;

import java.util.Map;

public interface FrameAnimationComponent extends Component {
    @Container(AnimationFrames.class)
    Map<String, AnimationFrames> getAnimations();

    @DefaultValue("idle")
    AnimationResolver getAnimationResolver();

    void setAnimationResolver(AnimationResolver animationResolver);
}
