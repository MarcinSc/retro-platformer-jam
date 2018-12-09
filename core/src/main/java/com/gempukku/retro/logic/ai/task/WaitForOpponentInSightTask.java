package com.gempukku.retro.logic.ai.task;

import com.gempukku.retro.logic.ai.AIWaitsForOpponentInRangeComponent;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.gaming.ai.AITask;
import com.gempukku.secsy.gaming.ai.AITaskResult;
import com.gempukku.secsy.gaming.ai.EntityRefReference;
import com.gempukku.secsy.gaming.ai.builder.TaskBuilder;
import com.gempukku.secsy.gaming.ai.task.AbstractAITask;

import java.util.Map;

public class WaitForOpponentInSightTask extends AbstractAITask<EntityRefReference> {
    private static final String FOUND_ONE = "foundOne";
    private final float height;
    private final float range;

    public WaitForOpponentInSightTask(String id, AITask parent, TaskBuilder<EntityRefReference> taskBuilder, Map<String, Object> taskData) {
        super(id, parent, taskBuilder, taskData);
        height = ((Number) taskData.get("height")).floatValue();
        range = ((Number) taskData.get("range")).floatValue();
    }

    @Override
    public AITaskResult startTask(EntityRefReference reference) {
        EntityRef entityRef = reference.getEntityRef();
        AIWaitsForOpponentInRangeComponent wait = entityRef.createComponent(AIWaitsForOpponentInRangeComponent.class);
        wait.setHeight(height);
        wait.setRange(range);
        entityRef.saveChanges();
        return AITaskResult.RUNNING;
    }

    @Override
    public AITaskResult continueTask(EntityRefReference reference) {
        if (reference.getValue(getId(), FOUND_ONE, Boolean.class) != null) {
            reference.removeValue(getId(), FOUND_ONE);
            reference.storeValues();

            EntityRef entityRef = reference.getEntityRef();
            entityRef.removeComponents(AIWaitsForOpponentInRangeComponent.class);
            entityRef.saveChanges();

            return AITaskResult.SUCCESS;
        }
        return AITaskResult.RUNNING;
    }

    @Override
    public void cancelTask(EntityRefReference reference) {
        reference.removeValue(getId(), FOUND_ONE);
        reference.storeValues();

        EntityRef entityRef = reference.getEntityRef();
        entityRef.removeComponents(AIWaitsForOpponentInRangeComponent.class);
        entityRef.saveChanges();
    }

    public void opponentInSight(EntityRefReference entityRefReference) {
        entityRefReference.setValue(getId(), FOUND_ONE, true);
        entityRefReference.storeValues();
    }
}
