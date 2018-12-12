package com.gempukku.retro.logic.movement;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.entity.game.GameLoopUpdate;
import com.gempukku.secsy.entity.index.EntityIndex;
import com.gempukku.secsy.entity.index.EntityIndexManager;
import com.gempukku.secsy.gaming.component.Position2DComponent;
import com.gempukku.secsy.gaming.easing.EasedValue;
import com.gempukku.secsy.gaming.easing.EasingResolver;
import com.gempukku.secsy.gaming.physics.basic2d.Basic2dPhysics;
import com.gempukku.secsy.gaming.physics.basic2d.EntityMoved;
import com.gempukku.secsy.gaming.time.TimeManager;

@RegisterSystem
public class MovementSystem extends AbstractLifeCycleSystem {
    @Inject
    private EntityIndexManager entityIndexManager;
    @Inject
    private TimeManager timeManager;
    @Inject
    private EasingResolver easingResolver;
    @Inject
    private Basic2dPhysics physics;

    private EntityIndex oscillatingEntities;

    @Override
    public void initialize() {
        oscillatingEntities = entityIndexManager.addIndexOnComponents(OscillatingComponent.class);
    }

    @ReceiveEvent
    public void moveOscillating(GameLoopUpdate update) {
        long time = timeManager.getTime();

        for (EntityRef oscillatingEntity : oscillatingEntities) {
            OscillatingComponent oscillating = oscillatingEntity.getComponent(OscillatingComponent.class);
            Position2DComponent position = oscillatingEntity.getComponent(Position2DComponent.class);
            float x = position.getX();
            float y = position.getY();
            Vector2 startingPosition = oscillating.getStartingPosition();
            Vector2 distance = oscillating.getDistance();

            long cycleLength = oscillating.getCycleLength();
            float frac = 1f * time / cycleLength;
            float timeInCycle = frac - MathUtils.floor(frac);
            EasedValue distanceTimeFunction = oscillating.getDistanceTimeFunction();
            float distancePerc = easingResolver.resolveValue(distanceTimeFunction, timeInCycle);

            float modifiedPositionX = startingPosition.x + distancePerc * distance.x;
            float modifiedPositionY = startingPosition.y + distancePerc * distance.y;

            if (modifiedPositionX != x || modifiedPositionY != y) {
                position.setX(modifiedPositionX);
                position.setY(modifiedPositionY);
            }
            oscillatingEntity.saveChanges();

            oscillatingEntity.send(new EntityMoved(x, y, modifiedPositionX, modifiedPositionY));
        }
    }

    @ReceiveEvent
    public void transporter(EntityMoved moved, EntityRef entity, TransporterComponent transporter) {
        for (EntityRef entityRef : physics.getSensorEntitiesContactedBy(entity, "groundSensor")) {
            Position2DComponent position = entityRef.getComponent(Position2DComponent.class);
            position.setX(position.getX() + moved.getNewX() - moved.getOldX());
            position.setY(position.getY() + moved.getNewY() - moved.getOldY());
            entityRef.saveChanges();
        }
    }
}
