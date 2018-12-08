package com.gempukku.retro.logic.trigger.activate;

import com.gempukku.retro.logic.activate.EntityActivated;
import com.gempukku.retro.logic.room.LoadRoom;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.entity.game.GameEntityProvider;

@RegisterSystem
public class LoadLevelOnActivateSystem {
    @Inject
    private GameEntityProvider gameEntityProvider;

    @ReceiveEvent
    public void loadLevelOnActivate(EntityActivated entityActivated, EntityRef entity, LoadRoomOnActivateComponent loadLevel) {
        String roomPath = loadLevel.getRoomPath();
        gameEntityProvider.getGameEntity().send(new LoadRoom(roomPath));
    }
}
