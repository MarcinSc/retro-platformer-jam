package com.gempukku.retro.logic.ai.task;

import com.gempukku.retro.logic.ai.AINotifyAfterComponent;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.gaming.ai.AITask;
import com.gempukku.secsy.gaming.ai.AITaskResult;
import com.gempukku.secsy.gaming.ai.EntityRefReference;
import com.gempukku.secsy.gaming.ai.builder.TaskBuilder;
import com.gempukku.secsy.gaming.ai.task.AbstractAITask;

import java.util.Map;

public class WaitTask extends AbstractAITask<EntityRefReference> {
    private static final String TIME_IS_UP = "timeIsUp";

    private final long time;

    public WaitTask(String id, AITask parent, TaskBuilder<EntityRefReference> taskBuilder, Map<String, Object> taskData) {
        super(id, parent, taskBuilder, taskData);
        time = ((Number) taskData.get("time")).longValue();
    }

    @Override
    public AITaskResult startTask(EntityRefReference reference) {
        EntityRef entityRef = reference.getEntityRef();
        AINotifyAfterComponent component = entityRef.createComponent(AINotifyAfterComponent.class);
        component.setNotifyAfter(time);
        entityRef.saveChanges();
        return AITaskResult.RUNNING;
    }

    @Override
    public AITaskResult continueTask(EntityRefReference reference) {
        if (reference.getValue(getId(), TIME_IS_UP, Boolean.class)) {
            reference.removeValue(getId(), TIME_IS_UP);
            reference.storeValues();

            EntityRef entityRef = reference.getEntityRef();
            entityRef.removeComponents(AINotifyAfterComponent.class);
            entityRef.saveChanges();

            return AITaskResult.SUCCESS;
        }
        return AITaskResult.RUNNING;
    }

    @Override
    public void cancelTask(EntityRefReference reference) {
        reference.removeValue(getId(), TIME_IS_UP);
        reference.storeValues();

        EntityRef entityRef = reference.getEntityRef();
        entityRef.removeComponents(AINotifyAfterComponent.class);
        entityRef.saveChanges();
    }

    public void timeIsUp(EntityRefReference entityRefReference) {
        entityRefReference.setValue(getId(), TIME_IS_UP, true);
        entityRefReference.storeValues();
    }
}
