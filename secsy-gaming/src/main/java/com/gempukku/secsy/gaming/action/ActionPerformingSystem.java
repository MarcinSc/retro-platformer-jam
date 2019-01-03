package com.gempukku.secsy.gaming.action;

import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.entity.game.GameLoopUpdate;
import com.gempukku.secsy.entity.index.EntityIndex;
import com.gempukku.secsy.entity.index.EntityIndexManager;

@RegisterSystem(profiles = "action")
public class ActionPerformingSystem extends AbstractLifeCycleSystem {
    @Inject
    private ActionSchemeProvider actionSchemeProvider;
    @Inject
    private EntityIndexManager entityIndexManager;

    private boolean actionPressed;
    private EntityIndex performsActions;

    @Override
    public void initialize() {
        performsActions = entityIndexManager.addIndexOnComponents(PerformsActionsComponent.class);
    }

    @ReceiveEvent
    public void activate(GameLoopUpdate gameLoopUpdate) {
        boolean pressedNow = actionSchemeProvider.isActionActivated();
        if (pressedNow && !actionPressed) {
            for (EntityRef performsAction : performsActions) {
                performsAction.send(new EntityPerformedAction());
            }

            actionPressed = true;
        } else if (!pressedNow) {
            actionPressed = false;
        }
    }
}
