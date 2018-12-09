package com.gempukku.retro.logic.equipment;

import com.gempukku.retro.logic.spawn.SpawnManager;
import com.gempukku.retro.model.ItemComponent;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.index.EntityIndex;
import com.gempukku.secsy.entity.index.EntityIndexManager;
import com.gempukku.secsy.entity.prefab.NamedEntityData;
import com.gempukku.secsy.entity.prefab.PrefabManager;

@RegisterSystem(shared = ItemProvider.class)
public class EquipmentSystem extends AbstractLifeCycleSystem implements ItemProvider {
    @Inject
    private PrefabManager prefabManager;
    @Inject
    private SpawnManager spawnManager;
    @Inject
    private EntityIndexManager entityIndexManager;

    private EntityIndex itemEntities;

    @Override
    public void postInitialize() {
        itemEntities = entityIndexManager.addIndexOnComponents(ItemComponent.class);

        Iterable<? extends NamedEntityData> items = prefabManager.findPrefabsWithComponents(ItemComponent.class);
        for (NamedEntityData item : items) {
            spawnManager.spawnEntity(item.getName());
        }
    }

    @Override
    public EntityRef getItemByName(String name) {
        for (EntityRef itemEntity : itemEntities) {
            ItemComponent item = itemEntity.getComponent(ItemComponent.class);
            if (item.getName().equals(name))
                return itemEntity;
        }
        return null;
    }
}
