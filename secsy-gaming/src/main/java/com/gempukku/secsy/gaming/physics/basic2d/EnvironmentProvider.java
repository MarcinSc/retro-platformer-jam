package com.gempukku.secsy.gaming.physics.basic2d;

import com.badlogic.gdx.math.Vector2;
import com.gempukku.secsy.entity.EntityRef;

public interface EnvironmentProvider {
    Vector2 getGravityForEntity(EntityRef entity, Vector2 toUse);

    float getTerminalVelocityForEntity(EntityRef entity);

    float getFrictionForEntity(EntityRef entity);
}
