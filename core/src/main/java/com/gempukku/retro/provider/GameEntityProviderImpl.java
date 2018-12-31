package com.gempukku.retro.provider;

import com.gempukku.retro.model.GameComponent;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.game.GameEntityProvider;
import com.gempukku.secsy.entity.index.EntityIndex;
import com.gempukku.secsy.entity.index.EntityIndexManager;

@RegisterSystem(shared = GameEntityProvider.class)
public class GameEntityProviderImpl extends AbstractLifeCycleSystem implements GameEntityProvider {
    @Inject
    private EntityIndexManager entityIndexManager;

    private EntityIndex gameIndex;

    @Override
    public void initialize() {
        gameIndex = entityIndexManager.addIndexOnComponents(GameComponent.class);
    }


    @Override
    public EntityRef getGameEntity() {
        return gameIndex.iterator().next();
    }
}
