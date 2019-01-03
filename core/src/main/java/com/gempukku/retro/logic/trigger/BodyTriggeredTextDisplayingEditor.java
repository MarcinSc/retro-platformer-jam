package com.gempukku.retro.logic.trigger;

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

public class BodyTriggeredTextDisplayingEditor implements EntityComponentEditor {
    @Override
    public void appendEditor(Table table, Skin skin, final EntityRef entityRef, PositionUpdateCallback positionUpdateCallback) {
        Table groupTable = new Table(skin);
        CommonEditors.initializeGroupTable(groupTable, skin);

        groupTable.add(new Label("Display Text Body Trigger", skin)).growX().colspan(2);
        groupTable.row();

        CommonEditors.appendLabel(groupTable, skin, "Text");
        CommonEditors.appendStringField(groupTable, skin, entityRef, null,
                new Function<EntityRef, String>() {
                    @Nullable
                    @Override
                    public String apply(@Nullable EntityRef entityRef) {
                        return entityRef.getComponent(BodyTriggeredTextDisplayingComponent.class).getDisplayText();
                    }
                },
                new Function<TextField, Void>() {
                    @Nullable
                    @Override
                    public Void apply(@Nullable TextField textField) {
                        BodyTriggeredTextDisplayingComponent display = entityRef.getComponent(BodyTriggeredTextDisplayingComponent.class);
                        display.setDisplayText(textField.getText());
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
        BodyTriggeredTextDisplayingComponent component = entityRef.getComponent(BodyTriggeredTextDisplayingComponent.class);
        changes.put("displayText", component.getDisplayText());
    }
}
