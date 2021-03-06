package com.gempukku.secsy.gaming.editor.component;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.gaming.easing.EasingPreview;
import com.gempukku.secsy.gaming.easing.EasingResolver;
import com.google.common.base.Function;

import javax.annotation.Nullable;

public class CommonEditors {
    private static final int LABEL_MAX_WIDTH = 50;
    private static final int MIN_GROUP_PADDING = 3;
    public static final TextField.TextFieldFilter FLOAT_FILTER = new FloatNumberTextFieldFilter();
    public static final TextField.TextFieldFilter INTEGER_FILTER = new IntegerNumberTextFieldFilter();

    private CommonEditors() {
    }

    public static Label appendLabel(Table table, Skin skin, String label) {
        Label result = new Label(label, skin);
        table.add(result).minWidth(LABEL_MAX_WIDTH);
        return result;
    }

    public static CheckBox appendCheckbox(Table table, Skin skin, String displayName, EntityRef entityRef,
                                          Function<EntityRef, Boolean> fieldValue, final Function<Boolean, Void> valueSetter) {
        final CheckBox checkBox = new CheckBox(displayName, skin);
        checkBox.setChecked(fieldValue.apply(entityRef));
        checkBox.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        valueSetter.apply(checkBox.isChecked());
                    }
                });
        checkBox.align(Align.left);
        table.add(checkBox).growX();
        return checkBox;
    }

    public static EasingPreview appendEasingPreview(Table table, Skin skin, EasingResolver resolver, String recipe) {
        EasingPreview easingPreview = new EasingPreview(resolver, skin, recipe);
        table.add(easingPreview).growX();
        return easingPreview;
    }

    public static TextField appendStringField(Table table, final Skin skin, final EntityRef entityRef, TextField.TextFieldFilter filter,
                                              final Function<EntityRef, String> fieldValue, final Function<TextField, Void> valueSetter) {
        final TextField firstEditor = new TextField(fieldValue.apply(entityRef), skin) {
            @Override
            public float getPrefWidth() {
                return 80;
            }
        };
        firstEditor.setTextFieldFilter(filter);
        firstEditor.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        valueSetter.apply(firstEditor);
                    }
                });
        table.add(firstEditor).growX();
        return firstEditor;
    }

    public static TextField appendFloatField(Table table, Skin skin, EntityRef entityRef,
                                             final Function<EntityRef, Float> fieldValue, final Function<Float, Void> fieldSetter) {
        TextField textField = appendStringField(table, skin, entityRef, FLOAT_FILTER,
                new Function<EntityRef, String>() {
                    @Nullable
                    @Override
                    public String apply(@Nullable EntityRef entityRef) {
                        return String.valueOf(fieldValue.apply(entityRef));
                    }
                },
                new Function<TextField, Void>() {
                    @Nullable
                    @Override
                    public Void apply(@Nullable TextField textField) {
                        try {
                            float value = Float.parseFloat(textField.getText());
                            fieldSetter.apply(value);
                            textField.setColor(Color.WHITE);
                        } catch (NumberFormatException exp) {
                            // Ignore
                            textField.setColor(Color.RED);
                        }
                        return null;
                    }
                });
        textField.setAlignment(Align.right);
        return textField;
    }

    public static void initializeGroupTable(Table groupTable, Skin skin) {
        Drawable background = skin.get("default-round", Drawable.class);
        groupTable.setBackground(background);
        groupTable.pad(Math.max(MIN_GROUP_PADDING, background.getTopHeight()), Math.max(MIN_GROUP_PADDING, background.getLeftWidth()),
                Math.max(MIN_GROUP_PADDING, background.getBottomHeight()), Math.max(MIN_GROUP_PADDING, background.getRightWidth()));
    }
}
