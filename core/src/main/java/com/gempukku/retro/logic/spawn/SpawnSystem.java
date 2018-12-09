package com.gempukku.retro.logic.spawn;

import com.gempukku.retro.model.PrefabComponent;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityManager;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.gaming.component.Position2DComponent;

@RegisterSystem(shared = SpawnManager.class)
public class SpawnSystem implements SpawnManager {
    @Inject
    private EntityManager entityManager;

    @Override
    public EntityRef spawnEntity(String prefab) {
        EntityRef entity = entityManager.createEntityFromPrefab(prefab);

        PrefabComponent prefabComp = entity.createComponent(PrefabComponent.class);
        prefabComp.setPrefab(prefab);

        entity.saveChanges();

        return entity;
    }

    @Override
    public EntityRef spawnEntityAt(String prefab, float x, float y) {
        EntityRef entityRef = spawnEntity(prefab);
        Position2DComponent position = entityRef.getComponent(Position2DComponent.class);
        position.setX(x);
        position.setY(y);

        entityRef.saveChanges();
        return entityRef;
    }
}
