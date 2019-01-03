package com.gempukku.secsy.gaming.editor.component;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.gempukku.secsy.entity.EntityRef;
import com.google.common.base.Function;

import javax.annotation.Nullable;

public class CommonEditors {
    private static final int LABEL_MAX_WIDTH = 50;
    public static final TextField.TextFieldFilter FLOAT_FILTER = new FloatNumberTextFieldFilter();
    public static final TextField.TextFieldFilter INTEGER_FILTER = new IntegerNumberTextFieldFilter();

    private CommonEditors() {
    }

    public static TextField appendStringField(Table table, final Skin skin, final EntityRef entityRef, String label, TextField.TextFieldFilter filter,
                                              final Function<EntityRef, String> fieldValue, final Function<TextField, Void> valueSetter) {
        table.add(new Label(label, skin)).minWidth(LABEL_MAX_WIDTH);

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

    public static TextField appendFloatField(Table table, Skin skin, EntityRef entityRef, String firstFieldLabel,
                                             final Function<EntityRef, Float> fieldValue, final Function<Float, Void> fieldSetter) {
        TextField textField = appendStringField(table, skin, entityRef, firstFieldLabel, FLOAT_FILTER,
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
}
