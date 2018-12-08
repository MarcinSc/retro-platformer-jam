package com.gempukku.retro.model;

import com.gempukku.secsy.gaming.component.Bounds2DComponent;

public interface PlatformComponent extends Bounds2DComponent {
    boolean isHorizontal();

    String getTextureAtlasId();

    String getBeginningImage();

    String getCenterImage();

    String getEndingImage();
}
