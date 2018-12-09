package com.gempukku.retro.logic.trigger;

import com.gempukku.retro.logic.room.LoadRoom;
import com.gempukku.retro.model.PlayerComponent;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.entity.game.GameEntityProvider;
import com.gempukku.secsy.gaming.physics.basic2d.SensorContactBegin;

@RegisterSystem
public class BodyTriggeredLoadRoomSystem extends AbstractLifeCycleSystem {
    @Inject
    private GameEntityProvider gameEntityProvider;

    @ReceiveEvent
    public void contactStart(SensorContactBegin contactBegin, EntityRef entity, PlayerComponent player) {
        if (contactBegin.getSensorType().equals("body")) {
            EntityRef sensorTrigger = contactBegin.getSensorTrigger();
            if (sensorTrigger.hasComponent(BodyTriggeredLoadRoomComponent.class)) {
                BodyTriggeredLoadRoomComponent component = sensorTrigger.getComponent(BodyTriggeredLoadRoomComponent.class);
                gameEntityProvider.getGameEntity().send(new LoadRoom(component.getRoomPath(), component.getX(), component.getY()));
            }
        }
    }
}
