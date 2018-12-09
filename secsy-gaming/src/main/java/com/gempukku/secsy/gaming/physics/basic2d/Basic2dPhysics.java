package com.gempukku.secsy.gaming.physics.basic2d;

import com.gempukku.secsy.entity.EntityRef;
import com.google.common.base.Predicate;

public interface Basic2dPhysics {
    Iterable<EntityRef> getSensorTriggersFor(EntityRef sensorEntity, String sensorType,
                                             float minX, float maxX, float minY, float maxY,
                                             Predicate<EntityRef> sensorTriggerPredicate);
    Iterable<EntityRef> getContactsForSensor(EntityRef sensorEntity, String type, Predicate<EntityRef> sensorTriggerPredicate);

    Iterable<EntityRef> getSensorEntitiesContactedBy(EntityRef sensorTriggerEntity, String sensorType);
}
