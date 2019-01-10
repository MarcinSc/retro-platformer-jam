package com.gempukku.retro.logic.trigger;

import com.gempukku.secsy.entity.Component;
import com.gempukku.secsy.gaming.editor.generic.EditorName;
import com.gempukku.secsy.gaming.editor.generic.type.EditorField;

@EditorName("Display Text Body Trigger")
public interface BodyTriggeredTextDisplayingComponent extends Component {
    @EditorField("Text")
    String getDisplayText();

    void setDisplayText(String displayText);

    float getX();

    float getY();
}
