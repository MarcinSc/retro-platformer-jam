package com.gempukku.retro.provider;

import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.gaming.physics.basic2d.TimeStepProvider;

@RegisterSystem(shared = TimeStepProvider.class)
public class AccurateTimeStepProvider implements TimeStepProvider {
    @Override
    public float getMaxTimeStep() {
        return 1 / 100f;
    }
}
