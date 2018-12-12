package com.gempukku.retro.provider;

import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.gaming.physics.basic2d.TimeStepProvider;

@RegisterSystem(shared = TimeStepProvider.class)
public class AccurateTimeStepProvider implements TimeStepProvider {
    @Override
    public long getTimeStep() {
        return 10;
    }
}
