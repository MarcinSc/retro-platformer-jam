package com.gempukku.secsy.gaming.ai.task.wait;

import com.gempukku.secsy.entity.Component;

public interface AINotifyAfterComponent extends Component {
    long getNotifyAfter();

    void setNotifyAfter(long notifyAfter);

    long getStartTime();

    void setStartTime(long startTime);
}
