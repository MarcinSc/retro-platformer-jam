package com.gempukku.secsy.gaming.editor.component;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.gaming.ai.AIComponent;
import com.gempukku.secsy.gaming.ai.AIEngine;
import com.gempukku.secsy.gaming.component.HorizontalOrientationComponent;
import com.gempukku.secsy.gaming.editor.EntityComponentEditor;
import com.google.common.base.Function;

import javax.annotation.Nullable;
import java.util.Map;

public class AINameEditor implements EntityComponentEditor {
    @Inject
    private AIEngine aiEngine;

    @Override
    public void appendEditor(Table table, Skin skin, final EntityRef entityRef, PositionUpdateCallback positionUpdateCallback) {
        Table groupTable = new Table(skin);
        CommonEditors.initializeGroupTable(groupTable, skin);

        groupTable.add(new Label("AI", skin)).growX().colspan(2);
        groupTable.row();

        CommonEditors.appendLabel(groupTable, skin, "Behavior");
        CommonEditors.appendStringField(groupTable, skin, entityRef, null,
                new Function<EntityRef, String>() {
                    @Nullable
                    @Override
                    public String apply(@Nullable EntityRef entityRef) {
                        return entityRef.getComponent(AIComponent.class).getAiName();
                    }
                },
                new Function<TextField, Void>() {
                    @Nullable
                    @Override
                    public Void apply(@Nullable TextField textField) {
                        if (aiEngine.hasBehavior(textField.getText())) {
                            AIComponent ai = entityRef.getComponent(AIComponent.class);
                            ai.setAiName(textField.getName());
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
    public void serializeChanges(EntityRef entityRef, Map<String, Object> changes) {
        HorizontalOrientationComponent orientation = entityRef.getComponent(HorizontalOrientationComponent.class);
        changes.put("facingRight", orientation.isFacingRight());
    }
}
