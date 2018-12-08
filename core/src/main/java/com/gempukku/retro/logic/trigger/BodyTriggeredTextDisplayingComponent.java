package com.gempukku.retro.logic.trigger;

import com.gempukku.secsy.entity.Component;

public interface BodyTriggeredTextDisplayingComponent extends Component {
    String getDisplayText();

    float getX();

    float getY();
}
