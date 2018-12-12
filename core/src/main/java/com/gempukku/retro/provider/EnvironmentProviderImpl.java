package com.gempukku.retro.provider;

import com.badlogic.gdx.math.Vector2;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.gaming.physics.basic2d.EnvironmentProvider;

@RegisterSystem(shared = EnvironmentProvider.class)
public class EnvironmentProviderImpl implements EnvironmentProvider {
    @Override
    public Vector2 getGravityForEntity(EntityRef entity, Vector2 toUse) {
        return toUse.set(0, -10);
    }

    @Override
    public float getTerminalVelocityForEntity(EntityRef entity) {
        return 5;
    }
}
