package com.gempukku.retro.render;

import com.badlogic.gdx.graphics.Color;
import com.gempukku.retro.model.PlatformComponent;
import com.gempukku.retro.model.SpritePriorities;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.entity.index.EntityIndex;
import com.gempukku.secsy.entity.index.EntityIndexManager;
import com.gempukku.secsy.gaming.component.Position2DComponent;
import com.gempukku.secsy.gaming.component.PositionResolver;
import com.gempukku.secsy.gaming.component.Size2DComponent;
import com.gempukku.secsy.gaming.rendering.sprite.GatherSprites;
import com.gempukku.secsy.gaming.rendering.sprite.SpriteRenderer;

@RegisterSystem
public class PlatformRenderer extends AbstractLifeCycleSystem {
    @Inject
    private EntityIndexManager entityIndexManager;
    private EntityIndex platforms;

    private static float platformBasicSize = 0.1f;

    @Override
    public void initialize() {
        platforms = entityIndexManager.addIndexOnComponents(PlatformComponent.class);
    }

    @ReceiveEvent
    public void platformSprites(GatherSprites sprites) {
        SpriteRenderer.SpriteSink spriteSink = sprites.getSpriteSink();
        for (EntityRef platformEntity : platforms) {
            PlatformComponent platform = platformEntity.getComponent(PlatformComponent.class);
            Size2DComponent size = platformEntity.getComponent(Size2DComponent.class);
            Position2DComponent position = platformEntity.getComponent(Position2DComponent.class);

            float x = position.getX() + PositionResolver.getLeft(size, platform);
            float y = position.getY() + PositionResolver.getDown(size, platform);

            float width = PositionResolver.getWidth(size, platform);
            float height = PositionResolver.getHeight(size, platform);

            String textureAtlasId = platform.getTextureAtlasId();
            String beginningImage = platform.getBeginningImage();
            String endingImage = platform.getEndingImage();

            if (platform.isHorizontal()) {
                float middleX = x;
                if (isNotEmpty(beginningImage)) {
                    spriteSink.addSprite(SpritePriorities.PLATFORM, textureAtlasId, beginningImage, x, y, platformBasicSize, height, Color.WHITE);
                    width -= platformBasicSize;
                    middleX += platformBasicSize;
                }
                if (isNotEmpty(endingImage)) {
                    spriteSink.addSprite(SpritePriorities.PLATFORM, textureAtlasId, endingImage, middleX + width - platformBasicSize, y, platformBasicSize, height, Color.WHITE);
                    width -= platformBasicSize;
                }
                spriteSink.addTiledSprite(SpritePriorities.PLATFORM, platform.getCenterImage(),
                        middleX, y, width, height, width / platformBasicSize, -1f, Color.WHITE);
            } else {
                float middleY = y;
                if (isNotEmpty(beginningImage)) {
                    spriteSink.addSprite(SpritePriorities.PLATFORM, textureAtlasId, beginningImage, x, y, width, platformBasicSize, Color.WHITE);
                    height -= platformBasicSize;
                    middleY += platformBasicSize;
                }
                if (isNotEmpty(endingImage)) {
                    spriteSink.addSprite(SpritePriorities.PLATFORM, textureAtlasId, endingImage, x, middleY + height - platformBasicSize, width, platformBasicSize, Color.WHITE);
                    width -= platformBasicSize;
                }
                spriteSink.addTiledSprite(SpritePriorities.PLATFORM, platform.getCenterImage(),
                        x, middleY, width, height, 1f, height / platformBasicSize, Color.WHITE);
            }
        }
    }

    private boolean isNotEmpty(String beginningImage) {
        return beginningImage != null && !beginningImage.equals("");
    }
}
