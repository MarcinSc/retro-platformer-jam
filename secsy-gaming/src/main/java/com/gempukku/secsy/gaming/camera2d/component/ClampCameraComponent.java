package com.gempukku.secsy.gaming.camera2d.component;

import com.gempukku.secsy.entity.Component;
import com.gempukku.secsy.gaming.editor.generic.EditorFields;
import com.gempukku.secsy.gaming.editor.generic.EditorName;
import com.gempukku.secsy.gaming.editor.generic.type.EditorField;

@EditorFields({"minX", "maxX", "minY", "maxY"})
@EditorName("Clamp Camera")
public interface ClampCameraComponent extends Component {
    @EditorField("Min X")
    float getMinX();

    void setMinX(float minX);

    @EditorField("Max X")
    float getMaxX();

    void setMaxX(float maxX);

    @EditorField("Min Y")
    float getMinY();

    void setMinY(float minY);

    @EditorField("Max Y")
    float getMaxY();

    void setMaxY(float maxY);
}
