package com.gempukku.retro.logic.room;

import com.gempukku.secsy.entity.Component;

public interface RoomComponent extends Component {
    String getRoom();

    void setRoom(String room);

    float getX();

    void setX(float x);

    float getY();

    void setY(float y);
}
