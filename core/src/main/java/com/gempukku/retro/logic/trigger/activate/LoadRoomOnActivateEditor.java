package com.gempukku.retro.logic.trigger.activate;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.gaming.editor.EntityComponentEditor;
import com.gempukku.secsy.gaming.editor.component.CommonEditors;
import com.google.common.base.Function;

import javax.annotation.Nullable;
import java.util.Map;

public class LoadRoomOnActivateEditor implements EntityComponentEditor {
    @Override
    public void appendEditor(Table table, Skin skin, final EntityRef entityRef, PositionUpdateCallback positionUpdateCallback) {
        Table groupTable = new Table(skin);
        CommonEditors.initializeGroupTable(groupTable, skin);

        groupTable.add(new Label("Activated Teleport", skin)).growX().colspan(2);
        groupTable.row();

        CommonEditors.appendLabel(groupTable, skin, "Room Path");
        CommonEditors.appendStringField(groupTable, skin, entityRef, null,
                new Function<EntityRef, String>() {
                    @Nullable
                    @Override
                    public String apply(@Nullable EntityRef entityRef) {
                        return entityRef.getComponent(LoadRoomOnActivateComponent.class).getRoomPath();
                    }
                },
                new Function<TextField, Void>() {
                    @Nullable
                    @Override
                    public Void apply(@Nullable TextField textField) {
                        String value = textField.getText();
                        if (Gdx.files.internal(value).exists()) {
                            LoadRoomOnActivateComponent component = entityRef.getComponent(LoadRoomOnActivateComponent.class);
                            component.setRoomPath(value);
                            entityRef.saveChanges();

                            textField.setColor(Color.WHITE);
                        } else {
                            textField.setColor(Color.RED);
                        }
                        return null;
                    }
                });

        groupTable.row();

        CommonEditors.appendLabel(groupTable, skin, "Spawn Id");
        CommonEditors.appendStringField(groupTable, skin, entityRef, null,
                new Function<EntityRef, String>() {
                    @Nullable
                    @Override
                    public String apply(@Nullable EntityRef entityRef) {
                        return entityRef.getComponent(LoadRoomOnActivateComponent.class).getSpawnId();
                    }
                },
                new Function<TextField, Void>() {
                    @Nullable
                    @Override
                    public Void apply(@Nullable TextField textField) {
                        String value = textField.getText();
                        if (Gdx.files.internal(value).exists()) {
                            LoadRoomOnActivateComponent component = entityRef.getComponent(LoadRoomOnActivateComponent.class);
                            component.setSpawnId(value);
                            entityRef.saveChanges();

                            textField.setColor(Color.WHITE);
                        } else {
                            textField.setColor(Color.RED);
                        }
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
    public void serializeChanges(EntityRef entityRef, Map<String, Object> changes, Map<String, Map<String, Object>> extraChanges) {
        LoadRoomOnActivateComponent component = entityRef.getComponent(LoadRoomOnActivateComponent.class);
        changes.put("roomPath", component.getRoomPath());
        changes.put("spawnId", component.getSpawnId());
    }
}
