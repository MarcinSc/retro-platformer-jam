package com.gempukku.secsy.gaming.editor.component;

import com.badlogic.gdx.scenes.scene2d.ui.TextField;

public class FloatNumberTextFieldFilter implements TextField.TextFieldFilter {
    @Override
    public boolean acceptChar(TextField textField, char c) {
        return c == '.' || Character.isDigit(c);
    }
}
