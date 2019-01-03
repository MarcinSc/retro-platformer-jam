package com.gempukku.secsy.gaming.editor.component;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.gaming.component.Position2DComponent;
import com.gempukku.secsy.gaming.editor.EntityComponentEditor;
import com.google.common.base.Function;

import javax.annotation.Nullable;

public class Position2DEditor implements EntityComponentEditor {
    @Override
    public void appendEditor(Table table, Skin skin, final EntityRef entityRef) {
        CommonEditors.appendTwoFloatFieldsEditor(table, skin, entityRef, "Position",
                "x", new Function<EntityRef, Float>() {
                    @Nullable
                    @Override
                    public Float apply(@Nullable EntityRef entityRef) {
                        return entityRef.getComponent(Position2DComponent.class).getX();
                    }
                }, new Function<Float, Void>() {
                    @Nullable
                    @Override
                    public Void apply(@Nullable Float value) {
                        Position2DComponent position = entityRef.getComponent(Position2DComponent.class);
                        position.setX(value);
                        entityRef.saveChanges();
                        return null;
                    }
                }, "x", new Function<EntityRef, Float>() {
                    @Nullable
                    @Override
                    public Float apply(@Nullable EntityRef entityRef) {
                        return entityRef.getComponent(Position2DComponent.class).getY();
                    }
                }, new Function<Float, Void>() {
                    @Nullable
                    @Override
                    public Void apply(@Nullable Float value) {
                        Position2DComponent position = entityRef.getComponent(Position2DComponent.class);
                        position.setY(value);
                        entityRef.saveChanges();
                        return null;
                    }
                });
    }


}
