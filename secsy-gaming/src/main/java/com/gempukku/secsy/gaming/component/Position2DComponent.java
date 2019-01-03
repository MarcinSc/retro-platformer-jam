package com.gempukku.secsy.gaming.component;

import com.gempukku.secsy.entity.Component;
import com.gempukku.secsy.gaming.editor.EditableWith;
import com.gempukku.secsy.gaming.editor.component.Position2DEditor;

@EditableWith(Position2DEditor.class)
public interface Position2DComponent extends Component {
    float getX();

    void setX(float x);

    float getY();

    void setY(float y);
}
