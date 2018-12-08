package com.gempukku.retro.camera;

import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.gaming.camera2d.Adjust2dCamera;

@RegisterSystem
public class GenesisCamera {
    @ReceiveEvent(priority = 100)
    public void adjustCamera(Adjust2dCamera camera, EntityRef cameraEntity, GenesisCameraComponent genesisCamera) {
        float objectPixelHeight = genesisCamera.getObjectPixelHeight();
        float objectUnitHeight = genesisCamera.getObjectUnitHeight();

        float viewportWidth = camera.getViewportWidth();
        float viewportHeight = camera.getViewportHeight();

        float desiredHeight = viewportHeight * objectUnitHeight / objectPixelHeight;
        float desiredWidth = viewportWidth / viewportHeight * desiredHeight;

        camera.setViewportWidth(desiredWidth);
        camera.setViewportHeight(desiredHeight);
    }
}
