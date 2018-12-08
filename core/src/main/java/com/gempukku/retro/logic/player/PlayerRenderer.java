package com.gempukku.retro.logic.player;

import com.badlogic.gdx.graphics.Color;
import com.gempukku.retro.logic.combat.TemporarilyInvulnerableComponent;
import com.gempukku.retro.model.PlayerComponent;
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
import com.gempukku.secsy.gaming.rendering.sprite.GatherSprites;
import com.gempukku.secsy.gaming.rendering.sprite.SpriteRenderer;
import com.gempukku.secsy.gaming.time.TimeManager;

@RegisterSystem
public class PlayerRenderer extends AbstractLifeCycleSystem {
    @Inject
    private EntityIndexManager entityIndexManager;
    @Inject
    private TimeManager timeManager;
    @Inject
    private EasingResolver easingResolver;

    private EntityIndex players;

    private EasedValue invulnerableAlpha = new EasedValue(1, "sineIn,multFrac(10)");

    @Override
    public void initialize() {
        players = entityIndexManager.addIndexOnComponents(PlayerComponent.class);
    }

    @ReceiveEvent
    public void gatherSprites(GatherSprites gatherSprites) {
        long time = timeManager.getTime();

        SpriteRenderer.SpriteSink spriteSink = gatherSprites.getSpriteSink();
        for (EntityRef playerEntity : players) {
            PlayerComponent player = playerEntity.getComponent(PlayerComponent.class);
            Position2DComponent position = playerEntity.getComponent(Position2DComponent.class);
            TemporarilyInvulnerableComponent invulnerable = playerEntity.getComponent(TemporarilyInvulnerableComponent.class);
            float alpha = 1;

            long effectStart = invulnerable.getEffectStart();
            long effectDuration = invulnerable.getEffectDuration();
            if (effectStart <= time && time < effectStart + effectDuration)
                alpha = easingResolver.resolveValue(invulnerableAlpha, 1f * (time - effectStart) / effectDuration);

            HorizontalOrientationComponent horizontal = playerEntity.getComponent(HorizontalOrientationComponent.class);
            if (!horizontal.isFacingRight())
                spriteSink.addSprite(player.getPriority(), "sprites", player.getFileName(),
                        position.getX() + player.getRight(), position.getY() + player.getDown(),
                        player.getLeft() - player.getRight(), player.getUp() - player.getDown(),
                        new Color(1, 1, 1, alpha));
            else
                spriteSink.addSprite(player.getPriority(), "sprites", player.getFileName(),
                        position.getX() + player.getLeft(), position.getY() + player.getDown(),
                        player.getRight() - player.getLeft(), player.getUp() - player.getDown(),
                        new Color(1, 1, 1, alpha));

        }
    }
}
