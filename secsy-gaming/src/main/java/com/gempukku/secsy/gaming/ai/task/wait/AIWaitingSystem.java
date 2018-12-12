package com.gempukku.secsy.gaming.ai.task.wait;

import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.entity.game.GameLoopUpdate;
import com.gempukku.secsy.entity.index.EntityIndex;
import com.gempukku.secsy.entity.index.EntityIndexManager;
import com.gempukku.secsy.gaming.ai.AIEngine;
import com.gempukku.secsy.gaming.time.TimeManager;

@RegisterSystem(profiles = "aiWait")
public class AIWaitingSystem extends AbstractLifeCycleSystem {
    @Inject
    private AIEngine aiEngine;
    @Inject
    private EntityIndexManager entityIndexManager;
    @Inject
    private TimeManager timeManager;

    private EntityIndex notifyEntities;

    @Override
    public void initialize() {
        notifyEntities = entityIndexManager.addIndexOnComponents(AINotifyAfterComponent.class);
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
    }
}
