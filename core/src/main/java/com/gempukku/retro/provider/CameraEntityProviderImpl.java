package com.gempukku.retro.provider;

import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.entity.EntityManager;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.gaming.rendering.pipeline.CameraEntityProvider;

@RegisterSystem(shared = CameraEntityProvider.class)
public class CameraEntityProviderImpl extends AbstractLifeCycleSystem implements CameraEntityProvider {
    @Inject
    private EntityManager entityManager;

    private EntityRef gameCameraEntity;

    private EntityRef currentCamera;

    @Override
    public void initialize() {
        gameCameraEntity = entityManager.createEntityFromPrefab("gameCameraEntity");

        currentCamera = gameCameraEntity;
    }

    @Override
    public EntityRef getCameraEntity() {
        return currentCamera;
    }
}
