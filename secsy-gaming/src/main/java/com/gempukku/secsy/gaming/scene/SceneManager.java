package com.gempukku.secsy.gaming.scene;

import com.badlogic.gdx.files.FileHandle;

public interface SceneManager {
    void unloadScene();

    void loadScene(String scene);

    void loadScene(FileHandle fileHandle);
}
