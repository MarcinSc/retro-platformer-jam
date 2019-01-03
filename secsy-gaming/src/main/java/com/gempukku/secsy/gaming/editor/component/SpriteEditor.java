package com.gempukku.secsy.gaming.editor.component;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.gaming.editor.EntityComponentEditor;
import com.gempukku.secsy.gaming.rendering.sprite.SpriteComponent;
import com.google.common.base.Function;

import javax.annotation.Nullable;
import java.util.Map;

public class SpriteEditor implements EntityComponentEditor {
    @Override
    public void appendEditor(Table table, Skin skin, final EntityRef entityRef, PositionUpdateCallback positionUpdateCallback) {
        Table groupTable = new Table(skin);
        CommonEditors.initializeGroupTable(groupTable, skin);

        groupTable.add(new Label("Sprite", skin)).growX().colspan(2);
        groupTable.row();

        CommonEditors.appendLabel(groupTable, skin, "Path");
        CommonEditors.appendStringField(groupTable, skin, entityRef, null,
                new Function<EntityRef, String>() {
                    @Nullable
                    @Override
                    public String apply(@Nullable EntityRef entityRef) {
                        return entityRef.getComponent(SpriteComponent.class).getFileName();
                    }
                },
                new Function<TextField, Void>() {
                    @Nullable
                    @Override
                    public Void apply(@Nullable TextField textField) {
                        String value = textField.getText();
                        if (Gdx.files.internal(value).exists()) {
                            SpriteComponent component = entityRef.getComponent(SpriteComponent.class);
                            component.setFileName(value);
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
        SpriteComponent component = entityRef.getComponent(SpriteComponent.class);
        changes.put("fileName", component.getFileName());
    }
}
