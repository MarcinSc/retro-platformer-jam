package com.gempukku.retro.logic.trigger;

import com.gempukku.secsy.entity.Component;

public interface BodyTriggeredLoadRoomComponent extends Component {
    String getRoomPath();

    float getX();

    float getY();
}
