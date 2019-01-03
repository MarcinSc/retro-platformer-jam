package com.gempukku.secsy.gaming.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.entity.EntityManager;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.game.GameEntityProvider;
import com.gempukku.secsy.gaming.spawn.SpawnManager;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.Reader;
import java.util.Map;

@RegisterSystem(profiles = "scene", shared = SceneManager.class)
public class SceneSystem extends AbstractLifeCycleSystem implements SceneManager {
    @Inject
    private EntityManager entityManager;
    @Inject
    private SpawnManager spawnManager;
    @Inject
    private GameEntityProvider gameEntityProvider;

    @Override
    public void unloadScene() {
        for (EntityRef entityRef : entityManager.getAllEntities()) {
            if (!entityRef.hasComponent(GlobalEntityComponent.class))
                spawnManager.despawnEntity(entityRef);
        }
    }

    @Override
    public void loadScene(String scene) {
        JSONArray sceneObj = loadJSON(scene);
        for (Object o : sceneObj) {
            JSONObject entityDef = (JSONObject) o;
            String prefab = (String) entityDef.get("prefab");
            Map<String, Map<String, Object>> overrides = (Map<String, Map<String, Object>>) entityDef.get("override");

            if (overrides != null)
                spawnManager.spawnEntity(prefab, overrides);
            else
                spawnManager.spawnEntity(prefab);
        }
        
        gameEntityProvider.getGameEntity().send(new SceneLoaded());
    }

    private JSONArray loadJSON(String roomFile) {
        FileHandle file = Gdx.files.internal(roomFile);
        JSONParser jsonParser = new JSONParser();
        Reader reader = file.reader("UTF-8");
        try {
            return (JSONArray) jsonParser.parse(reader);
        } catch (IOException exp) {
            throw new RuntimeException("Unable to load room", exp);
        } catch (ParseException exp) {
            throw new RuntimeException("Unable to load room", exp);
        } finally {
            IOUtils.closeQuietly(reader);
        }
    }

}
