package com.gempukku.secsy.gaming.editor.generic.type;

import com.badlogic.gdx.Gdx;

public class FileExistsValidator implements StringValueValidator {
    @Override
    public boolean isValid(String value) {
        return Gdx.files.internal(value).exists();
    }
}
