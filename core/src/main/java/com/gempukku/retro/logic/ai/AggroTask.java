package com.gempukku.retro.logic.ai;

import com.gempukku.retro.logic.combat.CausesVulnerabilityComponent;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.gaming.ai.AITask;
import com.gempukku.secsy.gaming.ai.AITaskResult;
import com.gempukku.secsy.gaming.ai.EntityRefReference;
import com.gempukku.secsy.gaming.ai.builder.TaskBuilder;
import com.gempukku.secsy.gaming.ai.task.AbstractAITask;

import java.util.Map;

public class AggroTask extends AbstractAITask<EntityRefReference> {
    private int damageAmount;

    public AggroTask(String id, AITask parent, TaskBuilder<EntityRefReference> taskBuilder, Map<String, Object> taskData) {
        super(id, parent, taskBuilder, taskData);
        damageAmount = ((Number) taskData.get("damageAmount")).intValue();
    }

    @Override
    public AITaskResult startTask(EntityRefReference reference) {
        EntityRef entityRef = reference.getEntityRef();
        CausesVulnerabilityComponent vulnerability = entityRef.createComponent(CausesVulnerabilityComponent.class);
        vulnerability.setDamageAmount(damageAmount);
        entityRef.saveChanges();
        return AITaskResult.SUCCESS;
    }

    @Override
    public AITaskResult continueTask(EntityRefReference reference) {
        return null;
    }

    @Override
    public void cancelTask(EntityRefReference reference) {

    }
}
