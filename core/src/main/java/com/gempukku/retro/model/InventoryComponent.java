package com.gempukku.retro.model;

import com.gempukku.secsy.entity.Component;

import java.util.List;

public interface InventoryComponent extends Component {
    List<String> getItems();

    void setItems(List<String> items);

    String getEquippedItem();

    void setEquippedItem(String equippedItem);
}
