package com.gempukku.retro.render;

import com.badlogic.gdx.graphics.Color;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.entity.index.EntityIndex;
import com.gempukku.secsy.entity.index.EntityIndexManager;
import com.gempukku.secsy.gaming.component.Position2DComponent;
import com.gempukku.secsy.gaming.rendering.sprite.GatherSprites;
import com.gempukku.secsy.gaming.rendering.sprite.SpriteRenderer;
import com.gempukku.secsy.gaming.time.TimeManager;

@RegisterSystem
public class FadingSpriteRenderer extends AbstractLifeCycleSystem {
    @Inject
    private EntityIndexManager entityIndexManager;
    @Inject
    private TimeManager timeManager;

    private EntityIndex fadingSprites;

    @Override
    public void initialize() {
        fadingSprites = entityIndexManager.addIndexOnComponents(FadingSpriteComponent.class);
    }

    @ReceiveEvent
    public void gatherSprites(GatherSprites gatherSprites) {
        long time = timeManager.getTime();
        SpriteRenderer.SpriteSink spriteSink = gatherSprites.getSpriteSink();
        for (EntityRef fadingSprite : fadingSprites) {
            FadingSpriteComponent sprite = fadingSprite.getComponent(FadingSpriteComponent.class);
            Position2DComponent position = fadingSprite.getComponent(Position2DComponent.class);

            long effectStart = sprite.getEffectStart();
            long effectDuration = sprite.getEffectDuration();

            float alpha = 1 - 1f * (time - effectStart) / effectDuration;

            spriteSink.addSprite(sprite.getPriority(), "sprites", sprite.getFileName(),
                    position.getX() + sprite.getLeft(), position.getY() + sprite.getDown(),
                    sprite.getRight() - sprite.getLeft(), sprite.getUp() - sprite.getDown(), new Color(1, 1, 1, alpha));
        }
    }
}
