package com.gempukku.retro.model;

import com.gempukku.secsy.entity.Component;

public interface PickupComponent extends Component {
    String getType();

    void setType(String type);
}
