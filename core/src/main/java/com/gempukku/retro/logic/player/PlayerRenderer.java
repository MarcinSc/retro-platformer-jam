package com.gempukku.retro.logic.player;

import com.badlogic.gdx.graphics.Color;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.gaming.combat.TemporarilyInvulnerableComponent;
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
public class PlayerRenderer extends AbstractLifeCycleSystem {
    @Inject
    private PlayerProvider playerProvider;
    @Inject
    private TimeManager timeManager;
    @Inject
    private EasingResolver easingResolver;

    private EasedValue invulnerableAlpha = new EasedValue(1, "sineIn,multFrac(10)");

    @ReceiveEvent
    public void gatherSprites(GatherSprites gatherSprites) {
        long time = timeManager.getTime();

        SpriteRenderer.SpriteSink spriteSink = gatherSprites.getSpriteSink();
        EntityRef playerEntity = playerProvider.getPlayer();
        if (playerEntity != null) {
            PlayerComponent player = playerEntity.getComponent(PlayerComponent.class);
            Position2DComponent position = playerEntity.getComponent(Position2DComponent.class);
            Size2DComponent size = playerEntity.getComponent(Size2DComponent.class);
            TemporarilyInvulnerableComponent invulnerable = playerEntity.getComponent(TemporarilyInvulnerableComponent.class);
            float alpha = 1;

            long effectStart = invulnerable.getEffectStart();
            long effectDuration = invulnerable.getEffectDuration();
            if (effectStart <= time && time < effectStart + effectDuration)
                alpha = easingResolver.resolveValue(invulnerableAlpha, 1f * (time - effectStart) / effectDuration);

            HorizontalOrientationComponent horizontal = playerEntity.getComponent(HorizontalOrientationComponent.class);
            if (!horizontal.isFacingRight())
                spriteSink.addSprite(player.getPriority(), "sprites", player.getFileName(),
                        position.getX() - getLeft(size, player), position.getY() + getDown(size, player),
                        -getWidth(size, player), getHeight(size, player),
                        new Color(1, 1, 1, alpha));
            else
                spriteSink.addSprite(player.getPriority(), "sprites", player.getFileName(),
                        position.getX() + getLeft(size, player), position.getY() + getDown(size, player),
                        getWidth(size, player), getHeight(size, player),
                        new Color(1, 1, 1, alpha));
        }
    }
}
