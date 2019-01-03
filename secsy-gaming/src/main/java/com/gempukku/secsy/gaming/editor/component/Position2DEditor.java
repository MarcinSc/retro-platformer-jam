package com.gempukku.secsy.gaming.editor.component;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.gaming.component.Position2DComponent;
import com.gempukku.secsy.gaming.editor.EntityComponentEditor;
import com.google.common.base.Function;

import javax.annotation.Nullable;

public class Position2DEditor implements EntityComponentEditor {
    private TextField xField;
    private TextField yField;

    private boolean selfUpdating;

    @Override
    public void appendEditor(Table table, Skin skin, final EntityRef entityRef, final PositionUpdateCallback positionUpdateCallback) {
        Table groupTable = new Table(skin);
        Drawable background = skin.get("default-round", Drawable.class);
        groupTable.setBackground(background);
        groupTable.pad(background.getTopHeight(), background.getLeftWidth(), background.getBottomHeight(), background.getRightWidth());

        groupTable.add(new Label("Position", skin)).growX().colspan(4);
        groupTable.row();

        CommonEditors.appendLabel(groupTable, skin, "X");
        xField = CommonEditors.appendFloatField(groupTable, skin, entityRef,
                new Function<EntityRef, Float>() {
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
                        selfUpdating = true;
                        positionUpdateCallback.positionUpdated(entityRef);
                        selfUpdating = false;
                        return null;
                    }
                });
        CommonEditors.appendLabel(groupTable, skin, "Y");
        yField = CommonEditors.appendFloatField(groupTable, skin, entityRef,
                new Function<EntityRef, Float>() {
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
                        selfUpdating = true;
                        positionUpdateCallback.positionUpdated(entityRef);
                        selfUpdating = false;
                        return null;
                    }
                });
        groupTable.row();

        table.add(groupTable).growX();
        table.row();
    }

    @Override
    public void entityMoved(EntityRef entityRef, float x, float y) {
        if (!selfUpdating) {
            xField.setText(String.valueOf(x));
            yField.setText(String.valueOf(y));
        }
    }
}
