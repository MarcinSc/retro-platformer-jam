package com.gempukku.retro.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.gempukku.retro.logic.equipment.ItemProvider;
import com.gempukku.retro.model.EquipmentComponent;
import com.gempukku.retro.model.EquippedSpriteComponent;
import com.gempukku.retro.model.PlayerComponent;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.entity.EntityManager;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.gaming.asset.texture.TextureAtlasProvider;
import com.gempukku.secsy.gaming.rendering.pipeline.RenderToPipeline;

@RegisterSystem
public class UIRenderer extends AbstractLifeCycleSystem {
    @Inject
    private EntityManager entityManager;
    @Inject
    private ItemProvider itemProvider;
    @Inject
    private TextureAtlasProvider textureAtlasProvider;

    private SpriteBatch spriteBatch;

    private Texture uiTexture;

    @Override
    public void initialize() {
        spriteBatch = new SpriteBatch();
        uiTexture = new Texture(Gdx.files.internal("images/ui.png"));
    }

    @ReceiveEvent(priority = -1)
    public void renderUI(RenderToPipeline renderToPipeline) {
        renderToPipeline.getRenderPipeline().getCurrentBuffer().begin();

        spriteBatch.getProjectionMatrix().setToOrtho2D(0, 0, renderToPipeline.getWidth(), renderToPipeline.getHeight());
        spriteBatch.begin();
        spriteBatch.draw(uiTexture, 0, 0);

        for (EntityRef player : entityManager.getEntitiesWithComponents(PlayerComponent.class)) {
            String equippedItemName = player.getComponent(EquipmentComponent.class).getEquippedItem();
            EntityRef equippedItem = itemProvider.getItemByName(equippedItemName);
            String filePath = equippedItem.getComponent(EquippedSpriteComponent.class).getFilePath();
            TextureRegion itemTexture = textureAtlasProvider.getTexture("ui", filePath);
            spriteBatch.draw(itemTexture, renderToPipeline.getWidth() - 34, renderToPipeline.getHeight() - 34);
        }

        spriteBatch.end();

        renderToPipeline.getRenderPipeline().getCurrentBuffer().end();
    }

    @Override
    public void destroy() {
        uiTexture.dispose();
        spriteBatch.dispose();
    }
}
