package com.gempukku.retro.logic.player;

import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.index.EntityIndex;
import com.gempukku.secsy.entity.index.EntityIndexManager;

@RegisterSystem(shared = PlayerProvider.class)
public class PlayerSystem extends AbstractLifeCycleSystem implements PlayerProvider {
    @Inject
    private EntityIndexManager entityIndexManager;
    private EntityIndex playerEntities;

    @Override
    public void initialize() {
        playerEntities = entityIndexManager.addIndexOnComponents(PlayerComponent.class);
    }

    @Override
    public EntityRef getPlayer() {
        for (EntityRef playerEntity : playerEntities) {
            return playerEntity;
        }

        return null;
    }
}
