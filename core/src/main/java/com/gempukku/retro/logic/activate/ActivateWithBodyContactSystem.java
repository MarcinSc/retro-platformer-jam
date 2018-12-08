package com.gempukku.retro.logic.activate;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.gempukku.retro.model.PlayerComponent;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.entity.game.GameLoopUpdate;
import com.gempukku.secsy.entity.index.EntityIndex;
import com.gempukku.secsy.entity.index.EntityIndexManager;
import com.gempukku.secsy.gaming.physics.basic2d.Basic2dPhysics;
import com.google.common.base.Predicate;

import javax.annotation.Nullable;

@RegisterSystem
public class ActivateWithBodyContactSystem extends AbstractLifeCycleSystem {
    @Inject
    private Basic2dPhysics basic2dPhysics;
    @Inject
    private EntityIndexManager entityIndexManager;

    private boolean activatePressed;
    private EntityIndex players;

    @Override
    public void initialize() {
        players = entityIndexManager.addIndexOnComponents(PlayerComponent.class);
    }

    @ReceiveEvent
    public void activate(GameLoopUpdate gameLoopUpdate) {
        boolean pressedNow = Gdx.input.isKeyPressed(Input.Keys.X);
        if (pressedNow && !activatePressed) {
            for (EntityRef player : players) {
                for (EntityRef activated : basic2dPhysics.getContactsForSensor(player, "body",
                        new Predicate<EntityRef>() {
                            @Override
                            public boolean apply(@Nullable EntityRef entityRef) {
                                return entityRef.hasComponent(ActivateWithBodyContactComponent.class);
                            }
                        })) {
                    activated.send(new EntityActivated());
                }
            }

            activatePressed = true;
        } else if (!pressedNow) {
            activatePressed = false;
        }
    }
}
