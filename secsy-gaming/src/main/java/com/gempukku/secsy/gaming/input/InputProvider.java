package com.gempukku.secsy.gaming.input;

import com.badlogic.gdx.InputProcessor;

public interface InputProvider {
    void registerInputProcessor(InputProcessor inputProcessor, float priority);

    void deregisterInputProcessor(InputProcessor inputProcessor);
}
