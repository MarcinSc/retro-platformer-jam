package com.gempukku.secsy.gaming.rendering.sprite;

import com.gempukku.secsy.gaming.component.Bounds2DComponent;
import com.gempukku.secsy.gaming.component.TimedEffectComponent;

public interface SpriteComponent extends Bounds2DComponent, TimedEffectComponent {
    String getFileName();
    void setFileName(String fileName);

    float getPriority();

    void setPriority(float priority);

    float getBobbingAmplitude();

    void setBobbingAmplitude(float bobbingAmplitude);
}
