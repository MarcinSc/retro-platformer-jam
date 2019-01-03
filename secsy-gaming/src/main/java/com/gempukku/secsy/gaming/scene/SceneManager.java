package com.gempukku.secsy.gaming.scene;

import com.badlogic.gdx.files.FileHandle;
import com.gempukku.secsy.entity.EntityRef;
import com.google.common.base.Function;
import com.google.common.base.Predicate;

import java.util.Map;

public interface SceneManager {
    void unloadScene();

    void loadScene(String scene);

    void loadScene(FileHandle fileHandle);

    void saveScene(FileHandle fileHandle, Predicate<EntityRef> filter,
                   Function<EntityRef, Map<String, Map<String, Object>>> serializeOverrides);
}
