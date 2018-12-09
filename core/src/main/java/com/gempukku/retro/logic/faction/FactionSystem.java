package com.gempukku.retro.logic.faction;

import com.gempukku.retro.logic.spawn.SpawnManager;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.index.EntityIndex;
import com.gempukku.secsy.entity.index.EntityIndexManager;
import com.gempukku.secsy.entity.prefab.NamedEntityData;
import com.gempukku.secsy.entity.prefab.PrefabManager;

@RegisterSystem(shared = FactionManager.class)
public class FactionSystem extends AbstractLifeCycleSystem implements FactionManager {
    @Inject
    private PrefabManager prefabManager;
    @Inject
    private EntityIndexManager entityIndexManager;
    @Inject
    private SpawnManager spawnManager;

    private EntityIndex factionEntities;

    @Override
    public void initialize() {
        factionEntities = entityIndexManager.addIndexOnComponents(FactionComponent.class);
    }

    @Override
    public void postInitialize() {
        for (NamedEntityData namedEntityData : prefabManager.findPrefabsWithComponents(FactionComponent.class)) {
            spawnManager.spawnEntity(namedEntityData.getName());
        }
    }

    @Override
    public boolean isEnemy(EntityRef sourceEntity, EntityRef destinationEntity) {
        for (EntityRef factionEntity : factionEntities) {
            FactionComponent component = factionEntity.getComponent(FactionComponent.class);
            if (component.getName().equals(sourceEntity.getComponent(FactionMemberComponent.class).getFaction())) {
                return component.getEnemyFactions().contains(destinationEntity.getComponent(FactionMemberComponent.class).getFaction());
            }
        }
        return false;
    }
}
