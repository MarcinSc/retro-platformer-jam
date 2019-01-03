package com.gempukku.secsy.gaming.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.entity.EntityManager;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.game.GameEntityProvider;
import com.gempukku.secsy.entity.index.EntityIndex;
import com.gempukku.secsy.entity.index.EntityIndexManager;
import com.gempukku.secsy.gaming.spawn.PrefabComponent;
import com.gempukku.secsy.gaming.spawn.SpawnManager;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Map;

@RegisterSystem(profiles = "scene", shared = SceneManager.class)
public class SceneSystem extends AbstractLifeCycleSystem implements SceneManager {
    @Inject
    private EntityManager entityManager;
    @Inject
    private SpawnManager spawnManager;
    @Inject
    private GameEntityProvider gameEntityProvider;
    @Inject
    private EntityIndexManager entityIndexManager;

    private EntityIndex prefabEntities;

    @Override
    public void initialize() {
        prefabEntities = entityIndexManager.addIndexOnComponents(PrefabComponent.class);
    }

    @Override
    public void unloadScene() {
        for (EntityRef entityRef : entityManager.getAllEntities()) {
            if (!entityRef.hasComponent(GlobalEntityComponent.class))
                spawnManager.despawnEntity(entityRef);
        }
    }

    @Override
    public void loadScene(String scene) {
        createScene(loadJSON(Gdx.files.internal(scene)));
    }

    @Override
    public void loadScene(FileHandle fileHandle) {
        createScene(loadJSON(fileHandle));
    }

    @Override
    public void saveScene(FileHandle fileHandle, Predicate<EntityRef> filter, Function<EntityRef, Map<String, Map<String, Object>>> serializeOverrides) {
        JSONArray sceneEntities = new JSONArray();

        for (EntityRef prefabEntity : prefabEntities) {
            if (filter.apply(prefabEntity)) {
                JSONObject entityObj = new JSONObject();
                String prefab = prefabEntity.getComponent(PrefabComponent.class).getPrefab();
                entityObj.put("prefab", prefab);

                Map<String, Map<String, Object>> overrides = serializeOverrides.apply(prefabEntity);
                if (overrides != null && !overrides.isEmpty())
                    entityObj.put("override", overrides);

                sceneEntities.add(entityObj);
            }
        }

        saveJSON(fileHandle, sceneEntities);
    }

    private void createScene(JSONArray sceneObj) {
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

    private JSONArray loadJSON(FileHandle fileHandle) {
        JSONParser jsonParser = new JSONParser();
        Reader reader = fileHandle.reader("UTF-8");
        try {
            return (JSONArray) jsonParser.parse(reader);
        } catch (IOException exp) {
            throw new RuntimeException("Unable to load scene", exp);
        } catch (ParseException exp) {
            throw new RuntimeException("Unable to load scene", exp);
        } finally {
            IOUtils.closeQuietly(reader);
        }
    }

    private void saveJSON(FileHandle fileHandle, JSONArray jsonArray) {
        Writer writer = fileHandle.writer(false, "UTF-8");
        try {
            jsonArray.writeJSONString(writer);
        } catch (IOException exp) {
            throw new RuntimeException("Unable to save scene", exp);
        } finally {
            IOUtils.closeQuietly(writer);
        }
    }
}
