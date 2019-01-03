package com.gempukku.retro.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.gempukku.retro.model.PlatformComponent;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.gaming.editor.EntityComponentEditor;
import com.gempukku.secsy.gaming.editor.component.CommonEditors;
import com.google.common.base.Function;

import javax.annotation.Nullable;
import java.util.Map;

public class PlatformEditor implements EntityComponentEditor {
    @Override
    public void appendEditor(Table table, Skin skin, final EntityRef entityRef, PositionUpdateCallback positionUpdateCallback) {
        Table groupTable = new Table(skin);
        CommonEditors.initializeGroupTable(groupTable, skin);

        groupTable.add(new Label("Platform", skin)).growX().colspan(2);
        groupTable.row();

        CommonEditors.appendLabel(groupTable, skin, "Start Image");
        CommonEditors.appendStringField(groupTable, skin, entityRef, null,
                new Function<EntityRef, String>() {
                    @Nullable
                    @Override
                    public String apply(@Nullable EntityRef entityRef) {
                        return entityRef.getComponent(PlatformComponent.class).getBeginningImage();
                    }
                },
                new Function<TextField, Void>() {
                    @Nullable
                    @Override
                    public Void apply(@Nullable TextField textField) {
                        String value = textField.getText();
                        if (value.equals("") || Gdx.files.internal(value).exists()) {
                            PlatformComponent ai = entityRef.getComponent(PlatformComponent.class);
                            ai.setBeginningImage(value);
                            entityRef.saveChanges();

                            textField.setColor(Color.WHITE);
                        } else {
                            textField.setColor(Color.RED);
                        }
                        return null;
                    }
                });

        groupTable.row();

        CommonEditors.appendLabel(groupTable, skin, "Center Image");
        CommonEditors.appendStringField(groupTable, skin, entityRef, null,
                new Function<EntityRef, String>() {
                    @Nullable
                    @Override
                    public String apply(@Nullable EntityRef entityRef) {
                        return entityRef.getComponent(PlatformComponent.class).getCenterImage();
                    }
                },
                new Function<TextField, Void>() {
                    @Nullable
                    @Override
                    public Void apply(@Nullable TextField textField) {
                        String value = textField.getText();
                        if (Gdx.files.internal(value).exists()) {
                            PlatformComponent ai = entityRef.getComponent(PlatformComponent.class);
                            ai.setCenterImage(value);
                            entityRef.saveChanges();

                            textField.setColor(Color.WHITE);
                        } else {
                            textField.setColor(Color.RED);
                        }
                        return null;
                    }
                });

        groupTable.row();

        CommonEditors.appendLabel(groupTable, skin, "Ending Image");
        CommonEditors.appendStringField(groupTable, skin, entityRef, null,
                new Function<EntityRef, String>() {
                    @Nullable
                    @Override
                    public String apply(@Nullable EntityRef entityRef) {
                        return entityRef.getComponent(PlatformComponent.class).getEndingImage();
                    }
                },
                new Function<TextField, Void>() {
                    @Nullable
                    @Override
                    public Void apply(@Nullable TextField textField) {
                        String value = textField.getText();
                        if (value.equals("") || Gdx.files.internal(value).exists()) {
                            PlatformComponent ai = entityRef.getComponent(PlatformComponent.class);
                            ai.setEndingImage(value);
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
        PlatformComponent platform = entityRef.getComponent(PlatformComponent.class);
        changes.put("beginningImage", platform.getBeginningImage());
        changes.put("centerImage", platform.getCenterImage());
        changes.put("endingImage", platform.getEndingImage());
    }
}
