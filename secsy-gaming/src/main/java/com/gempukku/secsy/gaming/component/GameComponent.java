package com.gempukku.secsy.gaming.component;

import com.gempukku.secsy.entity.Component;

public interface GameComponent extends Component {
    String getActiveCameraId();

    void setActiveCameraId(String activeCameraId);
}
