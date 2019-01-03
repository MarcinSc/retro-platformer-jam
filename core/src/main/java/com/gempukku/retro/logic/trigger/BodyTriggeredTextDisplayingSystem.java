package com.gempukku.retro.logic.trigger;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.gempukku.retro.logic.player.PlayerComponent;
import com.gempukku.retro.logic.room.LoadRoom;
import com.gempukku.retro.render.FontProvider;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.gaming.component.Position2DComponent;
import com.gempukku.secsy.gaming.physics.basic2d.SensorContactBegin;
import com.gempukku.secsy.gaming.physics.basic2d.SensorContactEnd;
import com.gempukku.secsy.gaming.rendering.pipeline.RenderToPipeline;

@RegisterSystem
public class BodyTriggeredTextDisplayingSystem extends AbstractLifeCycleSystem {
    @Inject
    private FontProvider fontProvider;

    private SpriteBatch spriteBatch;
    private GlyphLayout glyphLayout = new GlyphLayout();

    private String text;
    private float x;
    private float y;

    @Override
    public void initialize() {
        spriteBatch = new SpriteBatch();
    }

    @ReceiveEvent
    public void loadRoom(LoadRoom loadRoom) {
        text = null;
    }

    @ReceiveEvent
    public void contactStart(SensorContactBegin contactBegin, EntityRef entity, PlayerComponent player) {
        if (contactBegin.getSensorType().equals("body")) {
            EntityRef sensorTrigger = contactBegin.getSensorTrigger();
            if (sensorTrigger.hasComponent(BodyTriggeredTextDisplayingComponent.class)) {
                BodyTriggeredTextDisplayingComponent component = sensorTrigger.getComponent(BodyTriggeredTextDisplayingComponent.class);
                Position2DComponent position = sensorTrigger.getComponent(Position2DComponent.class);
                text = component.getDisplayText();
                this.x = component.getX() + position.getX();
                this.y = component.getY() + position.getY();
            }
        }
    }

    @ReceiveEvent
    public void contactEnd(SensorContactEnd contactEnd, EntityRef entity, PlayerComponent player) {
        if (contactEnd.getSensorType().equals("body")) {
            EntityRef sensorTrigger = contactEnd.getSensorTrigger();
            if (sensorTrigger.hasComponent(BodyTriggeredTextDisplayingComponent.class)) {
                this.text = null;
            }
        }
    }

    private Vector3 tmp = new Vector3();

    @ReceiveEvent(priority = -1000)
    public void render(RenderToPipeline renderToPipeline) {
        if (text != null) {
            BitmapFont textFont = fontProvider.getFont();
            tmp.set(x, y, 0);
            Matrix4 cameraProjection = renderToPipeline.getCamera().combined;
            Vector3 textLocation = tmp.mul(cameraProjection);
            int screenWidth = renderToPipeline.getWidth();
            int screenHeight = renderToPipeline.getHeight();
            textLocation.add(1).scl(0.5f);
            textLocation.x *= screenWidth;
            textLocation.y *= screenHeight;
            glyphLayout.setText(textFont, text);
            textLocation.x -= glyphLayout.width / 2;

            renderToPipeline.getRenderPipeline().getCurrentBuffer().begin();
            textFont.setColor(Color.WHITE);
            spriteBatch.getProjectionMatrix().setToOrtho2D(0, 0, screenWidth, screenHeight);
            spriteBatch.begin();
            textFont.draw(spriteBatch, text, textLocation.x, textLocation.y);
            spriteBatch.end();
            renderToPipeline.getRenderPipeline().getCurrentBuffer().end();
        }
    }

    @Override
    public void destroy() {
        spriteBatch.dispose();
    }
}
