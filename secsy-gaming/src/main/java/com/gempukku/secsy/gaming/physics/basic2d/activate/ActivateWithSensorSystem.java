package com.gempukku.secsy.gaming.physics.basic2d.activate;

import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.gaming.physics.basic2d.SensorContactBegin;

@RegisterSystem(profiles = "activateWithSensor")
public class ActivateWithSensorSystem extends AbstractLifeCycleSystem {
    @ReceiveEvent
    public void contactStart(SensorContactBegin contactBegin, EntityRef entity, ActivatesWithSensorComponent activatesWithSensor) {
        if (contactBegin.getSensorType().equals(activatesWithSensor.getSensorName())) {
            EntityRef sensorTrigger = contactBegin.getSensorTrigger();
            if (sensorTrigger.hasComponent(ActivatedWithSensorComponent.class)) {
                sensorTrigger.send(new EntityActivated());
            }
        }
    }
}
