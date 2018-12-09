package com.gempukku.retro.model;

import com.gempukku.secsy.entity.Component;

import java.util.List;

public interface EquipmentComponent extends Component {
    List<String> getEquipment();

    void setEquipment(List<String> equipment);

    String getEquippedItem();

    void setEquippedItem(String equippedItem);
}
