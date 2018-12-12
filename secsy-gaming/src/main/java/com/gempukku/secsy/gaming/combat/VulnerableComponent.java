package com.gempukku.secsy.gaming.combat;

import com.gempukku.secsy.entity.Component;
import com.gempukku.secsy.entity.component.DefaultValue;

public interface VulnerableComponent extends Component {
    @DefaultValue("body")
    String getSensorType();
}
