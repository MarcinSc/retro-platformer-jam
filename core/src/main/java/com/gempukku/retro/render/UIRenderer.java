package com.gempukku.retro.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.gempukku.retro.logic.equipment.ItemProvider;
import com.gempukku.retro.logic.player.PlayerProvider;
import com.gempukku.retro.model.DisplayNameComponent;
import com.gempukku.retro.model.EquippedSpriteComponent;
import com.gempukku.retro.model.InventoryComponent;
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
    @Inject
    private PlayerProvider playerProvider;
    @Inject
    private FontProvider fontProvider;

    private SpriteBatch spriteBatch;

    private Texture uiTexture;
    private GlyphLayout glyphLayout = new GlyphLayout();

    @Override
    public void initialize() {
        spriteBatch = new SpriteBatch();
        uiTexture = new Texture(Gdx.files.internal("images/ui.png"));
    }

    @ReceiveEvent(priority = -1)
    public void renderUI(RenderToPipeline renderToPipeline) {
        EntityRef player = playerProvider.getPlayer();
        if (player != null) {
            renderToPipeline.getRenderPipeline().getCurrentBuffer().begin();

            spriteBatch.getProjectionMatrix().setToOrtho2D(0, 0, renderToPipeline.getWidth(), renderToPipeline.getHeight());
            spriteBatch.begin();
            spriteBatch.draw(uiTexture, 0, 0);

            String equippedItemName = player.getComponent(InventoryComponent.class).getEquippedItem();

            EntityRef equippedItem = itemProvider.getItemByName(equippedItemName);
            String filePath = equippedItem.getComponent(EquippedSpriteComponent.class).getFilePath();
            TextureRegion background = textureAtlasProvider.getTexture("ui", "images/equipment-background.png");
            spriteBatch.draw(background, renderToPipeline.getWidth() - 34, renderToPipeline.getHeight() - 34, 32, 32);
            TextureRegion itemTexture = textureAtlasProvider.getTexture("ui", filePath);
            spriteBatch.draw(itemTexture, renderToPipeline.getWidth() - 34, renderToPipeline.getHeight() - 34, 32, 32);

            BitmapFont font = fontProvider.getFont();
            String displayName = equippedItem.getComponent(DisplayNameComponent.class).getDisplayName();
            glyphLayout.setText(font, displayName);
            font.draw(spriteBatch, displayName, renderToPipeline.getWidth() - 16 - 2 - glyphLayout.width / 2, renderToPipeline.getHeight() - 34 - 2);

            spriteBatch.end();

            renderToPipeline.getRenderPipeline().getCurrentBuffer().end();
        }
    }

    @Override
    public void destroy() {
        uiTexture.dispose();
        spriteBatch.dispose();
    }
}
