package com.gempukku.secsy.gaming.editor.component;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.gempukku.secsy.entity.EntityRef;
import com.google.common.base.Function;

public class CommonEditors {
    private static final int LABEL_MAX_WIDTH = 50;
    private static final FloatNumberTextFieldFilter FLOAT_FILTER = new FloatNumberTextFieldFilter();

    private CommonEditors() {
    }

    public static void appendTwoFloatFieldsEditor(
            Table table, Skin skin, final EntityRef entityRef, String groupLabel,
            String firstFieldLabel, Function<EntityRef, Float> firstFieldValue, final Function<Float, Void> firstFieldSetter,
            String secondFieldLabel, Function<EntityRef, Float> secondFieldValue, final Function<Float, Void> secondFieldSetter) {
        Table groupTable = new Table(skin);
        Drawable background = skin.get("default-round", Drawable.class);
        groupTable.setBackground(background);
        groupTable.pad(background.getTopHeight(), background.getLeftWidth(), background.getBottomHeight(), background.getRightWidth());

        groupTable.add(new Label(groupLabel, skin)).growX().colspan(4);
        groupTable.row();

        groupTable.add(new Label(firstFieldLabel, skin)).minWidth(LABEL_MAX_WIDTH);

        final TextField firstEditor = new TextField(String.valueOf(firstFieldValue.apply(entityRef)), skin) {
            @Override
            public float getPrefWidth() {
                return 80;
            }
        };
        firstEditor.setTextFieldFilter(FLOAT_FILTER);
        firstEditor.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        String text = firstEditor.getText();
                        try {
                            float value = Float.parseFloat(text);
                            firstFieldSetter.apply(value);
                            firstEditor.setColor(Color.WHITE);
                        } catch (NumberFormatException exp) {
                            // Ignore
                            firstEditor.setColor(Color.RED);
                        }
                    }
                });
        groupTable.add(firstEditor).growX();

        groupTable.add(new Label(secondFieldLabel, skin)).minWidth(LABEL_MAX_WIDTH);

        final TextField secondEditor = new TextField(String.valueOf(secondFieldValue.apply(entityRef)), skin) {
            @Override
            public float getPrefWidth() {
                return 80;
            }
        };
        secondEditor.setTextFieldFilter(FLOAT_FILTER);
        secondEditor.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        String text = secondEditor.getText();
                        try {
                            float value = Float.parseFloat(text);
                            secondFieldSetter.apply(value);
                            secondEditor.setColor(Color.WHITE);
                        } catch (NumberFormatException exp) {
                            // Ignore
                            secondEditor.setColor(Color.RED);
                        }
                    }
                });
        groupTable.add(secondEditor).growX();

        table.add(groupTable).growX();
        table.row();
    }
}
