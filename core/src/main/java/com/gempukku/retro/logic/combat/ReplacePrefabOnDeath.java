package com.gempukku.retro.logic.combat;

import com.gempukku.retro.logic.spawn.SpawnManager;
import com.gempukku.retro.model.PrefabComponent;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityManager;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.gaming.component.Position2DComponent;

import java.util.LinkedList;
import java.util.List;

@RegisterSystem
public class ReplacePrefabOnDeath {
    @Inject
    private EntityManager entityManager;
    @Inject
    private SpawnManager spawnManager;

    @ReceiveEvent
    public void replacePrefab(EntityDied entityDied, EntityRef entity, ReplacePrefabOnDeathComponent replacePrefabOnDeath) {
        String oldPrefab = replacePrefabOnDeath.getOldPrefab();
        String newPrefab = replacePrefabOnDeath.getNewPrefab();

        List<EntityRef> toReplace = new LinkedList<EntityRef>();
        for (EntityRef prefabEntity : entityManager.getEntitiesWithComponents(PrefabComponent.class)) {
            PrefabComponent component = prefabEntity.getComponent(PrefabComponent.class);
            if (component.getPrefab().equals(oldPrefab)) {
                toReplace.add(prefabEntity);
            }
        }

        for (EntityRef entityToReplace : toReplace) {
            Position2DComponent position = entityToReplace.getComponent(Position2DComponent.class);
            float x = position.getX();
            float y = position.getY();

            entityManager.destroyEntity(entityToReplace);

            spawnManager.spawnEntityAt(newPrefab, x, y);
        }
    }
}

