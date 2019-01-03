package com.gempukku.secsy.gaming.editor.component;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.gaming.camera2d.component.ClampCameraComponent;
import com.gempukku.secsy.gaming.editor.EntityComponentEditor;
import com.google.common.base.Function;

import javax.annotation.Nullable;
import java.util.Map;

public class ClampCameraEditor implements EntityComponentEditor {
    @Override
    public void appendEditor(Table table, Skin skin, final EntityRef entityRef, PositionUpdateCallback positionUpdateCallback) {
        Table groupTable = new Table(skin);
        CommonEditors.initializeGroupTable(groupTable, skin);

        groupTable.add(new Label("Clamp Camera", skin)).growX().colspan(4);
        groupTable.row();

        CommonEditors.appendLabel(groupTable, skin, "Min X");
        CommonEditors.appendFloatField(groupTable, skin, entityRef,
                new Function<EntityRef, Float>() {
                    @Nullable
                    @Override
                    public Float apply(@Nullable EntityRef entityRef) {
                        return entityRef.getComponent(ClampCameraComponent.class).getMinX();
                    }
                },
                new Function<Float, Void>() {
                    @Nullable
                    @Override
                    public Void apply(@Nullable Float value) {
                        ClampCameraComponent clampCamera = entityRef.getComponent(ClampCameraComponent.class);
                        clampCamera.setMinX(value);
                        entityRef.saveChanges();
                        return null;
                    }
                });
        CommonEditors.appendLabel(groupTable, skin, "Max X");
        CommonEditors.appendFloatField(groupTable, skin, entityRef,
                new Function<EntityRef, Float>() {
                    @Nullable
                    @Override
                    public Float apply(@Nullable EntityRef entityRef) {
                        return entityRef.getComponent(ClampCameraComponent.class).getMaxX();
                    }
                },
                new Function<Float, Void>() {
                    @Nullable
                    @Override
                    public Void apply(@Nullable Float value) {
                        ClampCameraComponent clampCamera = entityRef.getComponent(ClampCameraComponent.class);
                        clampCamera.setMaxX(value);
                        entityRef.saveChanges();
                        return null;
                    }
                });
        groupTable.row();

        CommonEditors.appendLabel(groupTable, skin, "Min Y");
        CommonEditors.appendFloatField(groupTable, skin, entityRef,
                new Function<EntityRef, Float>() {
                    @Nullable
                    @Override
                    public Float apply(@Nullable EntityRef entityRef) {
                        return entityRef.getComponent(ClampCameraComponent.class).getMinY();
                    }
                },
                new Function<Float, Void>() {
                    @Nullable
                    @Override
                    public Void apply(@Nullable Float value) {
                        ClampCameraComponent clampCamera = entityRef.getComponent(ClampCameraComponent.class);
                        clampCamera.setMinX(value);
                        entityRef.saveChanges();
                        return null;
                    }
                });
        CommonEditors.appendLabel(groupTable, skin, "Max Y");
        CommonEditors.appendFloatField(groupTable, skin, entityRef,
                new Function<EntityRef, Float>() {
                    @Nullable
                    @Override
                    public Float apply(@Nullable EntityRef entityRef) {
                        return entityRef.getComponent(ClampCameraComponent.class).getMaxX();
                    }
                },
                new Function<Float, Void>() {
                    @Nullable
                    @Override
                    public Void apply(@Nullable Float value) {
                        ClampCameraComponent clampCamera = entityRef.getComponent(ClampCameraComponent.class);
                        clampCamera.setMaxY(value);
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

    @Override
    public void serializeChanges(EntityRef entityRef, Map<String, Object> changes) {
        ClampCameraComponent clampCamera = entityRef.getComponent(ClampCameraComponent.class);
        changes.put("minX", clampCamera.getMinX());
        changes.put("maxX", clampCamera.getMaxX());
        changes.put("minY", clampCamera.getMinY());
        changes.put("maxY", clampCamera.getMaxY());
    }
}
