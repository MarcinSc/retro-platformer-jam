package com.gempukku.secsy.gaming.component;

import com.gempukku.secsy.entity.Component;
import com.gempukku.secsy.gaming.editor.generic.EditorFields;
import com.gempukku.secsy.gaming.editor.generic.EditorName;
import com.gempukku.secsy.gaming.editor.generic.type.EditorField;

@EditorFields({"width", "height", "anchorX", "anchorY"})
@EditorName("Size")
public interface Size2DComponent extends Component {
    float getWidth();

    void setWidth(float width);

    float getHeight();

    void setHeight(float height);

    // Percentage of width for anchor, i.e. if anchor is on left, it's 0, if on right 1, if in the middle 0.5
    @EditorField("Anchor X")
    float getAnchorX();

    void setAnchorX(float anchorX);

    // Percentage of height for anchor, i.e. if anchor is on the bottom, it's 0, if on top 1, if in the middle 0.5
    @EditorField("Anchor Y")
    float getAnchorY();

    void setAnchorY(float anchorY);
}
