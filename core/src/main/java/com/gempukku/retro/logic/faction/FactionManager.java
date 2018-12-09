package com.gempukku.retro.logic.faction;

import com.gempukku.secsy.entity.EntityRef;

public interface FactionManager {
    boolean isEnemy(EntityRef sourceEntity, EntityRef destinationEntity);
}
