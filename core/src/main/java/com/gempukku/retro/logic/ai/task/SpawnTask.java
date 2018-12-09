package com.gempukku.retro.logic.ai.task;

import com.gempukku.retro.logic.spawn.SpawnerComponent;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.gaming.ai.AITask;
import com.gempukku.secsy.gaming.ai.AITaskResult;
import com.gempukku.secsy.gaming.ai.EntityRefReference;
import com.gempukku.secsy.gaming.ai.builder.TaskBuilder;
import com.gempukku.secsy.gaming.ai.task.AbstractAITask;

import java.util.Map;

public class SpawnTask extends AbstractAITask<EntityRefReference> {
    private final String prefab;

    public SpawnTask(String id, AITask parent, TaskBuilder<EntityRefReference> taskBuilder, Map<String, Object> taskData) {
        super(id, parent, taskBuilder, taskData);
        prefab = (String) taskData.get("prefab");
    }

    @Override
    public AITaskResult startTask(EntityRefReference reference) {
        EntityRef entityRef = reference.getEntityRef();
        SpawnerComponent spawner = entityRef.createComponent(SpawnerComponent.class);
        spawner.setPrefab(prefab);
        entityRef.saveChanges();
        return AITaskResult.RUNNING;
    }

    @Override
    public AITaskResult continueTask(EntityRefReference reference) {
        EntityRef entityRef = reference.getEntityRef();
        entityRef.removeComponents(SpawnerComponent.class);
        entityRef.saveChanges();
        return AITaskResult.SUCCESS;
    }

    @Override
    public void cancelTask(EntityRefReference reference) {
        EntityRef entityRef = reference.getEntityRef();
        entityRef.removeComponents(SpawnerComponent.class);
        entityRef.saveChanges();
    }
}
