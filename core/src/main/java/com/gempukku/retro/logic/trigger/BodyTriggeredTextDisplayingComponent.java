package com.gempukku.retro.logic.trigger;

import com.gempukku.secsy.entity.Component;
import com.gempukku.secsy.gaming.editor.EditableWith;

@EditableWith(BodyTriggeredTextDisplayingEditor.class)
public interface BodyTriggeredTextDisplayingComponent extends Component {
    String getDisplayText();

    void setDisplayText(String displayText);

    float getX();

    float getY();
}
