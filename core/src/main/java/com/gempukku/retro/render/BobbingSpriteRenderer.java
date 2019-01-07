package com.gempukku.retro.render;

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
import com.gempukku.secsy.gaming.component.Size2DComponent;
import com.gempukku.secsy.gaming.easing.EasedValue;
import com.gempukku.secsy.gaming.easing.EasingResolver;
import com.gempukku.secsy.gaming.rendering.sprite.GatherSprites;
import com.gempukku.secsy.gaming.rendering.sprite.SpriteRenderer;
import com.gempukku.secsy.gaming.time.TimeManager;

import static com.gempukku.secsy.gaming.component.PositionResolver.*;

@RegisterSystem
public class BobbingSpriteRenderer extends AbstractLifeCycleSystem {
    @Inject
    private EntityIndexManager entityIndexManager;
    @Inject
    private EasingResolver easingResolver;
    @Inject
    private TimeManager timeManager;

    private EntityIndex bobbingSprites;

    private EasedValue bobbingValue = new EasedValue(1, "0-1-0");

    @Override
    public void initialize() {
        bobbingSprites = entityIndexManager.addIndexOnComponents(BobbingSpriteComponent.class);
    }

    @ReceiveEvent
    public void renderBobbingSprites(GatherSprites sprites) {
        SpriteRenderer.SpriteSink spriteSink = sprites.getSpriteSink();

        long time = timeManager.getTime();
        float seconds = time / 1000f;

        float amplitudePercentage = easingResolver.resolveValue(bobbingValue, seconds - MathUtils.floor(seconds));

        for (EntityRef bobbingSprite : bobbingSprites) {
            Position2DComponent position = bobbingSprite.getComponent(Position2DComponent.class);
            Size2DComponent size = bobbingSprite.getComponent(Size2DComponent.class);
            HorizontalOrientationComponent horizontal = bobbingSprite.getComponent(HorizontalOrientationComponent.class);
            BobbingSpriteComponent sprite = bobbingSprite.getComponent(BobbingSpriteComponent.class);
            String fileName = sprite.getFileName();

            Color color = new Color(1, 1, 1, 1);

            float bobbingValue = sprite.getBobbingAmplitude() * amplitudePercentage;

            if (horizontal != null && !horizontal.isFacingRight())
                spriteSink.addSprite(sprite.getPriority(), "sprites", fileName,
                        position.getX() - getLeft(size, sprite), bobbingValue + position.getY() + getDown(size, sprite),
                        -getWidth(size, sprite), getHeight(size, sprite), color);
            else
                spriteSink.addSprite(sprite.getPriority(), "sprites", fileName,
                        position.getX() + getLeft(size, sprite), bobbingValue + position.getY() + getDown(size, sprite),
                        getWidth(size, sprite), getHeight(size, sprite), color);
        }
    }
}
