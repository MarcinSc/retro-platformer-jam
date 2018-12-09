package com.gempukku.retro.logic.ai;

import com.gempukku.retro.logic.ai.task.WaitForOpponentInSightTask;
import com.gempukku.retro.logic.ai.task.WaitTask;
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
import com.gempukku.secsy.gaming.time.TimeManager;
import com.google.common.base.Predicate;

import javax.annotation.Nullable;

@RegisterSystem
public class AIWaitingSystem extends AbstractLifeCycleSystem {
    @Inject
    private AIEngine aiEngine;
    @Inject
    private EntityIndexManager entityIndexManager;
    @Inject
    private TimeManager timeManager;
    @Inject
    private Basic2dPhysics basic2dPhysics;

    private EntityIndex notifyEntities;
    private EntityIndex waitingForOpponentInRangeEntities;

    @Override
    public void initialize() {
        notifyEntities = entityIndexManager.addIndexOnComponents(AINotifyAfterComponent.class);
        waitingForOpponentInRangeEntities = entityIndexManager.addIndexOnComponents(AIWaitsForOpponentInRangeComponent.class);
    }

    @ReceiveEvent
    public void update(GameLoopUpdate update) {
        long time = timeManager.getTime();

        for (EntityRef notifyEntity : notifyEntities) {
            AINotifyAfterComponent notifyAfter = notifyEntity.getComponent(AINotifyAfterComponent.class);
            if (notifyAfter.getStartTime() == 0) {
                notifyAfter.setStartTime(time);
                notifyEntity.saveChanges();
            } else if (notifyAfter.getStartTime() + notifyAfter.getNotifyAfter() <= time) {
                for (WaitTask waitTask : aiEngine.getRunningTasksOfType(notifyEntity, WaitTask.class)) {
                    waitTask.timeIsUp(aiEngine.getReference(notifyEntity));
                }
            }
        }

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
            if (basic2dPhysics.getSensorTriggersFor(waitingForOpponentInRangeEntity, "view",
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
