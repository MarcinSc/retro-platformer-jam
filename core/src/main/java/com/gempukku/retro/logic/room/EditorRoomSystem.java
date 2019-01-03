package com.gempukku.retro.logic.room;

import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.gaming.scene.SceneManager;
import com.gempukku.secsy.gaming.spawn.SpawnManager;

@RegisterSystem(profiles = "editor")
public class EditorRoomSystem extends AbstractLifeCycleSystem {
    @Inject
    private SpawnManager spawnManager;
    @Inject
    private SceneManager sceneManager;

    @Override
    public float getPriority() {
        return -1000;
    }

    @Override
    public void initialize() {
        spawnManager.spawnEntity("editorTime");
        spawnManager.spawnEntity("editorEntity");
        sceneManager.loadScene("scenes/editor.scene");
    }
}
