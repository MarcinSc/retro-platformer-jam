package com.gempukku.retro.render;

import com.gempukku.secsy.gaming.rendering.sprite.SpriteComponent;

public interface BobbingSpriteComponent extends SpriteComponent {
    float getBobbingAmplitude();

    void setBobbingAmplitude(float bobbingAmplitude);
}
