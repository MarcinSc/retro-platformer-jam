package com.gempukku.secsy.gaming.editor.component;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.gaming.component.HorizontalOrientationComponent;
import com.gempukku.secsy.gaming.editor.EntityComponentEditor;
import com.google.common.base.Function;

import javax.annotation.Nullable;
import java.util.Map;

public class HorizontalOrientationEditor implements EntityComponentEditor {
    @Override
    public void appendEditor(Table table, Skin skin, final EntityRef entityRef, PositionUpdateCallback positionUpdateCallback) {
        Table groupTable = new Table(skin);
        CommonEditors.initializeGroupTable(groupTable, skin);

        groupTable.add(new Label("Orientation", skin)).growX().colspan(2);
        groupTable.row();

        CommonEditors.appendLabel(groupTable, skin, "Face right");
        CommonEditors.appendCheckbox(groupTable, skin, entityRef,
                new Function<EntityRef, Boolean>() {
                    @Nullable
                    @Override
                    public Boolean apply(@Nullable EntityRef entityRef) {
                        return entityRef.getComponent(HorizontalOrientationComponent.class).isFacingRight();
                    }
                },
                new Function<Boolean, Void>() {
                    @Nullable
                    @Override
                    public Void apply(@Nullable Boolean aBoolean) {
                        HorizontalOrientationComponent orientation = entityRef.getComponent(HorizontalOrientationComponent.class);
                        orientation.setFacingRight(aBoolean);
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
    public void serializeChanges(EntityRef entityRef, Map<String, Object> changes, Map<String, Map<String, Object>> extraChanges) {
        HorizontalOrientationComponent orientation = entityRef.getComponent(HorizontalOrientationComponent.class);
        changes.put("facingRight", orientation.isFacingRight());
    }
}
