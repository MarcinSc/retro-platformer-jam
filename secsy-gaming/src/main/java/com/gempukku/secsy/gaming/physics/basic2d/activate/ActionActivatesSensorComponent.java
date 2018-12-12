package com.gempukku.secsy.gaming.physics.basic2d.activate;

import com.gempukku.secsy.entity.Component;
import com.gempukku.secsy.entity.component.DefaultValue;

public interface ActionActivatesSensorComponent extends Component {
    @DefaultValue("body")
    String getSensorType();
}
