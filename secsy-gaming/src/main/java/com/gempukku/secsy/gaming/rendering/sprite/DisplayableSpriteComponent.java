package com.gempukku.secsy.gaming.rendering.sprite;

import com.gempukku.secsy.gaming.component.Bounds2DComponent;

public interface DisplayableSpriteComponent extends Bounds2DComponent {
    float getPriority();

    void setPriority(float priority);
}
