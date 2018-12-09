package com.gempukku.retro.logic.room;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.gempukku.retro.logic.combat.EntityDied;
import com.gempukku.retro.logic.player.PlayerProvider;
import com.gempukku.retro.logic.spawn.SpawnManager;
import com.gempukku.retro.model.*;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.entity.EntityManager;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.entity.game.GameEntityProvider;
import com.gempukku.secsy.entity.game.GameLoopUpdate;
import com.gempukku.secsy.gaming.camera2d.component.ClampCameraComponent;
import com.gempukku.secsy.gaming.component.Bounds2DComponent;
import com.gempukku.secsy.gaming.component.Position2DComponent;
import com.gempukku.secsy.gaming.physics.basic2d.ObstacleComponent;
import com.gempukku.secsy.gaming.physics.basic2d.SensorTriggerComponent;
import com.gempukku.secsy.gaming.rendering.pipeline.CameraEntityProvider;
import com.gempukku.secsy.gaming.rendering.pipeline.RenderToPipeline;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.List;

@RegisterSystem
public class RoomSystem extends AbstractLifeCycleSystem {
    @Inject
    private GameEntityProvider gameEntityProvider;
    @Inject
    private EntityManager entityManager;
    @Inject
    private CameraEntityProvider cameraEntityProvider;
    @Inject
    private SpawnManager spawnManager;
    @Inject
    private PlayerProvider playerProvider;

    public static final int RELOAD_KEY = Input.Keys.R;
    public static final int SAVE_KEY = Input.Keys.T;

    private boolean roomLoaded = false;

    @ReceiveEvent
    public void update(GameLoopUpdate update) {
        if (!roomLoaded) {
//            loadRoom(-0.6f, 0);
//            roomLoaded = true;
            spawnManager.spawnEntity("player");
            reloadRoomFromGame();
            roomLoaded = true;
        }
    }

    @ReceiveEvent
    public void processLoadRoom(LoadRoom loadRoom) {
        unloadRoomEntities();
        loadRoom(loadRoom.getRoomPath(), loadRoom.getX(), loadRoom.getY());
    }

    private void loadRoom(float x, float y) {
        spawnManager.spawnEntity("player");

        // Room size in units is 4 units / 3 units

        loadRoom("rooms/entrance.json", x, y);
    }

    private boolean reloadPressed;
    private boolean serializePressed;
    private boolean mouse1Pressed;
    private boolean mouse2Pressed;

    @ReceiveEvent
    public void reloadRoom(GameLoopUpdate update) {
        if (Gdx.input.isKeyPressed(RELOAD_KEY) && !reloadPressed) {
            unloadRoomEntities();

            loadRoom("rooms/room.json", -0.6f, 0);

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
        for (EntityRef entityRef : entityManager.getAllEntities()) {
            if (!entityRef.hasComponent(GlobalEntityComponent.class)
                    && !entityRef.hasComponent(PlayerComponent.class))
                entityManager.destroyEntity(entityRef);
        }
    }

    @ReceiveEvent
    public void placePlatform(RenderToPipeline renderToPipeline) {
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && !mouse1Pressed) {
            int x = Gdx.input.getX();
            int y = Gdx.input.getY();
            Vector3 position = renderToPipeline.getCamera().unproject(new Vector3(x, y, 0));

            createPlatform("platform", position.x, position.y, 0.2f, 0.1f);

            mouse1Pressed = true;
        } else if (!Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            mouse1Pressed = false;
        }
    }

