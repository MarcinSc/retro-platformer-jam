package com.gempukku.secsy.gaming.ai.task.movement;

import com.gempukku.secsy.entity.Component;
import com.gempukku.secsy.entity.component.DefaultValue;

public interface AIApplyMovementIfPossibleComponent extends Component {
    @DefaultValue("obstacleSensor")
    String getObstacleSensor();

    @DefaultValue("fallSensor")
    String getFallSensor();
}
