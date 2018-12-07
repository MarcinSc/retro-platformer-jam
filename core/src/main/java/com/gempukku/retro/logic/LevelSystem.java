package com.gempukku.retro.logic;

import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.entity.EntityManager;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.entity.game.GameLoopUpdate;

@RegisterSystem
public class LevelSystem extends AbstractLifeCycleSystem {
    @Inject
    private EntityManager entityManager;

    private boolean levelLoaded = false;

    @ReceiveEvent
    public void update(GameLoopUpdate update) {
        if (!levelLoaded) {
            loadLevel();
            levelLoaded = true;
        }
    }

    private void loadLevel() {
        entityManager.createEntityFromPrefab("playerEntity");
    }
}
