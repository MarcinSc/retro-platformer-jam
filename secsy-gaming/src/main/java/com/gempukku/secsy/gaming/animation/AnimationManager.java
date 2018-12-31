package com.gempukku.secsy.gaming.animation;

import com.gempukku.secsy.entity.EntityRef;

public interface AnimationManager {
    void queueAnimation(EntityRef entity, String animation);

    void setAnimation(EntityRef entity, String animation);
}
