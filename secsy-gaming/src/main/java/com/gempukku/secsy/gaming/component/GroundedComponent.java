package com.gempukku.secsy.gaming.component;

import com.gempukku.secsy.entity.Component;
import com.gempukku.secsy.entity.component.DefaultValue;

public interface GroundedComponent extends Component {
    @DefaultValue("groundSensor")
    String getSensorType();

    boolean isGrounded();

    void setGrounded(boolean grounded);
}
