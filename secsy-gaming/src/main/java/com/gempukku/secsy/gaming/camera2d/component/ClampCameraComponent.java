package com.gempukku.secsy.gaming.camera2d.component;

import com.gempukku.secsy.entity.Component;
import com.gempukku.secsy.gaming.editor.EditableWith;
import com.gempukku.secsy.gaming.editor.component.ClampCameraEditor;

@EditableWith(ClampCameraEditor.class)
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
