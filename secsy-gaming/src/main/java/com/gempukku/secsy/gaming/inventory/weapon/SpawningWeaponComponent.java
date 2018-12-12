package com.gempukku.secsy.gaming.inventory.weapon;

import com.gempukku.secsy.entity.Component;

public interface SpawningWeaponComponent extends Component {
    long getCoolDown();

    String getPrefab();

    float getX();

    float getY();
}
