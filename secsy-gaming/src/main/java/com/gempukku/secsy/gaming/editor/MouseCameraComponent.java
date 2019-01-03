package com.gempukku.secsy.gaming.editor;

import com.gempukku.secsy.entity.Component;

public interface MouseCameraComponent extends Component {
    float getX();

    void setX(float x);

    float getY();

    void setY(float y);

    float getViewportWidth();

    void setViewportWidth(float viewportWidth);

    float getViewportHeight();

    void setViewportHeight(float viewportHeight);
}
