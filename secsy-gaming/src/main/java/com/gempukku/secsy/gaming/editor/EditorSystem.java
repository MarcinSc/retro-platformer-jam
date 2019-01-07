package com.gempukku.secsy.gaming.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.gempukku.secsy.context.SystemContext;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.entity.Component;
import com.gempukku.secsy.entity.EntityManager;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.PriorityResolver;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.entity.prefab.NamedEntityData;
import com.gempukku.secsy.entity.prefab.PrefabManager;
import com.gempukku.secsy.entity.serialization.NameComponentManager;
import com.gempukku.secsy.gaming.component.Position2DComponent;
import com.gempukku.secsy.gaming.component.Size2DComponent;
import com.gempukku.secsy.gaming.editor.component.CommonEditors;
import com.gempukku.secsy.gaming.input.InputProvider;
import com.gempukku.secsy.gaming.rendering.pipeline.CameraEntityProvider;
import com.gempukku.secsy.gaming.rendering.pipeline.GetCamera;
import com.gempukku.secsy.gaming.rendering.pipeline.RenderToPipeline;
import com.gempukku.secsy.gaming.scene.SceneManager;
import com.gempukku.secsy.gaming.spawn.EntityDespawning;
import com.gempukku.secsy.gaming.spawn.EntitySpawned;
import com.gempukku.secsy.gaming.spawn.PrefabComponent;
import com.gempukku.secsy.gaming.spawn.SpawnManager;
import com.gempukku.secsy.gaming.ui.StageProvider;
import com.google.common.base.Function;
import com.google.common.base.Predicate;

import javax.annotation.Nullable;
import javax.swing.*;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@RegisterSystem(profiles = "editor")
public class EditorSystem extends AbstractLifeCycleSystem {
    private static final double VIEWPORT_ADJUST_SCALE = 1.2;

    @Inject
    private StageProvider stageProvider;
    @Inject
    private PrefabManager prefabManager;
    @Inject
    private SpawnManager spawnManager;
    @Inject
    private InputProvider inputProvider;
    @Inject
    private PriorityResolver priorityResolver;
    @Inject
    private CameraEntityProvider cameraEntityProvider;
    @Inject
    private NameComponentManager nameComponentManager;
    @Inject
    private SystemContext systemContext;
    @Inject
    private EntityManager entityManager;
    @Inject
    private SceneManager sceneManager;

    private Skin skin;

    private Window entityList;
    private SelectBox<String> prefabDropDown;
    private Tree entityTree;

    private Window entityInspector;
    private Table inspectorTable;
    private LinkedList<EntityComponentEditor> activeEditors = new LinkedList<EntityComponentEditor>();
    private PositionUpdateCallbackImpl positionUpdateCallback = new PositionUpdateCallbackImpl();

    private EntityRef selectedEntity;

    private ShapeRenderer shapeRenderer;

    private int lastRenderWidth;
    private int lastRenderHeight;

    private JFileChooser jfc = new JFileChooser(System.getProperty("user.dir"));

    @Override
    public void initialize() {
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        shapeRenderer = new ShapeRenderer();

        float priority = getPriority("gaming.input.editor");
        inputProvider.registerInputProcessor(new EditorInputProcessor(), priority);
        inputProvider.registerInputProcessor(new DeleteProcessor(), priority);

        float cameraPriority = getPriority("gaming.input.editor.camera");
        inputProvider.registerInputProcessor(new CameraAdjust(), cameraPriority);

        Stage stage = stageProvider.getStage();
        entityList = createEntityListWindow();
        stage.addActor(entityList);

        entityInspector = createEntityInspectorWindow();
        stage.addActor(entityInspector);
    }

    private float getPriority(String priorityName) {
        Float priority = priorityResolver.getPriority(priorityName);
        if (priority == null)
            priority = 0f;
        return priority;
    }

