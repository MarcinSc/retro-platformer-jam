package com.gempukku.retro.logic.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.gempukku.retro.logic.combat.CombatComponent;
import com.gempukku.retro.logic.combat.EntityMeleeAttacked;
import com.gempukku.retro.model.PlayerComponent;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityManager;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.entity.game.GameLoopUpdate;
import com.gempukku.secsy.gaming.input2d.InputScheme2dProvider;
import com.gempukku.secsy.gaming.time.TimeManager;

@RegisterSystem(shared = InputScheme2dProvider.class)
public class PlayerControls implements InputScheme2dProvider {
    @Inject
    private TimeManager timeManager;
    @Inject
    private EntityManager entityManager;

    private int[] jumpKeys = new int[]{Input.Keys.W, Input.Keys.UP};
    private int[] leftKeys = new int[]{Input.Keys.A, Input.Keys.LEFT};
    private int[] rightKeys = new int[]{Input.Keys.D, Input.Keys.RIGHT};
    private int attackKey = Input.Keys.SPACE;

    private boolean attackPressed;

    @Override
    public boolean isJumpActivated() {
        return isAnyPressed(jumpKeys);
    }

    @Override
    public boolean isLeftActivated() {
        return isAnyPressed(leftKeys);
    }

    @Override
    public boolean isRightActivated() {
        return isAnyPressed(rightKeys);
    }

    @ReceiveEvent
    public void update(GameLoopUpdate update) {
        long time = timeManager.getTime();
        boolean attack = Gdx.input.isKeyPressed(attackKey);
        if (attack && !attackPressed) {
            for (EntityRef playerEntity : entityManager.getEntitiesWithComponents(PlayerComponent.class)) {
                CombatComponent combat = playerEntity.getComponent(CombatComponent.class);
                long lastMeleeAttacked = combat.getLastMeleeAttacked();
                long meleeCoolDown = combat.getMeleeCoolDown();
                if (time > lastMeleeAttacked + meleeCoolDown) {
                    combat.setLastMeleeAttacked(time);
                    playerEntity.saveChanges();
                    playerEntity.send(new EntityMeleeAttacked());
                }
            }

            attackPressed = true;
        } else if (!attack) {
            attackPressed = false;
        }
    }

    private boolean isAnyPressed(int[] keys) {
        for (int key : keys) {
            if (Gdx.input.isKeyPressed(key))
                return true;
        }
        return false;
    }
}
