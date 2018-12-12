package com.gempukku.secsy.gaming.faction;

import com.gempukku.secsy.entity.EntityRef;

public interface FactionManager {
    boolean isEnemy(EntityRef sourceEntity, EntityRef destinationEntity);
}