    @ReceiveEvent
    public void removePlatform(RenderToPipeline renderToPipeline) {
        if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT) && !mouse2Pressed) {

            int x = Gdx.input.getX();
            int y = Gdx.input.getY();

            Vector3 worldCoords = renderToPipeline.getCamera().unproject(new Vector3(x, y, 0));

            EntityRef closestPlatform = findClosestPlatform(worldCoords.x, worldCoords.y);
            entityManager.destroyEntity(closestPlatform);

            mouse2Pressed = true;
        } else if (!Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
            mouse2Pressed = false;
        }
    }

    private EntityRef findClosestPlatform(float x, float y) {
        float shortestDistance = Float.MAX_VALUE;
        EntityRef result = null;
        for (EntityRef platformEntity : entityManager.getEntitiesWithComponents(PlatformComponent.class)) {
            Position2DComponent platform = platformEntity.getComponent(Position2DComponent.class);
            float distance = new Vector2(platform.getX(), platform.getY()).dst(x, y);
            if (distance < shortestDistance) {
                shortestDistance = distance;
                result = platformEntity;
            }
        }

        return result;
    }

    @ReceiveEvent
    public void saveRoom(GameLoopUpdate update) throws UnsupportedEncodingException {
        if (Gdx.input.isKeyPressed(SAVE_KEY) && !serializePressed) {
            JSONObject result = new JSONObject();
            JSONArray platforms = new JSONArray();
            for (EntityRef platformEntity : entityManager.getEntitiesWithComponents(PlatformComponent.class)) {
                Position2DComponent position = platformEntity.getComponent(Position2DComponent.class);
                PlatformComponent platform = platformEntity.getComponent(PlatformComponent.class);
                PrefabComponent prefab = platformEntity.getComponent(PrefabComponent.class);
                JSONObject plObj = new JSONObject();
                plObj.put("prefab", prefab.getPrefab());
                plObj.put("x", position.getX());
                plObj.put("y", position.getY());
                plObj.put("width", platform.getRight());
                plObj.put("height", -platform.getDown());

                platforms.add(plObj);
            }
            result.put("platforms", platforms);
            JSONArray pickups = new JSONArray();
            for (EntityRef pickupEntity : entityManager.getEntitiesWithComponents(PickupComponent.class)) {
                Position2DComponent position = pickupEntity.getComponent(Position2DComponent.class);
                PickupComponent pickup = pickupEntity.getComponent(PickupComponent.class);
                JSONObject pickupObj = new JSONObject();
                pickupObj.put("type", pickup.getType());
                pickupObj.put("x", position.getX());
                pickupObj.put("y", position.getY());

                pickups.add(pickupObj);
            }
            result.put("pickups", pickups);

            Gdx.files.absolute("/Users/marcin.sciesinski/private/retro-platformer-jam/core/src/main/resources/rooms/room.json").write(
                    new ByteArrayInputStream(result.toJSONString().getBytes("UTF-8")), false);

            serializePressed = true;
        } else if (!Gdx.input.isKeyPressed(SAVE_KEY)) {
            serializePressed = false;
        }
    }

    private void loadRoom(String roomFile, float x, float y) {
        JSONObject room = loadJSON(roomFile);

        updateGame(roomFile, x, y);
        updateCamera((JSONObject) room.get("cameraSettings"));
        updatePlayerPosition(x, y);

        JSONArray platformArray = (JSONArray) room.get("platforms");
        JSONArray pickupsArray = (JSONArray) room.get("pickups");
        JSONArray objectsArray = (JSONArray) room.get("objects");

        for (Object platform : platformArray) {
            JSONObject platformObj = (JSONObject) platform;
            createPlatform((String) platformObj.get("prefab"), getFloat(platformObj, "x"), getFloat(platformObj, "y"),
                    getFloat(platformObj, "width"), getFloat(platformObj, "height"));
        }
        List<String> items = playerProvider.getPlayer().getComponent(InventoryComponent.class).getItems();
        for (Object pickup : pickupsArray) {
            JSONObject pickupObj = (JSONObject) pickup;
            String pickupType = (String) pickupObj.get("type");
            // Only spawn pickups that have not been picked up yet
            if (!items.contains(pickupType))
                createPickup(pickupType, (String) pickupObj.get("image"),
                        getFloat(pickupObj, "x"), getFloat(pickupObj, "y"));
        }
        for (Object object : objectsArray) {
            JSONObject objectObj = (JSONObject) object;
            if (objectObj.get("x") != null)
                spawnManager.spawnEntityAt((String) objectObj.get("prefab"), getFloat(objectObj, "x"), getFloat(objectObj, "y"));
            else
                spawnManager.spawnEntity((String) objectObj.get("prefab"));
        }
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

    private void updateCamera(JSONObject cameraSettings) {
        JSONObject clampSettings = (JSONObject) cameraSettings.get("clamp");

        EntityRef cameraEntity = cameraEntityProvider.getCameraEntity();
        ClampCameraComponent clamp = cameraEntity.getComponent(ClampCameraComponent.class);
        clamp.setMinX(getFloat(clampSettings, "minX"));
        clamp.setMaxX(getFloat(clampSettings, "maxX"));
        clamp.setMinY(getFloat(clampSettings, "minY"));
        clamp.setMaxY(getFloat(clampSettings, "maxY"));
        cameraEntity.saveChanges();
    }

    private float getFloat(JSONObject object, String key) {
        return ((Number) object.get(key)).floatValue();
    }

    private JSONObject loadJSON(String roomFile) {
        FileHandle file = Gdx.files.internal(roomFile);
        JSONParser jsonParser = new JSONParser();
        Reader reader = file.reader("UTF-8");
        try {
            return (JSONObject) jsonParser.parse(reader);
        } catch (IOException exp) {
            throw new RuntimeException("Unable to load room", exp);
        } catch (ParseException exp) {
            throw new RuntimeException("Unable to load room", exp);
        } finally {
            IOUtils.closeQuietly(reader);
        }
    }

    private void createPickup(String type, String image, float x, float y) {
        EntityRef pickupEntity = spawnManager.spawnEntityAt("pickup", x, y);

        PickupComponent pickup = pickupEntity.getComponent(PickupComponent.class);
        pickup.setType(type);

        BobbingSpriteComponent sprite = pickupEntity.getComponent(BobbingSpriteComponent.class);
        sprite.setFileName(image);

        pickupEntity.saveChanges();
    }

    private void createPlatform(String prefab, float x, float y, float width, float height) {
        EntityRef platformEntity = spawnManager.spawnEntityAt(prefab, x, y);

        PlatformComponent platform = platformEntity.getComponent(PlatformComponent.class);
        setBounds(platform, 0, width, -height, 0f);

        ObstacleComponent obstacle = platformEntity.getComponent(ObstacleComponent.class);
        setBounds(obstacle, 0, width, -height, 0f);

        SensorTriggerComponent sensorTrigger = platformEntity.getComponent(SensorTriggerComponent.class);
        setBounds(sensorTrigger, 0, width, -height, 0f);

        platformEntity.saveChanges();
    }

    private void setBounds(Bounds2DComponent bounds2D, float left, float right, float down, float up) {
        bounds2D.setLeft(left);
        bounds2D.setRight(right);
        bounds2D.setDown(down);
        bounds2D.setUp(up);
    }
}
