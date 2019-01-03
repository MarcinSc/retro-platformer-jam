package com.gempukku.secsy.gaming.editor.component;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.gaming.component.Size2DComponent;
import com.gempukku.secsy.gaming.editor.EntityComponentEditor;
import com.google.common.base.Function;

import javax.annotation.Nullable;

public class Size2DEditor implements EntityComponentEditor {
    private static final int LABEL_MAX_WIDTH = 50;
    private static final FloatNumberTextFieldFilter FLOAT_FILTER = new FloatNumberTextFieldFilter();

    @Override
    public void appendEditor(Table table, Skin skin, final EntityRef entityRef, PositionUpdateCallback positionUpdateCallback) {
        Table groupTable = new Table(skin);
        Drawable background = skin.get("default-round", Drawable.class);
        groupTable.setBackground(background);
        groupTable.pad(background.getTopHeight(), background.getLeftWidth(), background.getBottomHeight(), background.getRightWidth());

        groupTable.add(new Label("Size", skin)).growX().colspan(4);
        groupTable.row();

        CommonEditors.appendLabel(groupTable, skin, "Width");
        CommonEditors.appendFloatField(groupTable, skin, entityRef,
                new Function<EntityRef, Float>() {
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
                });
        CommonEditors.appendLabel(groupTable, skin, "Height");
        CommonEditors.appendFloatField(groupTable, skin, entityRef,
                new Function<EntityRef, Float>() {
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
        groupTable.row();

        table.add(groupTable).growX();
        table.row();
    }

    @Override
    public void entityMoved(EntityRef entityRef, float x, float y) {

    }
}
