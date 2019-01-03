package com.gempukku.retro.logic.room;

import com.gempukku.retro.logic.player.PlayerComponent;
import com.gempukku.retro.logic.player.PlayerProvider;
import com.gempukku.retro.logic.player.PlayerSpawnComponent;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.entity.EntityManager;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.entity.game.GameEntityProvider;
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
    @Inject
    private EntityManager entityManager;

    @Override
    public float getPriority() {
        return -1000;
    }

    @Override
    public void initialize() {
//        spawnManager.spawnEntity("gameTime");
//        spawnManager.spawnEntity("gameEntity");
//        spawnManager.spawnEntity("player");
//        reloadRoomFromGame();
        spawnManager.spawnEntity("editorTime");
        spawnManager.spawnEntity("editorEntity");
        sceneManager.loadScene("scenes/editor.scene");
    }

    @ReceiveEvent
    public void processLoadRoom(LoadRoom loadRoom) {
        unloadRoomEntities();
        loadRoom(loadRoom.getRoomPath(), loadRoom.getSpawnId());
    }

    @ReceiveEvent
    public void playerDied(EntityDied entityDied, EntityRef entity, PlayerComponent player) {
        reloadRoomFromGame();
    }

    private void reloadRoomFromGame() {
        RoomComponent room = gameEntityProvider.getGameEntity().getComponent(RoomComponent.class);
        unloadRoomEntities();
        loadRoom(room.getRoom(), room.getSpawnId());
    }

    private void unloadRoomEntities() {
        sceneManager.unloadScene();
    }

    private void loadRoom(String roomFile, String spawnId) {
        sceneManager.loadScene(roomFile);

        updateGame(roomFile, spawnId);
        updatePlayerPosition(spawnId);
    }

    private void updatePlayerPosition(String spawnId) {
        for (EntityRef entityRef : entityManager.getEntitiesWithComponents(PlayerSpawnComponent.class)) {
            PlayerSpawnComponent playerSpawn = entityRef.getComponent(PlayerSpawnComponent.class);
            if (playerSpawn.getSpawnId().equals(spawnId)) {
                Position2DComponent spawnPosition = entityRef.getComponent(Position2DComponent.class);
                EntityRef player = playerProvider.getPlayer();
                Position2DComponent position = player.getComponent(Position2DComponent.class);
                position.setX(spawnPosition.getX());
                position.setY(spawnPosition.getY());
                player.saveChanges();
            }
        }
    }

    private void updateGame(String roomFile, String spawnId) {
        EntityRef gameEntity = gameEntityProvider.getGameEntity();
        RoomComponent roomComp = gameEntity.getComponent(RoomComponent.class);
        roomComp.setRoom(roomFile);
        roomComp.setSpawnId(spawnId);
        gameEntity.saveChanges();
    }
}
