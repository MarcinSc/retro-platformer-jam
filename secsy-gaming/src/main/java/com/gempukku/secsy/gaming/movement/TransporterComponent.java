package com.gempukku.secsy.gaming.movement;

import com.gempukku.secsy.entity.Component;
import com.gempukku.secsy.entity.component.DefaultValue;

public interface TransporterComponent extends Component {
    @DefaultValue("groundSensor")
    String getSensorType();
}
