package com.gempukku.retro.logic.faction;

import com.gempukku.secsy.entity.Component;

import java.util.List;

public interface FactionComponent extends Component {
    String getName();

    void setName(String name);

    List<String> getEnemyFactions();

    void setEnemyFactions(List<String> enemyFactions);
}
