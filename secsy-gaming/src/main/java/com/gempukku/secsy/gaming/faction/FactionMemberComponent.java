package com.gempukku.secsy.gaming.faction;

import com.gempukku.secsy.entity.Component;

public interface FactionMemberComponent extends Component {
    String getFaction();

    void setFaction(String faction);
}
