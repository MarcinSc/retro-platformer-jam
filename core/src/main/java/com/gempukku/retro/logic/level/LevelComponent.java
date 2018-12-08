package com.gempukku.retro.logic.level;

import com.gempukku.secsy.entity.Component;

public interface LevelComponent extends Component {
    String getLevel();

    void setLevel(String level);
}
