package com.gempukku.secsy.gaming.component;

import com.gempukku.secsy.entity.Component;

public interface Bounds2DComponent extends Component {
    float getLeftPerc();

    void setLeftPerc(float leftPerc);

    float getRightPerc();

    void setRightPerc(float rightPerc);

    float getUpPerc();

    void setUpPerc(float upPerc);

    float getDownPerc();

    void setDownPerc(float downPerc);
}
