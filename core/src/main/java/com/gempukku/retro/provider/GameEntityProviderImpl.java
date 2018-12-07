package com.gempukku.retro.provider;

import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.entity.EntityManager;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.game.GameEntityProvider;
import com.gempukku.secsy.gaming.time.TimeEntityProvider;

@RegisterSystem(shared = {GameEntityProvider.class, TimeEntityProvider.class})
public class GameEntityProviderImpl extends AbstractLifeCycleSystem implements GameEntityProvider, TimeEntityProvider {
    @Inject
    private EntityManager entityManager;

    private EntityRef currentGameLoopEntity;

    @Override
    public void initialize() {
        currentGameLoopEntity = entityManager.createEntityFromPrefab("gameGameEntity");
    }


    @Override
    public EntityRef getTimeEntity() {
        return currentGameLoopEntity;
    }

    @Override
    public EntityRef getGameEntity() {
        return currentGameLoopEntity;
    }
}
