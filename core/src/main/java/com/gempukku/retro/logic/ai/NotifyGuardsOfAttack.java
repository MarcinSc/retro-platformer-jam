package com.gempukku.retro.logic.ai;

import com.gempukku.retro.logic.combat.EntityDamaged;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.entity.index.EntityIndex;
import com.gempukku.secsy.entity.index.EntityIndexManager;
import com.gempukku.secsy.gaming.ai.AIComponent;
import com.gempukku.secsy.gaming.ai.AIEngine;

@RegisterSystem
public class NotifyGuardsOfAttack extends AbstractLifeCycleSystem {
    @Inject
    private AIEngine aiEngine;
    @Inject
    private EntityIndexManager entityIndexManager;
    private EntityIndex ais;

    @Override
    public void initialize() {
        ais = entityIndexManager.addIndexOnComponents(AIComponent.class);
    }

    @ReceiveEvent
    public void damageDone(EntityDamaged entityDamaged) {
        for (EntityRef ai : ais) {
            for (WaitForAnyDamageTask waitForAnyDamageTask : aiEngine.getRunningTasksOfType(ai, WaitForAnyDamageTask.class)) {
                waitForAnyDamageTask.damageDone(aiEngine.getReference(ai));
            }
        }
    }
}
