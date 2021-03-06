package com.gempukku.secsy.entity.game;

import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.util.PriorityCollection;

@RegisterSystem(
        profiles = "gameLoop",
        shared = {InternalGameLoop.class})
public class SimpleGameLoop implements InternalGameLoop {
    @Inject
    private GameEntityProvider gameEntityProvider;

    private PriorityCollection<InternalGameLoopListener> internalGameLoopListeners = new PriorityCollection<InternalGameLoopListener>();

    @Override
    public void addInternalGameLoopListener(InternalGameLoopListener internalGameLoopListener) {
        internalGameLoopListeners.add(internalGameLoopListener);
    }

    @Override
    public void removeInternalGameLooplListener(InternalGameLoopListener internalGameLoopListener) {
        internalGameLoopListeners.remove(internalGameLoopListener);
    }

    @Override
    public void processUpdate() {
        for (InternalGameLoopListener internalGameLoopListener : internalGameLoopListeners) {
            internalGameLoopListener.preUpdate();
        }

        gameEntityProvider.getGameEntity().send(new GameLoopUpdate());

        for (InternalGameLoopListener internalGameLoopListener : internalGameLoopListeners) {
            internalGameLoopListener.postUpdate();
        }
    }
}
