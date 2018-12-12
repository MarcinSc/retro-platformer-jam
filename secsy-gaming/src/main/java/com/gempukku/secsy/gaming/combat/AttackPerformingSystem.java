package com.gempukku.secsy.gaming.combat;

import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.entity.game.GameLoopUpdate;
import com.gempukku.secsy.entity.index.EntityIndex;
import com.gempukku.secsy.entity.index.EntityIndexManager;
import com.gempukku.secsy.gaming.time.TimeManager;

@RegisterSystem(profiles = "combat")
public class AttackPerformingSystem extends AbstractLifeCycleSystem {
    @Inject
    private TimeManager timeManager;
    @Inject
    private EntityIndexManager entityIndexManager;
    @Inject
    private AttackSchemeProvider attackSchemeProvider;

    private EntityIndex performAttacksEntities;

    private boolean attackPressed;

    @Override
    public void initialize() {
        performAttacksEntities = entityIndexManager.addIndexOnComponents(PerformsAttacksComponent.class);
    }

    @ReceiveEvent
    public void update(GameLoopUpdate update) {
        long time = timeManager.getTime();
        boolean attack = attackSchemeProvider.isAttackActivated();
        if (attack && !attackPressed) {
            for (EntityRef performAttacksEntity : performAttacksEntities) {
                CombatComponent combat = performAttacksEntity.getComponent(CombatComponent.class);
                long nextAttackTime = combat.getNextAttackTime();

                if (time >= nextAttackTime) {
                    performAttacksEntity.send(new EntityAttacked());
                }
            }

            attackPressed = true;
        } else if (!attack) {
            attackPressed = false;
        }
    }
}
