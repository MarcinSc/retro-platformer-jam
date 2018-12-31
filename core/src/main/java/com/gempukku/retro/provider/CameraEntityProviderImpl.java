package com.gempukku.retro.provider;

import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.index.EntityIndex;
import com.gempukku.secsy.entity.index.EntityIndexManager;
import com.gempukku.secsy.gaming.camera2d.component.Camera2DComponent;
import com.gempukku.secsy.gaming.rendering.pipeline.CameraEntityProvider;
import com.gempukku.secsy.gaming.spawn.SpawnManager;

@RegisterSystem(shared = CameraEntityProvider.class)
public class CameraEntityProviderImpl extends AbstractLifeCycleSystem implements CameraEntityProvider {
    @Inject
    private SpawnManager spawnManager;
    @Inject
    private EntityIndexManager entityIndexManager;

    private EntityRef gameCameraEntity;

    private EntityIndex cameraIndex;

    @Override
    public void initialize() {
        cameraIndex = entityIndexManager.addIndexOnComponents(Camera2DComponent.class);

        gameCameraEntity = spawnManager.spawnEntity("gameCameraEntity");
    }

    @Override
    public EntityRef getCameraEntity() {
        return cameraIndex.iterator().next();
    }
}
