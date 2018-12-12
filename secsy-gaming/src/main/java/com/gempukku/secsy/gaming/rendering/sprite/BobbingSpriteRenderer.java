package com.gempukku.secsy.gaming.rendering.sprite;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.entity.index.EntityIndex;
import com.gempukku.secsy.entity.index.EntityIndexManager;
import com.gempukku.secsy.gaming.component.HorizontalOrientationComponent;
import com.gempukku.secsy.gaming.component.Position2DComponent;
import com.gempukku.secsy.gaming.easing.EasedValue;
import com.gempukku.secsy.gaming.easing.EasingResolver;
import com.gempukku.secsy.gaming.time.TimeManager;

@RegisterSystem(profiles = "bobbingSprites")
public class BobbingSpriteRenderer extends AbstractLifeCycleSystem {
    @Inject
    private EntityIndexManager entityIndexManager;
    @Inject
    private TimeManager timeManager;
    @Inject
    private EasingResolver easingResolver;

    private EntityIndex bobbingSpriteEntities;

    private EasedValue easedValue = new EasedValue(1, "0-1-0");

    @Override
    public void initialize() {
        bobbingSpriteEntities = entityIndexManager.addIndexOnComponents(BobbingSpriteComponent.class);
    }

    @ReceiveEvent
    public void renderSprites(GatherSprites gatherSprites) {
        long time = timeManager.getTime();

        float seconds = time / 1000f;

        float amplitudePercentage = easingResolver.resolveValue(easedValue, seconds - MathUtils.floor(seconds));

        SpriteRenderer.SpriteSink spriteSink = gatherSprites.getSpriteSink();
        for (EntityRef bobbingSpriteEntity : bobbingSpriteEntities) {
            Position2DComponent position = bobbingSpriteEntity.getComponent(Position2DComponent.class);
            BobbingSpriteComponent bobbingSprite = bobbingSpriteEntity.getComponent(BobbingSpriteComponent.class);
            HorizontalOrientationComponent horizontal = bobbingSpriteEntity.getComponent(HorizontalOrientationComponent.class);

            float y = bobbingSprite.getBobbingAmplitude() * amplitudePercentage;

            if (horizontal != null && !horizontal.isFacingRight())
                spriteSink.addSprite(bobbingSprite.getPriority(), "sprites", bobbingSprite.getFileName(), position.getX() + bobbingSprite.getRight(), y + position.getY() + bobbingSprite.getDown(),
                        bobbingSprite.getLeft() - bobbingSprite.getRight(), bobbingSprite.getUp() - bobbingSprite.getDown(), Color.WHITE);
            else
                spriteSink.addSprite(bobbingSprite.getPriority(), "sprites", bobbingSprite.getFileName(), position.getX() + bobbingSprite.getLeft(), y + position.getY() + bobbingSprite.getDown(),
                        bobbingSprite.getRight() - bobbingSprite.getLeft(), bobbingSprite.getUp() - bobbingSprite.getDown(), Color.WHITE);
        }
    }
}
