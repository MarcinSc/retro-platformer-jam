package com.gempukku.retro.logic.ai;

import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.gaming.ai.AIEngine;
import com.gempukku.secsy.gaming.ai.movement.AIApplyMovementIfPossibleComponent;
import com.gempukku.secsy.gaming.ai.movement.AICantMove;
import com.gempukku.secsy.gaming.physics.basic2d.ObstacleComponent;
import com.gempukku.secsy.gaming.physics.basic2d.SensorContactBegin;

@RegisterSystem
public class MonitorIfCanMove {
    @Inject
    private AIEngine aiEngine;

    @ReceiveEvent
    public void sensorStart(SensorContactBegin contactBegin, EntityRef aiEntity, AIApplyMovementIfPossibleComponent aiMovement) {
        if (contactBegin.getSensorType().equals("obstacleSensor")) {
            if (contactBegin.getSensorTrigger().hasComponent(ObstacleComponent.class)) {
                aiEntity.send(new AICantMove());
            }
        }
    }
}
