package com.gempukku.retro.provider;

import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.index.EntityIndex;
import com.gempukku.secsy.entity.index.EntityIndexManager;
import com.gempukku.secsy.gaming.time.TimeComponent;
import com.gempukku.secsy.gaming.time.TimeEntityProvider;

@RegisterSystem(shared = TimeEntityProvider.class)
public class TimeEntityProviderImpl extends AbstractLifeCycleSystem implements TimeEntityProvider {
    @Inject
    private EntityIndexManager entityIndexManager;

    private EntityIndex timeIndex;

    @Override
    public void initialize() {
        timeIndex = entityIndexManager.addIndexOnComponents(TimeComponent.class);
    }

    @Override
    public EntityRef getTimeEntity() {
        return timeIndex.iterator().next();
    }
}
