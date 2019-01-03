package com.gempukku.retro.logic.trigger.activate;

import com.gempukku.retro.logic.room.LoadRoom;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.entity.game.GameEntityProvider;
import com.gempukku.secsy.gaming.physics.basic2d.activate.EntityActivated;

@RegisterSystem
public class LoadRoomOnActivateSystem {
    @Inject
    private GameEntityProvider gameEntityProvider;

    @ReceiveEvent
    public void loadRoomOnActivate(EntityActivated entityActivated, EntityRef entity, LoadRoomOnActivateComponent loadRoom) {
        String roomPath = loadRoom.getRoomPath();
        gameEntityProvider.getGameEntity().send(new LoadRoom(roomPath, loadRoom.getSpawnId()));
    }
}
