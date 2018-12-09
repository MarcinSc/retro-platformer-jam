package com.gempukku.retro.logic.ai;

import com.gempukku.secsy.entity.Component;

public interface AIWaitsForOpponentInRangeComponent extends Component {
    float getHeight();

    void setHeight(float height);

    float getRange();

    void setRange(float range);
}
