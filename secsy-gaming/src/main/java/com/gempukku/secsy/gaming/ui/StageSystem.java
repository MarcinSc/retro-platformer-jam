package com.gempukku.secsy.gaming.ui;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.entity.dispatch.PriorityResolver;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.gaming.input.InputProvider;
import com.gempukku.secsy.gaming.rendering.pipeline.RenderToPipeline;

@RegisterSystem(profiles = "ui", shared = StageProvider.class)
public class StageSystem extends AbstractLifeCycleSystem implements StageProvider {
    @Inject
    private InputProvider inputProvider;
    @Inject
    private PriorityResolver priorityResolver;

    private Stage stage;

    @Override
    public void initialize() {
        stage = new Stage();
        Float priority = priorityResolver.getPriority("gaming.input.ui");
        if (priority == null)
            priority = 0f;
        inputProvider.registerInputProcessor(stage, priority);
    }

    @Override
    public Stage getStage() {
        return stage;
    }

    @ReceiveEvent(priorityName = "gaming.renderer.ui")
    public void drawUI(RenderToPipeline renderToPipeline) {
        renderToPipeline.getRenderPipeline().getCurrentBuffer().begin();
        stage.draw();
        renderToPipeline.getRenderPipeline().getCurrentBuffer().end();
    }

    @Override
    public void destroy() {
        stage.dispose();
    }
}
