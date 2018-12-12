package com.gempukku.retro.logic.ai;

import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.gaming.ai.AIEngine;
import com.gempukku.secsy.gaming.ai.task.movement.AIApplyMovementIfPossibleComponent;
import com.gempukku.secsy.gaming.ai.task.movement.AICantMove;
import com.gempukku.secsy.gaming.component.GroundComponent;
import com.gempukku.secsy.gaming.component.HorizontalOrientationComponent;
import com.gempukku.secsy.gaming.physics.basic2d.ObstacleComponent;
import com.gempukku.secsy.gaming.physics.basic2d.SensorContactBegin;
import com.gempukku.secsy.gaming.physics.basic2d.SensorContactEnd;

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

    @ReceiveEvent
    public void cantMove(SensorContactEnd sensorContactEnd, EntityRef entity, AIApplyMovementIfPossibleComponent aiApplyMovement,
                         HorizontalOrientationComponent horizontalOrientation) {
        boolean right = horizontalOrientation.isFacingRight();
        if (right && sensorContactEnd.getSensorType().equals("rightGroundSensor")
                && sensorContactEnd.getSensorTrigger().hasComponent(GroundComponent.class)) {
            entity.send(new AICantMove());
        } else if (!right && sensorContactEnd.getSensorType().equals("leftGroundSensor")
                && sensorContactEnd.getSensorTrigger().hasComponent(GroundComponent.class)) {
            entity.send(new AICantMove());
        }
    }

}