    private Window createEntityListWindow() {
        final Window entityList = new Window("Entities", skin);
        entityList.setResizable(true);
        entityList.setResizeBorder(10);

        prefabDropDown = new SelectBox<String>(skin);
        Array<String> prefabs = new Array<String>();
        for (NamedEntityData prefab : prefabManager.findPrefabsWithComponents(EditorEditableComponent.class)) {
            if (entityManager.wrapEntityData(prefab).getComponent(EditorEditableComponent.class).isAddable())
                prefabs.add(prefab.getName());
        }
        prefabs.sort();
        prefabDropDown.setItems(prefabs);
        entityList.add(prefabDropDown).growX();

        TextButton createEntity = new TextButton("Create", skin);
        createEntity.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        String selected = prefabDropDown.getSelected();
                        if (selected != null) {
                            spawnManager.spawnEntity(selected);
                        }
                    }
                });

        entityList.add(createEntity);
        entityList.row();

        entityTree = new Tree(skin);
        entityTree.getSelection().setMultiple(false);
        entityTree.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        entityTreeSelectionChanged();
                    }
                });

        ScrollPane entityTreeScroll = new ScrollPane(entityTree, skin);
        entityTreeScroll.setFadeScrollBars(false);
        entityList.add(entityTreeScroll).colspan(2).minHeight(200).minWidth(300).grow();
        entityList.row();

        Table fileTable = new Table(skin);
        Label sceneLabel = new Label("Scene", skin);
        fileTable.add(sceneLabel);
        final TextField sceneFile = new TextField("", skin);
        sceneFile.setDisabled(true);
        fileTable.add(sceneFile).growX();

        TextButton load = new TextButton("Load", skin);
        load.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        try {
                            final AtomicInteger returnValue = new AtomicInteger();
                            SwingUtilities.invokeAndWait(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            returnValue.set(jfc.showOpenDialog(null));
                                        }
                                    });
                            if (returnValue.get() == JFileChooser.APPROVE_OPTION) {
                                File selectedFile = jfc.getSelectedFile();
                                String absolutePath = selectedFile.getAbsolutePath();
                                sceneManager.unloadScene();
                                sceneManager.loadScene(Gdx.files.absolute(absolutePath));
                                sceneFile.setText(absolutePath);
                                sceneFile.setCursorPosition(absolutePath.length());
                            }
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        } catch (InvocationTargetException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
        fileTable.add(load);
        TextButton save = new TextButton("Save", skin);
        save.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        boolean saveScene = true;
                        if (sceneFile.getText().equals("") || Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT)) {
                            saveScene = false;
                            try {
                                final AtomicInteger returnValue = new AtomicInteger();
                                SwingUtilities.invokeAndWait(
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                returnValue.set(jfc.showSaveDialog(null));
                                            }
                                        });
                                if (returnValue.get() == JFileChooser.APPROVE_OPTION) {
                                    File selectedFile = jfc.getSelectedFile();
                                    String absolutePath = selectedFile.getAbsolutePath();
                                    sceneFile.setText(absolutePath);
                                    sceneFile.setCursorPosition(absolutePath.length());
                                    saveScene = true;
                                }
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            } catch (InvocationTargetException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        if (saveScene)
                            sceneManager.saveScene(Gdx.files.absolute(sceneFile.getText()),
                                    new Predicate<EntityRef>() {
                                        @Override
                                        public boolean apply(@Nullable EntityRef entityRef) {
                                            return entityRef.hasComponent(EditorEditableComponent.class);
                                        }
                                    },
                                    new Function<EntityRef, Map<String, Map<String, Object>>>() {
                                        @Nullable
                                        @Override
                                        public Map<String, Map<String, Object>> apply(@Nullable EntityRef entityRef) {
                                            return serializeEntity(entityRef);
                                        }
                                    });
                    }
                });
        fileTable.add(save);

        entityList.add(fileTable).colspan(2).growX();
        entityList.row();

        entityList.pack();
        entityList.setPosition(0, Gdx.graphics.getHeight() - entityList.getHeight());

        return entityList;
    }

    private Map<String, Map<String, Object>> serializeEntity(EntityRef entityRef) {
        Map<String, Map<String, Object>> result = new HashMap<String, Map<String, Object>>();

        EditorEditableComponent editorEditable = entityRef.getComponent(EditorEditableComponent.class);
        String nameInEditor = editorEditable.getNameInEditor();

        result.put("EditorEditable", Collections.<String, Object>singletonMap("nameInEditor", nameInEditor));

        for (String editableComponent : editorEditable.getEditableComponents()) {
            Class<? extends Component> componentClass = nameComponentManager.getComponentByName(editableComponent);
            EditableWith annotation = componentClass.getAnnotation(EditableWith.class);
            Class<? extends EntityComponentEditor> editorClass = annotation.value();

            try {
                EntityComponentEditor editor = editorClass.newInstance();
                systemContext.initializeObject(editor);
                Map<String, Object> changes = new LinkedHashMap<String, Object>();
                editor.serializeChanges(entityRef, changes, result);
                if (!changes.isEmpty())
                    result.put(editableComponent, changes);
            } catch (InstantiationException e) {
                throw new RuntimeException("Unable to create editor", e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Unable to create editor", e);
            }
        }

        return result;
    }

    private Window createEntityInspectorWindow() {
        Window entityInspector = new Window("Inspector", skin);
        entityInspector.setResizable(true);
        entityInspector.setResizeBorder(10);

        inspectorTable = new Table();
        ScrollPane inspectorScroll = new ScrollPane(inspectorTable, skin);
        inspectorScroll.setFadeScrollBars(false);

        entityInspector.add(inspectorScroll).grow().minWidth(300).minHeight(200);
        entityInspector.pack();
        entityInspector.setHeight(Gdx.graphics.getHeight() - entityList.getHeight());
        entityInspector.setPosition(0, 0);

        return entityInspector;
    }

    @ReceiveEvent
    public void entitySpawned(EntitySpawned entitySpawned, EntityRef entity, EditorEditableComponent editorEditable) {
        String prefab = entitySpawned.getPrefab();
        Tree.Node prefabNode = getPrefabRootNode(prefab);

        Label entityNodeLabel = new Label(editorEditable.getNameInEditor(), skin);
        Tree.Node entityNode = new Tree.Node(entityNodeLabel);
        entityNode.setObject(entity);
        prefabNode.add(entityNode);
    }

    @ReceiveEvent
    public void entityDespawning(EntityDespawning entityDespawning, EntityRef entity, EditorEditableComponent editorEditable) {
        String prefab = entityDespawning.getPrefab();
        Tree.Node prefabNode = getPrefabRootNode(prefab);
        Tree.Node despawningNode = getEntityNode(prefabNode, entity);
        prefabNode.remove(despawningNode);
        if (prefabNode.getChildren().size == 0)
            entityTree.remove(prefabNode);
    }

    private Tree.Node getEntityNode(Tree.Node rootNode, EntityRef entity) {
        for (Tree.Node child : rootNode.getChildren()) {
            if (entityManager.isSameEntity(entity, (EntityRef) child.getObject()))
                return child;
        }
        return null;
    }

    private Tree.Node getPrefabRootNode(String prefab) {
        Tree.Node prefabNode = findNodeByName(prefab);
        if (prefabNode == null) {
            Label prefabNodeLabel = new Label(prefab, skin);
            prefabNode = new Tree.Node(prefabNodeLabel);
            prefabNode.setSelectable(false);
            prefabNode.setExpanded(true);

            entityTree.add(prefabNode);
        }
        return prefabNode;
    }

    @ReceiveEvent(priorityName = "gaming.renderer.editor.selection")
    public void renderSelection(RenderToPipeline renderToPipeline, EntityRef entityRef, EditorComponent editor) {
        lastRenderWidth = renderToPipeline.getWidth();
        lastRenderHeight = renderToPipeline.getHeight();
        if (selectedEntity != null && selectedEntity.exists()) {
            Position2DComponent position = selectedEntity.getComponent(Position2DComponent.class);
            Size2DComponent size = selectedEntity.getComponent(Size2DComponent.class);

            if (position != null && size != null) {
                renderToPipeline.getRenderPipeline().getCurrentBuffer().begin();
                Camera camera = renderToPipeline.getCamera();

                float selectionBorderX = size.getWidth() * 0.05f;
                float selectionBorderY = size.getHeight() * 0.05f;
                float border = Math.max(editor.getMinimumSelectionBorder(), Math.min(selectionBorderX, selectionBorderY));

                float x = position.getX() - size.getAnchorX() * size.getWidth() - border;
                float y = position.getY() - size.getAnchorY() * size.getHeight() - border;

                shapeRenderer.setProjectionMatrix(camera.combined);
                shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
                shapeRenderer.setColor(Color.WHITE);
                shapeRenderer.rect(x, y, size.getWidth() + border * 2, size.getHeight() + border * 2);
                shapeRenderer.end();

                renderToPipeline.getRenderPipeline().getCurrentBuffer().end();
            }
        }
    }

    @Override
    public void destroy() {
        shapeRenderer.dispose();
        skin.dispose();
    }

    private void entityTreeSelectionChanged() {
        Tree.Node selectedNode = entityTree.getSelection().first();
        if (selectedNode != null) {
            selectedEntity = (EntityRef) selectedNode.getObject();
            inspectorTable.clearChildren();
            activeEditors.clear();

            fillInspector();
        } else {
            selectedEntity = null;
            inspectorTable.clearChildren();
            activeEditors.clear();
        }
    }

    private void fillInspector() {
        String prefab = selectedEntity.getComponent(PrefabComponent.class).getPrefab();
        inspectorTable.add(new Label("Prefab: " + prefab, skin)).growX();
        inspectorTable.row();

        Table groupTable = new Table(skin);
        CommonEditors.initializeGroupTable(groupTable, skin);

        CommonEditors.appendLabel(groupTable, skin, "Name");
        CommonEditors.appendStringField(groupTable, skin, selectedEntity, null,
                new Function<EntityRef, String>() {
                    @Nullable
                    @Override
                    public String apply(@Nullable EntityRef entityRef) {
                        return entityRef.getComponent(EditorEditableComponent.class).getNameInEditor();
                    }
                },
                new Function<TextField, Void>() {
                    @Nullable
                    @Override
                    public Void apply(@Nullable TextField value) {
                        String name = value.getText();

                        EditorEditableComponent editorEditable = selectedEntity.getComponent(EditorEditableComponent.class);
                        editorEditable.setNameInEditor(name);
                        selectedEntity.saveChanges();
                        Label label = (Label) entityTree.getSelection().first().getActor();
                        label.setText(name);
                        return null;
                    }
                });
        groupTable.row();

        inspectorTable.add(groupTable).growX();
        inspectorTable.row();

        final EditorEditableComponent editorEditable = selectedEntity.getComponent(EditorEditableComponent.class);

        for (String editableComponent : editorEditable.getEditableComponents()) {
            Class<? extends Component> componentClass = nameComponentManager.getComponentByName(editableComponent);
            EditableWith annotation = componentClass.getAnnotation(EditableWith.class);
            Class<? extends EntityComponentEditor> editorClass = annotation.value();

            try {
                EntityComponentEditor editor = editorClass.newInstance();
                systemContext.initializeObject(editor);
                editor.appendEditor(inspectorTable, skin, selectedEntity, positionUpdateCallback);
                activeEditors.add(editor);
            } catch (InstantiationException e) {
                throw new RuntimeException("Unable to create editor", e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Unable to create editor", e);
            }
        }
    }

    private Tree.Node findNodeByName(String prefab) {
        Array<Tree.Node> rootNodes = entityTree.getRootNodes();
        for (Tree.Node rootNode : rootNodes) {
            Label label = (Label) rootNode.getActor();
            String nodeLabel = label.getText().toString();
            if (nodeLabel.equals(prefab)) {
                return rootNode;
            }
        }
        return null;
    }

    private void notifyPositionUpdated(EntityRef entityRef, float x, float y) {
        for (EntityComponentEditor activeEditor : activeEditors) {
            activeEditor.entityMoved(entityRef, x, y);
        }
    }

    private class PositionUpdateCallbackImpl implements EntityComponentEditor.PositionUpdateCallback {
        @Override
        public void positionUpdated(EntityRef entityRef) {
            Position2DComponent position = entityRef.getComponent(Position2DComponent.class);
            notifyPositionUpdated(entityRef, position.getX(), position.getY());
        }
    }

    private class CameraAdjust extends InputAdapter {
        private boolean cameraDragged;
        private int dragStartX;
        private int dragStartY;

        @Override
        public boolean scrolled(int amount) {
            EntityRef cameraEntity = cameraEntityProvider.getCameraEntity();
            MouseCameraComponent mouseCamera = cameraEntity.getComponent(MouseCameraComponent.class);

            float viewportWidth = mouseCamera.getViewportWidth();
            float viewportHeight = mouseCamera.getViewportHeight();

            if (amount == -1) {
                viewportWidth *= VIEWPORT_ADJUST_SCALE;
                viewportHeight *= VIEWPORT_ADJUST_SCALE;
            } else {
                viewportWidth /= VIEWPORT_ADJUST_SCALE;
                viewportHeight /= VIEWPORT_ADJUST_SCALE;
            }

            mouseCamera.setViewportWidth(viewportWidth);
            mouseCamera.setViewportHeight(viewportHeight);
            cameraEntity.saveChanges();

            return true;
        }

        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            dragStartX = screenX;
            dragStartY = screenY;

            cameraDragged = true;
            return true;
        }

        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {
            if (cameraDragged) {
                EntityRef cameraEntity = cameraEntityProvider.getCameraEntity();

                GetCamera getCamera = new GetCamera(0, lastRenderWidth, lastRenderHeight);
                cameraEntity.send(getCamera);
                Camera camera = getCamera.getCamera();
                Vector3 clickCoords1 = camera.unproject(new Vector3(screenX, screenY, 0));
                Vector3 clickCoords2 = camera.unproject(new Vector3(dragStartX, dragStartY, 0));

                float diffX = clickCoords2.x - clickCoords1.x;
                float diffY = clickCoords2.y - clickCoords1.y;

                dragStartX = screenX;
                dragStartY = screenY;

                MouseCameraComponent mouseCamera = cameraEntity.getComponent(MouseCameraComponent.class);
                mouseCamera.setX(mouseCamera.getX() + diffX);
                mouseCamera.setY(mouseCamera.getY() + diffY);

                cameraEntity.saveChanges();
                return true;
            }
            return false;
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            if (cameraDragged) {
                cameraDragged = false;
                return true;
            }
            return false;
        }
    }

    private class DeleteProcessor extends InputAdapter {
        @Override
        public boolean keyDown(int keycode) {
            if (keycode == Input.Keys.DEL && selectedEntity != null) {
                spawnManager.despawnEntity(selectedEntity);
                entityTree.getSelection().clear();
                return true;
            }
            return false;
        }
    }

    private class EditorInputProcessor extends InputAdapter {
        private static final float DRAG_START_DISTANCE_SQUARED = 25f;

        private EntityRef dragged;
        private boolean dragStarted;
        private int dragScreenX;
        private int dragScreenY;
        private float dragStartX;
        private float dragStartY;
        private float draggedPosX;
        private float draggedPosY;

        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            GetCamera getCamera = new GetCamera(0, lastRenderWidth, lastRenderHeight);
            cameraEntityProvider.getCameraEntity().send(getCamera);
            Camera camera = getCamera.getCamera();
            Vector3 clickCoords = camera.unproject(new Vector3(screenX, screenY, 0));

            for (Tree.Node rootNode : entityTree.getRootNodes()) {
                for (Tree.Node node : rootNode.getChildren()) {
                    Object obj = node.getObject();
                    if (obj != null) {
                        EntityRef nodeEntity = (EntityRef) obj;
                        EditorEditableComponent editable = nodeEntity.getComponent(EditorEditableComponent.class);
                        if (editable.isSelectableInScene()) {
                            Position2DComponent position = nodeEntity.getComponent(Position2DComponent.class);
                            Size2DComponent size = nodeEntity.getComponent(Size2DComponent.class);

                            if (position != null && size != null) {
                                float posX = position.getX();
                                float x = posX - size.getAnchorX() * size.getWidth();
                                float posY = position.getY();
                                float y = posY - size.getAnchorY() * size.getHeight();

                                if (clickCoords.x >= x && clickCoords.x < x + size.getWidth()
                                        && clickCoords.y >= y && clickCoords.y < y + size.getHeight()) {
                                    dragged = nodeEntity;
                                    dragStartX = clickCoords.x;
                                    dragStartY = clickCoords.y;
                                    draggedPosX = posX;
                                    draggedPosY = posY;
                                    dragScreenX = screenX;
                                    dragScreenY = screenY;

                                    entityTree.getSelection().set(node);
                                    return true;
                                }
                            }
                        }
                    }
                }
            }

            entityTree.getSelection().clear();
            return false;
        }

        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {
            if (dragged != null && dragged.exists()) {
                if (!dragStarted) {
                    int dstSquared = (screenX - dragScreenX) * (screenX - dragScreenX)
                            + (screenY - dragScreenY) * (screenY - dragScreenY);
                    if (dstSquared >= DRAG_START_DISTANCE_SQUARED) {
                        dragStarted = true;
                    }
                }
                if (dragStarted) {
                    GetCamera getCamera = new GetCamera(0, lastRenderWidth, lastRenderHeight);
                    cameraEntityProvider.getCameraEntity().send(getCamera);
                    Camera camera = getCamera.getCamera();
                    Vector3 dragCoords = camera.unproject(new Vector3(screenX, screenY, 0));

                    float newX = draggedPosX + dragCoords.x - dragStartX;
                    float newY = draggedPosY + dragCoords.y - dragStartY;

                    Position2DComponent position = dragged.getComponent(Position2DComponent.class);
                    position.setX(newX);
                    position.setY(newY);
                    dragged.saveChanges();

                    notifyPositionUpdated(dragged, newX, newY);

                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            if (dragged != null) {
                dragged = null;
                dragStarted = false;
                return true;
            }
            return false;
        }
    }
}
