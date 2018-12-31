package com.gempukku.retro.provider;

import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.game.GameEntityProvider;
import com.gempukku.secsy.gaming.spawn.SpawnManager;

@RegisterSystem(shared = GameEntityProvider.class)
public class GameEntityProviderImpl extends AbstractLifeCycleSystem implements GameEntityProvider {
    @Inject
    private SpawnManager spawnManager;

    private EntityRef currentGameLoopEntity;

    @Override
    public void initialize() {
        currentGameLoopEntity = spawnManager.spawnEntity("gameGameEntity");
    }

    @Override
    public EntityRef getGameEntity() {
        return currentGameLoopEntity;
    }
}
