package com.gempukku.secsy.gaming.camera2d.component;

import com.gempukku.secsy.entity.Component;
import com.gempukku.secsy.entity.component.DefaultValue;

public interface TrackingCameraComponent extends Component {
    @DefaultValue("true")
    boolean isFollowingNotGrounded();

    float getOrientationBasedDistance();

    void setLastGroundY(float lastGroundY);

    float getLastGroundY();
}
