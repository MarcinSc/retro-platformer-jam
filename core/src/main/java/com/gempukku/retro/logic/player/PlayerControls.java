package com.gempukku.retro.logic.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.gaming.combat.AttackSchemeProvider;
import com.gempukku.secsy.gaming.input.ActionSchemeProvider;
import com.gempukku.secsy.gaming.input2d.InputScheme2dProvider;
import com.gempukku.secsy.gaming.time.TimeManager;

@RegisterSystem(shared = {InputScheme2dProvider.class, ActionSchemeProvider.class, AttackSchemeProvider.class})
public class PlayerControls extends AbstractLifeCycleSystem implements InputScheme2dProvider, ActionSchemeProvider, AttackSchemeProvider {
    @Inject
    private TimeManager timeManager;
    @Inject
    private PlayerProvider playerProvider;

    private int[] jumpKeys = new int[]{Input.Keys.W, Input.Keys.UP};
    private int[] leftKeys = new int[]{Input.Keys.A, Input.Keys.LEFT};
    private int[] rightKeys = new int[]{Input.Keys.D, Input.Keys.RIGHT};
    private int[] actionKeys = new int[]{Input.Keys.X};
    private int[] attackKeys = new int[]{Input.Keys.SPACE};
    private int attackKey = Input.Keys.SPACE;

    private boolean attackPressed;

    @Override
    public boolean isActionActivated() {
        return isAnyPressed(actionKeys);
    }

    @Override
    public boolean isAttackActivated() {
        return isAnyPressed(attackKeys);
    }

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

    private boolean isAnyPressed(int[] keys) {
        for (int key : keys) {
            if (Gdx.input.isKeyPressed(key))
                return true;
        }
        return false;
    }
}
