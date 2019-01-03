package com.gempukku.retro.provider;

import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.game.GameEntityProvider;
import com.gempukku.secsy.entity.index.EntityIndex;
import com.gempukku.secsy.entity.index.EntityIndexManager;
import com.gempukku.secsy.gaming.camera2d.component.Camera2DComponent;
import com.gempukku.secsy.gaming.component.GameComponent;
import com.gempukku.secsy.gaming.rendering.pipeline.CameraEntityProvider;

@RegisterSystem(shared = CameraEntityProvider.class)
public class CameraEntityProviderImpl extends AbstractLifeCycleSystem implements CameraEntityProvider {
    @Inject
    private EntityIndexManager entityIndexManager;
    @Inject
    private GameEntityProvider gameEntityProvider;

    private EntityIndex cameraIndex;

    @Override
    public void initialize() {
        cameraIndex = entityIndexManager.addIndexOnComponents(Camera2DComponent.class);
    }

    @Override
    public EntityRef getCameraEntity() {
        String activeCameraId = gameEntityProvider.getGameEntity().getComponent(GameComponent.class).getActiveCameraId();
        for (EntityRef camera : cameraIndex) {
            String id = camera.getComponent(Camera2DComponent.class).getId();
            if (id.equals(activeCameraId))
                return camera;
        }

        return null;
    }
}
