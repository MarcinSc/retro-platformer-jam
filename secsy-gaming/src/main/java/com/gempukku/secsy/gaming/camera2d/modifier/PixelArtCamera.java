package com.gempukku.secsy.gaming.camera2d.modifier;

import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.gaming.camera2d.Adjust2dCamera;
import com.gempukku.secsy.gaming.camera2d.component.GenesisCameraComponent;

import static com.gempukku.secsy.gaming.camera2d.Camera2DProvider.CAMERA_2D_PROFILE;

@RegisterSystem(profiles = CAMERA_2D_PROFILE)
public class PixelArtCamera {
    @ReceiveEvent(priorityName = "gaming.camera2d.pixelArt")
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
