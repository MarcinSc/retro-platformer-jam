package com.gempukku.secsy.gaming.combat;

import com.gempukku.secsy.entity.Component;

public interface HealthComponent extends Component {
    int getMaxHealth();

    void setMaxHealth(int maxHealth);

    int getCurrentHealth();

    void setCurrentHealth(int currentHealth);
}
