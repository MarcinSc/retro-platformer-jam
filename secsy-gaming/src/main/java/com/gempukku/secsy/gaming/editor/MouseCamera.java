package com.gempukku.secsy.gaming.editor;

import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.gaming.camera2d.Adjust2dCamera;

import static com.gempukku.secsy.gaming.camera2d.Camera2DProvider.CAMERA_2D_PROFILE;

@RegisterSystem(profiles = {CAMERA_2D_PROFILE, "editor"})
public class MouseCamera {
    @ReceiveEvent(priorityName = "gaming.camera2d.mouse")
    public void setCamera(Adjust2dCamera cameraLocation, EntityRef cameraEntity, MouseCameraComponent mouseCamera) {
        float viewportWidth = mouseCamera.getViewportWidth();
        float viewportHeight = mouseCamera.getViewportHeight();
        float x = mouseCamera.getX();
        float y = mouseCamera.getY();

        cameraLocation.set(x, y);
        if (viewportWidth != 0 && viewportHeight != 0)
            cameraLocation.setViewport(viewportWidth, viewportHeight);
        else {
            mouseCamera.setViewportWidth(cameraLocation.getViewportWidth());
            mouseCamera.setViewportHeight(cameraLocation.getViewportHeight());
            cameraEntity.saveChanges();
        }
    }
}
