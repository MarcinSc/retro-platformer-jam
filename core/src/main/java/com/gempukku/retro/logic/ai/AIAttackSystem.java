package com.gempukku.retro.logic.ai;

import com.gempukku.retro.logic.ai.task.WaitForOpponentInSightTask;
import com.gempukku.retro.model.PlayerComponent;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.entity.game.GameLoopUpdate;
import com.gempukku.secsy.entity.index.EntityIndex;
import com.gempukku.secsy.entity.index.EntityIndexManager;
import com.gempukku.secsy.gaming.ai.AIEngine;
import com.gempukku.secsy.gaming.component.HorizontalOrientationComponent;
import com.gempukku.secsy.gaming.component.Position2DComponent;
import com.gempukku.secsy.gaming.physics.basic2d.Basic2dPhysics;
import com.google.common.base.Predicate;

import javax.annotation.Nullable;

@RegisterSystem
public class AIAttackSystem extends AbstractLifeCycleSystem {
    @Inject
    private EntityIndexManager entityIndexManager;
    @Inject
    private Basic2dPhysics physics;
    @Inject
    private AIEngine aiEngine;

    private EntityIndex waitingForOpponentInRangeEntities;

    @Override
    public void initialize() {
        waitingForOpponentInRangeEntities = entityIndexManager.addIndexOnComponents(AIWaitsForOpponentInRangeComponent.class);
    }

    @ReceiveEvent
    public void update(GameLoopUpdate update) {
        for (EntityRef waitingForOpponentInRangeEntity : waitingForOpponentInRangeEntities) {
            AIWaitsForOpponentInRangeComponent component = waitingForOpponentInRangeEntity.getComponent(AIWaitsForOpponentInRangeComponent.class);
            float height = component.getHeight();
            float range = component.getRange();
            Position2DComponent position = waitingForOpponentInRangeEntity.getComponent(Position2DComponent.class);
            HorizontalOrientationComponent orientation = waitingForOpponentInRangeEntity.getComponent(HorizontalOrientationComponent.class);

            float minX;
            float maxX;
            if (orientation.isFacingRight()) {
                minX = position.getX();
                maxX = position.getX() + range;
            } else {
                minX = position.getX() - range;
                maxX = position.getX();
            }
            if (physics.getSensorTriggersFor(waitingForOpponentInRangeEntity, "view",
                    minX, maxX, position.getY(), position.getY() + height, new Predicate<EntityRef>() {
                        @Override
                        public boolean apply(@Nullable EntityRef entityRef) {
                            return entityRef.hasComponent(PlayerComponent.class);
                        }
                    }).iterator().hasNext()) {
                for (WaitForOpponentInSightTask waitForOpponentInSightTask : aiEngine.getRunningTasksOfType(waitingForOpponentInRangeEntity, WaitForOpponentInSightTask.class)) {
                    waitForOpponentInSightTask.opponentInSight(aiEngine.getReference(waitingForOpponentInRangeEntity));
                }
            }
        }
    }
}
