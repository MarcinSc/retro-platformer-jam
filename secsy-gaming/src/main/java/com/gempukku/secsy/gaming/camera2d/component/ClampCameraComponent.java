package com.gempukku.secsy.gaming.camera2d.component;

import com.gempukku.secsy.entity.Component;

public interface ClampCameraComponent extends Component {
    float getMinX();

    void setMinX(float minX);

    float getMaxX();

    void setMaxX(float maxX);

    float getMinY();

    void setMinY(float minY);

    float getMaxY();

    void setMaxY(float maxY);
}
