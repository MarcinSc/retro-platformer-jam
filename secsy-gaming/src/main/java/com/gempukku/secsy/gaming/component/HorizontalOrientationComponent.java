package com.gempukku.secsy.gaming.component;

import com.gempukku.secsy.entity.Component;
import com.gempukku.secsy.gaming.editor.generic.EditorName;
import com.gempukku.secsy.gaming.editor.generic.type.EditorField;

@EditorName("Orientation")
public interface HorizontalOrientationComponent extends Component {
    @EditorField("Face right")
    boolean isFacingRight();

    void setFacingRight(boolean facingRight);
}
