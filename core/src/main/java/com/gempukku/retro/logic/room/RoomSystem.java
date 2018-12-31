package com.gempukku.retro.logic.room;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.gempukku.retro.logic.player.PlayerProvider;
import com.gempukku.retro.model.PlayerComponent;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.entity.game.GameEntityProvider;
import com.gempukku.secsy.entity.game.GameLoopUpdate;
import com.gempukku.secsy.gaming.combat.EntityDied;
import com.gempukku.secsy.gaming.component.Position2DComponent;
import com.gempukku.secsy.gaming.scene.SceneManager;
import com.gempukku.secsy.gaming.spawn.SpawnManager;

@RegisterSystem
public class RoomSystem extends AbstractLifeCycleSystem {
    @Inject
    private GameEntityProvider gameEntityProvider;
    @Inject
    private SpawnManager spawnManager;
    @Inject
    private PlayerProvider playerProvider;
    @Inject
    private SceneManager sceneManager;

    public static final int RELOAD_KEY = Input.Keys.R;

    @Override
    public float getPriority() {
        return -1000;
    }

    @Override
    public void initialize() {
        spawnManager.spawnEntity("gameTime");
        spawnManager.spawnEntity("gameEntity");
        spawnManager.spawnEntity("player");
        reloadRoomFromGame();
//        spawnManager.spawnEntity("editorTime");
//        spawnManager.spawnEntity("editorEntity");
//        sceneManager.loadScene("scenes/editor.scene");
    }

    @ReceiveEvent
    public void processLoadRoom(LoadRoom loadRoom) {
        unloadRoomEntities();
        loadRoom(loadRoom.getRoomPath(), loadRoom.getX(), loadRoom.getY());
    }

    private boolean reloadPressed;

    @ReceiveEvent
    public void reloadRoom(GameLoopUpdate update) {
        if (Gdx.input.isKeyPressed(RELOAD_KEY) && !reloadPressed) {
            reloadRoomFromGame();
            reloadPressed = true;
        } else if (!Gdx.input.isKeyPressed(RELOAD_KEY)) {
            reloadPressed = false;
        }
    }

    @ReceiveEvent
    public void playerDied(EntityDied entityDied, EntityRef entity, PlayerComponent player) {
        reloadRoomFromGame();
    }

    private void reloadRoomFromGame() {
        RoomComponent room = gameEntityProvider.getGameEntity().getComponent(RoomComponent.class);
        unloadRoomEntities();
        loadRoom(room.getRoom(), room.getX(), room.getY());
    }

    private void unloadRoomEntities() {
        sceneManager.unloadScene();
    }

    private void loadRoom(String roomFile, float x, float y) {
        sceneManager.loadScene(roomFile);

        updateGame(roomFile, x, y);
        updatePlayerPosition(x, y);
    }

    private void updatePlayerPosition(float x, float y) {
        EntityRef player = playerProvider.getPlayer();
        Position2DComponent position = player.getComponent(Position2DComponent.class);
        position.setX(x);
        position.setY(y);
        player.saveChanges();
    }

    private void updateGame(String roomFile, float x, float y) {
        EntityRef gameEntity = gameEntityProvider.getGameEntity();
        RoomComponent roomComp = gameEntity.getComponent(RoomComponent.class);
        roomComp.setRoom(roomFile);
        roomComp.setX(x);
        roomComp.setY(y);
        gameEntity.saveChanges();
    }
}
