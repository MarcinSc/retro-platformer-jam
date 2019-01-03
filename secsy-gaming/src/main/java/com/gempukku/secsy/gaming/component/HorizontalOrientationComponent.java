package com.gempukku.secsy.gaming.component;

import com.gempukku.secsy.entity.Component;
import com.gempukku.secsy.gaming.editor.EditableWith;
import com.gempukku.secsy.gaming.editor.component.HorizontalOrientationEditor;

@EditableWith(HorizontalOrientationEditor.class)
public interface HorizontalOrientationComponent extends Component {
    boolean isFacingRight();

    void setFacingRight(boolean facingRight);
}
