package com.gempukku.secsy.gaming.component;

import com.gempukku.secsy.entity.Component;
import com.gempukku.secsy.gaming.editor.generic.type.EditorField;

public interface Bounds2DComponent extends Component {
    @EditorField("Left %")
    float getLeftPerc();

    void setLeftPerc(float leftPerc);

    @EditorField("Right %")
    float getRightPerc();

    void setRightPerc(float rightPerc);

    @EditorField("Up %")
    float getUpPerc();

    void setUpPerc(float upPerc);

    @EditorField("Down %")
    float getDownPerc();

    void setDownPerc(float downPerc);
}
