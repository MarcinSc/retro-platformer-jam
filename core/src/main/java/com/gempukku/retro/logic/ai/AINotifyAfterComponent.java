package com.gempukku.retro.logic.ai;

import com.gempukku.secsy.entity.Component;

public interface AINotifyAfterComponent extends Component {
    long getNotifyAfter();

    void setNotifyAfter(long notifyAfter);

    long getStartTime();

    void setStartTime(long startTime);
}
