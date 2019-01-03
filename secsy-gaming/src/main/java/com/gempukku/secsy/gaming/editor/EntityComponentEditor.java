package com.gempukku.secsy.gaming.editor;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.gempukku.secsy.entity.EntityRef;

public interface EntityComponentEditor {
    void appendEditor(Table table, Skin skin, EntityRef entityRef, PositionUpdateCallback positionUpdateCallback);

    void entityMoved(EntityRef entityRef, float x, float y);

    interface PositionUpdateCallback {
        void positionUpdated(EntityRef entityRef);
    }
}