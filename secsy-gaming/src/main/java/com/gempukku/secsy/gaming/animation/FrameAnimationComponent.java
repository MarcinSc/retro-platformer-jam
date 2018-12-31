package com.gempukku.secsy.gaming.animation;

import com.gempukku.secsy.entity.Component;
import com.gempukku.secsy.entity.component.Container;

import java.util.Map;

public interface FrameAnimationComponent extends Component {
    @Container(AnimationFrames.class)
    Map<String, AnimationFrames> getAnimations();

    AnimationResolver getAnimationResolver();

    void setAnimationResolver(AnimationResolver animationResolver);
}
