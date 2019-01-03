package com.gempukku.secsy.gaming.editor;

import com.badlogic.gdx.Gdx;
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
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.gempukku.secsy.context.SystemContext;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.entity.Component;
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
import com.gempukku.secsy.gaming.spawn.EntitySpawned;
import com.gempukku.secsy.gaming.spawn.PrefabComponent;
import com.gempukku.secsy.gaming.spawn.SpawnManager;
import com.gempukku.secsy.gaming.ui.StageProvider;
import com.google.common.base.Function;

import javax.annotation.Nullable;
import java.util.LinkedList;

@RegisterSystem(profiles = "editor")
public class EditorSystem extends AbstractLifeCycleSystem {
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

    @Override
    public void initialize() {
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        shapeRenderer = new ShapeRenderer();

        Float priority = priorityResolver.getPriority("gaming.input.editor");
        if (priority == null)
            priority = 0f;
        inputProvider.registerInputProcessor(new EditorInputProcessor(), priority);

        Stage stage = stageProvider.getStage();
        entityList = createEntityListWindow();
        stage.addActor(entityList);

        entityInspector = createEntityInspectorWindow();
        stage.addActor(entityInspector);
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
        entityInspector.setPosition(0, Gdx.graphics.getHeight() - entityList.getHeight() - entityInspector.getHeight());

        return entityInspector;
    }

    private Window createEntityListWindow() {
        Window entityList = new Window("Entities", skin);
        entityList.setResizable(true);
        entityList.setResizeBorder(10);

        prefabDropDown = new SelectBox<String>(skin);
        Array<String> prefabs = new Array<String>();
        for (NamedEntityData prefab : prefabManager.findPrefabsWithComponents(EditorEditableComponent.class)) {
            prefabs.add(prefab.getName());
        }
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
        entityList.add(entityTreeScroll).colspan(2).minHeight(100).minWidth(300).grow();
        entityList.row();

        entityList.pack();
        entityList.setPosition(0, Gdx.graphics.getHeight() - entityList.getHeight());

        return entityList;
    }

    @ReceiveEvent
    public void entitySpawned(EntitySpawned entitySpawned, EntityRef entity, EditorEditableComponent editorEditable) {
        String prefab = entitySpawned.getPrefab();
        Tree.Node prefabNode = findNodeByName(prefab);
        if (prefabNode == null) {
            Label prefabNodeLabel = new Label(prefab, skin);
            prefabNode = new Tree.Node(prefabNodeLabel);
            prefabNode.setSelectable(false);
            prefabNode.setExpanded(true);

            entityTree.add(prefabNode);
        }

        Label entityNodeLabel = new Label(editorEditable.getNameInEditor(), skin);
        Tree.Node entityNode = new Tree.Node(entityNodeLabel);
        entityNode.setObject(entity);
        prefabNode.add(entityNode);
    }

    @ReceiveEvent(priorityName = "gaming.renderer.editor.selection")
    public void renderSelection(RenderToPipeline renderToPipeline, EntityRef entityRef, EditorComponent editor) {
        lastRenderWidth = renderToPipeline.getWidth();
        lastRenderHeight = renderToPipeline.getHeight();
        if (selectedEntity != null && selectedEntity.exists()) {
            renderToPipeline.getRenderPipeline().getCurrentBuffer().begin();
            Camera camera = renderToPipeline.getCamera();
            Position2DComponent position = selectedEntity.getComponent(Position2DComponent.class);
            Size2DComponent size = selectedEntity.getComponent(Size2DComponent.class);

            float selectionBorderX = size.getWidth() * 0.05f;
            float selectionBorderY = size.getHeight() * 0.05f;

            float x = position.getX() - size.getAnchorX() * size.getWidth() - selectionBorderX;
            float y = position.getY() - size.getAnchorY() * size.getHeight() - selectionBorderY;

            shapeRenderer.setProjectionMatrix(camera.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(Color.WHITE);
            shapeRenderer.rect(x, y, size.getWidth() + selectionBorderX * 2, size.getHeight() + selectionBorderY * 2);
            shapeRenderer.end();

            renderToPipeline.getRenderPipeline().getCurrentBuffer().end();
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
        Drawable background = skin.get("default-round", Drawable.class);
        groupTable.setBackground(background);
        groupTable.pad(background.getTopHeight(), background.getLeftWidth(), background.getBottomHeight(), background.getRightWidth());

        CommonEditors.appendStringField(groupTable, skin, selectedEntity, "Name", null,
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

    private class PositionUpdateCallbackImpl implements EntityComponentEditor.PositionUpdateCallback {
        @Override
        public void positionUpdated(EntityRef entityRef) {
            Position2DComponent position = entityRef.getComponent(Position2DComponent.class);
            notifyPositionUpdated(entityRef, position.getX(), position.getY());
        }
    }

    private void notifyPositionUpdated(EntityRef entityRef, float x, float y) {
        for (EntityComponentEditor activeEditor : activeEditors) {
            activeEditor.entityMoved(entityRef, x, y);
        }
    }

    private class EditorInputProcessor extends InputAdapter {
        private EntityRef dragged;
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
                        Position2DComponent position = nodeEntity.getComponent(Position2DComponent.class);
                        Size2DComponent size = nodeEntity.getComponent(Size2DComponent.class);

                        float x = position.getX() - size.getAnchorX() * size.getWidth();
                        float y = position.getY() - size.getAnchorY() * size.getHeight();

                        if (clickCoords.x >= x && clickCoords.x < x + size.getWidth()
                                && clickCoords.y >= y && clickCoords.y < y + size.getHeight()) {
                            dragged = nodeEntity;
                            dragStartX = clickCoords.x;
                            dragStartY = clickCoords.y;
                            draggedPosX = position.getX();
                            draggedPosY = position.getY();

                            entityTree.getSelection().set(node);
                            return true;
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
            return false;
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            if (dragged != null) {
                dragged = null;
                return true;
            }
            return false;
        }
    }
}
