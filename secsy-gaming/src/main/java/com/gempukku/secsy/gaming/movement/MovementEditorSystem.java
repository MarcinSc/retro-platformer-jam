package com.gempukku.secsy.gaming.movement;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.entity.index.EntityIndex;
import com.gempukku.secsy.entity.index.EntityIndexManager;
import com.gempukku.secsy.gaming.rendering.pipeline.RenderToPipeline;

@RegisterSystem(profiles = {"movement", "editor"})
public class MovementEditorSystem extends AbstractLifeCycleSystem {
    @Inject
    private EntityIndexManager entityIndexManager;

    private EntityIndex oscillatingEntities;
    private ShapeRenderer shapeRenderer;

    @Override
    public void initialize() {
        shapeRenderer = new ShapeRenderer();

        oscillatingEntities = entityIndexManager.addIndexOnComponents(OscillatingComponent.class);
    }

    @ReceiveEvent(priorityName = "gaming.renderer.editor.movement")
    public void renderMovement(RenderToPipeline renderToPipeline) {
        renderToPipeline.getRenderPipeline().getCurrentBuffer().begin();
        shapeRenderer.setProjectionMatrix(renderToPipeline.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        for (EntityRef oscillatingEntity : oscillatingEntities) {
            OscillatingComponent component = oscillatingEntity.getComponent(OscillatingComponent.class);
            Vector2 startingPosition = component.getStartingPosition();
            Vector2 distance = component.getDistance();
            shapeRenderer.line(startingPosition.x, startingPosition.y, startingPosition.x + distance.x, startingPosition.y + distance.y);
        }
        shapeRenderer.end();
        renderToPipeline.getRenderPipeline().getCurrentBuffer().end();
    }

    @Override
    public void destroy() {
        shapeRenderer.dispose();
    }
}
