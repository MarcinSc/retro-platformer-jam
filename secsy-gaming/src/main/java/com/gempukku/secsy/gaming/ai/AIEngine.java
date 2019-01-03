package com.gempukku.secsy.gaming.ai;

import com.gempukku.secsy.entity.EntityRef;

public interface AIEngine {
    boolean hasBehavior(String name);

    Iterable<AITask<EntityRefReference>> getRunningTasks(EntityRef entityRef);

    <T extends AITask<EntityRefReference>> Iterable<T> getRunningTasksOfType(EntityRef entityRef, Class<T> clazz);

    EntityRefReference getReference(EntityRef entityRef);
}
