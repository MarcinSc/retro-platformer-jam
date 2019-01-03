package com.gempukku.secsy.gaming.editor.component;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.gaming.component.Size2DComponent;
import com.gempukku.secsy.gaming.editor.EntityComponentEditor;
import com.google.common.base.Function;

import javax.annotation.Nullable;

public class Size2DEditor implements EntityComponentEditor {
    private static final int LABEL_MAX_WIDTH = 50;
    private static final FloatNumberTextFieldFilter FLOAT_FILTER = new FloatNumberTextFieldFilter();

    @Override
    public void appendEditor(Table table, Skin skin, final EntityRef entityRef) {
        CommonEditors.appendTwoFloatFieldsEditor(table, skin, entityRef, "Size",
                "width", new Function<EntityRef, Float>() {
                    @Nullable
                    @Override
                    public Float apply(@Nullable EntityRef entityRef) {
                        return entityRef.getComponent(Size2DComponent.class).getWidth();
                    }
                }, new Function<Float, Void>() {
                    @Nullable
                    @Override
                    public Void apply(@Nullable Float value) {
                        Size2DComponent size = entityRef.getComponent(Size2DComponent.class);
                        size.setWidth(value);
                        entityRef.saveChanges();
                        return null;
                    }
                }, "height", new Function<EntityRef, Float>() {
                    @Nullable
                    @Override
                    public Float apply(@Nullable EntityRef entityRef) {
                        return entityRef.getComponent(Size2DComponent.class).getHeight();
                    }
                }, new Function<Float, Void>() {
                    @Nullable
                    @Override
                    public Void apply(@Nullable Float value) {
                        Size2DComponent size = entityRef.getComponent(Size2DComponent.class);
                        size.setHeight(value);
                        entityRef.saveChanges();
                        return null;
                    }
                });
    }
}
