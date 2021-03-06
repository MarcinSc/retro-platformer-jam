package com.gempukku.secsy.gaming.movement;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.gaming.easing.EasedValue;
import com.gempukku.secsy.gaming.easing.EasingPreview;
import com.gempukku.secsy.gaming.easing.EasingResolver;
import com.gempukku.secsy.gaming.editor.EntityComponentEditor;
import com.gempukku.secsy.gaming.editor.component.CommonEditors;
import com.google.common.base.Function;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;

public class OscillatingEditor implements EntityComponentEditor {
    @Inject
    private EasingResolver resolver;
    private EasingPreview preview;
    private boolean selfUpdating;
    private TextField xField;
    private TextField yField;

    @Override
    public void appendEditor(Table table, Skin skin, final EntityRef entityRef, final PositionUpdateCallback positionUpdateCallback) {
        Table groupTable = new Table(skin);
        CommonEditors.initializeGroupTable(groupTable, skin);

        groupTable.add(new Label("Oscillating", skin)).growX().colspan(4);
        groupTable.row();

        CommonEditors.appendLabel(groupTable, skin, "X");
        xField = CommonEditors.appendFloatField(groupTable, skin, entityRef,
                new Function<EntityRef, Float>() {
                    @Nullable
                    @Override
                    public Float apply(@Nullable EntityRef entityRef) {
                        return entityRef.getComponent(OscillatingComponent.class).getStartingPosition().x;
                    }
                }, new Function<Float, Void>() {
                    @Nullable
                    @Override
                    public Void apply(@Nullable Float value) {
                        OscillatingComponent position = entityRef.getComponent(OscillatingComponent.class);
                        position.setStartingPosition(new Vector2(value, position.getStartingPosition().y));
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
                        return entityRef.getComponent(OscillatingComponent.class).getStartingPosition().y;
                    }
                }, new Function<Float, Void>() {
                    @Nullable
                    @Override
                    public Void apply(@Nullable Float value) {
                        OscillatingComponent position = entityRef.getComponent(OscillatingComponent.class);
                        position.setStartingPosition(new Vector2(position.getStartingPosition().x, value));
                        entityRef.saveChanges();
                        selfUpdating = true;
                        positionUpdateCallback.positionUpdated(entityRef);
                        selfUpdating = false;
                        return null;
                    }
                });
        groupTable.row();

        CommonEditors.appendLabel(groupTable, skin, "Dist. X");
        CommonEditors.appendFloatField(groupTable, skin, entityRef,
                new Function<EntityRef, Float>() {
                    @Nullable
                    @Override
                    public Float apply(@Nullable EntityRef entityRef) {
                        return entityRef.getComponent(OscillatingComponent.class).getDistance().x;
                    }
                }, new Function<Float, Void>() {
                    @Nullable
                    @Override
                    public Void apply(@Nullable Float value) {
                        OscillatingComponent oscillating = entityRef.getComponent(OscillatingComponent.class);
                        oscillating.setDistance(new Vector2(value, oscillating.getDistance().y));
                        entityRef.saveChanges();
                        return null;
                    }
                });
        CommonEditors.appendLabel(groupTable, skin, "Dist. Y");
        CommonEditors.appendFloatField(groupTable, skin, entityRef,
                new Function<EntityRef, Float>() {
                    @Nullable
                    @Override
                    public Float apply(@Nullable EntityRef entityRef) {
                        return entityRef.getComponent(OscillatingComponent.class).getDistance().y;
                    }
                }, new Function<Float, Void>() {
                    @Nullable
                    @Override
                    public Void apply(@Nullable Float value) {
                        OscillatingComponent oscillating = entityRef.getComponent(OscillatingComponent.class);
                        oscillating.setDistance(new Vector2(oscillating.getDistance().x, value));
                        entityRef.saveChanges();
                        return null;
                    }
                });
        groupTable.row();

        Label durationLabel = CommonEditors.appendLabel(groupTable, skin, "Duration (ms)");
        groupTable.getCell(durationLabel).colspan(2);
        TextField time = CommonEditors.appendStringField(groupTable, skin, entityRef,
                CommonEditors.INTEGER_FILTER,
                new Function<EntityRef, String>() {
                    @Nullable
                    @Override
                    public String apply(@Nullable EntityRef entityRef) {
                        return String.valueOf(entityRef.getComponent(OscillatingComponent.class).getCycleLength());
                    }
                }, new Function<TextField, Void>() {
                    @Nullable
                    @Override
                    public Void apply(@Nullable TextField textField) {
                        try {
                            long value = Long.parseLong(textField.getText());
                            OscillatingComponent oscillating = entityRef.getComponent(OscillatingComponent.class);
                            oscillating.setCycleLength(value);
                            entityRef.saveChanges();
                            textField.setColor(Color.WHITE);
                        } catch (NumberFormatException exp) {
                            // Ignore
                            textField.setColor(Color.RED);
                        }
                        return null;
                    }
                });
        time.setAlignment(Align.right);
        groupTable.getCell(time).colspan(2);
        groupTable.row();

        CommonEditors.appendLabel(groupTable, skin, "f(t)");
        TextField function = CommonEditors.appendStringField(groupTable, skin, entityRef,
                null,
                new Function<EntityRef, String>() {
                    @Nullable
                    @Override
                    public String apply(@Nullable EntityRef entityRef) {
                        return entityRef.getComponent(OscillatingComponent.class).getDistanceTimeFunction().getRecipe();
                    }
                }, new Function<TextField, Void>() {
                    @Nullable
                    @Override
                    public Void apply(@Nullable TextField textField) {
                        String recipe = textField.getText();
                        EasedValue value = new EasedValue(1, recipe);
                        try {
                            resolver.resolveValue(value, 0);
                            OscillatingComponent oscillating = entityRef.getComponent(OscillatingComponent.class);
                            oscillating.setDistanceTimeFunction(value);
                            entityRef.saveChanges();
                            preview.setRecipe(recipe);
                            textField.setColor(Color.WHITE);
                        } catch (Exception exp) {
                            // Ignore
                            textField.setColor(Color.RED);
                        }
                        return null;
                    }
                });
        function.setAlignment(Align.right);
        //function.setDisabled(true);
        groupTable.getCell(function).colspan(2);

        String recipe = entityRef.getComponent(OscillatingComponent.class).getDistanceTimeFunction().getRecipe();

        final TextButton editFunction = new TextButton("Edit f(t)", skin, "toggle");
        groupTable.add(editFunction);

        groupTable.row();

        preview = CommonEditors.appendEasingPreview(groupTable, skin, resolver, recipe);
        preview.setVisible(false);
        groupTable.getCell(preview).colspan(4);

        editFunction.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        preview.setVisible(editFunction.isChecked());
                        preview.invalidateHierarchy();
                    }
                });

        groupTable.row();

        table.add(groupTable).growX();
        table.row();
    }

    @Override
    public void entityMoved(EntityRef entityRef, float x, float y) {
        if (!selfUpdating) {
            OscillatingComponent oscillating = entityRef.getComponent(OscillatingComponent.class);
            oscillating.setStartingPosition(new Vector2(x, y));
            entityRef.saveChanges();

            xField.setText(String.valueOf(x));
            yField.setText(String.valueOf(y));
        }
    }

    @Override
    public void serializeChanges(EntityRef entityRef, Map<String, Object> changes, Map<String, Map<String, Object>> extraChanges) {
        OscillatingComponent oscillating = entityRef.getComponent(OscillatingComponent.class);
        Vector2 startingPosition = oscillating.getStartingPosition();
        changes.put("startingPosition", startingPosition.x + "," + startingPosition.y);
        Vector2 distance = oscillating.getDistance();
        changes.put("distance", distance.x + "," + distance.y);
        long cycleLength = oscillating.getCycleLength();
        changes.put("cycleLength", cycleLength);
        EasedValue distanceTimeFunction = oscillating.getDistanceTimeFunction();
        changes.put("distanceTimeFunction", distanceTimeFunction.getMultiplier() + "*" + distanceTimeFunction.getRecipe());

        Map<String, Object> positionChanges = new LinkedHashMap<String, Object>();
        positionChanges.put("x", startingPosition.x);
        positionChanges.put("y", startingPosition.y);
        extraChanges.put("Position2D", positionChanges);
    }
}
