package com.gempukku.retro.logic.ai;

import com.gempukku.secsy.gaming.ai.AITask;
import com.gempukku.secsy.gaming.ai.AITaskResult;
import com.gempukku.secsy.gaming.ai.EntityRefReference;
import com.gempukku.secsy.gaming.ai.builder.TaskBuilder;
import com.gempukku.secsy.gaming.ai.task.AbstractAITask;

import java.util.Map;

public class WaitForAnyDamageTask extends AbstractAITask<EntityRefReference> {
    private static final String DAMAGE_DONE = "damageDone";

    public WaitForAnyDamageTask(String id, AITask parent, TaskBuilder<EntityRefReference> taskBuilder, Map<String, Object> taskData) {
        super(id, parent, taskBuilder, taskData);
    }

    @Override
    public AITaskResult startTask(EntityRefReference reference) {
        return AITaskResult.RUNNING;
    }

    @Override
    public AITaskResult continueTask(EntityRefReference reference) {
        if (reference.getValue(getId(), DAMAGE_DONE, Boolean.class) != null) {
            reference.removeValue(getId(), DAMAGE_DONE);
            reference.storeValues();

            return AITaskResult.SUCCESS;
        }
        return AITaskResult.RUNNING;
    }

    @Override
    public void cancelTask(EntityRefReference reference) {
        reference.removeValue(getId(), DAMAGE_DONE);
        reference.storeValues();
    }

    public void damageDone(EntityRefReference reference) {
        reference.setValue(getId(), DAMAGE_DONE, true);
        reference.storeValues();
    }
}
