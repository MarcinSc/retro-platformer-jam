package com.gempukku.secsy.gaming.physics.basic2d.activate;

import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.gaming.input.EntityPerformedAction;
import com.gempukku.secsy.gaming.physics.basic2d.Basic2dPhysics;
import com.gempukku.secsy.gaming.physics.basic2d.SensorContactBegin;
import com.google.common.base.Predicate;

import javax.annotation.Nullable;

@RegisterSystem(profiles = "activateWithSensor")
public class ActivateWithSensorSystem {
    @Inject
    private Basic2dPhysics physics;

    @ReceiveEvent
    public void contactStart(SensorContactBegin contactBegin, EntityRef entity, ActivatesWithSensorComponent activatesWithSensor) {
        if (contactBegin.getSensorType().equals(activatesWithSensor.getSensorType())) {
            EntityRef sensorTrigger = contactBegin.getSensorTrigger();
            if (sensorTrigger.hasComponent(ActivatedBySensorComponent.class)) {
                sensorTrigger.send(new EntityActivated(entity));
            }
        }
    }

    @ReceiveEvent
    public void performsAction(EntityPerformedAction action, EntityRef entity, ActionActivatesSensorComponent actionActivatesSensor) {
        for (EntityRef activated : physics.getContactsForSensor(entity, actionActivatesSensor.getSensorType(),
                new Predicate<EntityRef>() {
                    @Override
                    public boolean apply(@Nullable EntityRef entityRef) {
                        return entityRef.hasComponent(ActivatedByActionWithSensorComponent.class);
                    }
                })) {
            activated.send(new EntityActivated(entity));
        }
    }
}
