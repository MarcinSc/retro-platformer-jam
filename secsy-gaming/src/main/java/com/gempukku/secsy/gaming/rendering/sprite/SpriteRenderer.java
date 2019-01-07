package com.gempukku.secsy.gaming.rendering.sprite;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Pool;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.context.util.Prioritable;
import com.gempukku.secsy.context.util.PriorityCollection;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.entity.index.EntityIndex;
import com.gempukku.secsy.entity.index.EntityIndexManager;
import com.gempukku.secsy.gaming.asset.texture.TextureAtlasProvider;
import com.gempukku.secsy.gaming.component.HorizontalOrientationComponent;
import com.gempukku.secsy.gaming.component.Position2DComponent;
import com.gempukku.secsy.gaming.component.Size2DComponent;
import com.gempukku.secsy.gaming.easing.EasingResolver;
import com.gempukku.secsy.gaming.rendering.pipeline.RenderToPipeline;
import com.gempukku.secsy.gaming.time.TimeManager;

import java.util.HashMap;
import java.util.Map;

import static com.gempukku.secsy.gaming.component.PositionResolver.*;

@RegisterSystem(profiles = "sprites")
public class SpriteRenderer extends AbstractLifeCycleSystem {
    @Inject
    private EntityIndexManager entityIndexManager;
    @Inject
    private TextureAtlasProvider textureAtlasProvider;
    @Inject
    private TimeManager timeManager;
    @Inject
    private EasingResolver easingResolver;
    @Inject(optional = true)
    private AnimatedSpriteProvider animatedSpriteProvider;

    private SpriteBatch spriteBatch;

    private EntityIndex spriteEntities;
    private EntityIndex tiledSpriteEntities;
    private EntityIndex animatedSpriteEntities;

    private PriorityCollection<RenderableSprite> sprites = new PriorityCollection<RenderableSprite>();
    private SpriteSinkImpl spriteSink = new SpriteSinkImpl();

    private Map<String, Texture> tiledTextures = new HashMap<String, Texture>();

    private NormalRenderableSpritePool normalPool = new NormalRenderableSpritePool();
    private TiledRenderableSpritePool tiledPool = new TiledRenderableSpritePool();

    @Override
    public void initialize() {
        spriteBatch = new SpriteBatch();

        spriteEntities = entityIndexManager.addIndexOnComponents(SpriteComponent.class);
        tiledSpriteEntities = entityIndexManager.addIndexOnComponents(TiledSpriteComponent.class);
        animatedSpriteEntities = entityIndexManager.addIndexOnComponents(AnimatedSpriteComponent.class);
    }

    @ReceiveEvent(priorityName = "gaming.renderer.sprites")
    public void renderSprites(RenderToPipeline renderToPipeline, EntityRef cameraEntity) {
        cameraEntity.send(new GatherSprites(spriteSink));

        if (!sprites.isEmpty()) {
            renderToPipeline.getRenderPipeline().getCurrentBuffer().begin();

            Camera camera = renderToPipeline.getCamera();

            Gdx.gl.glEnable(GL20.GL_BLEND);
            spriteBatch.setProjectionMatrix(camera.combined);
            spriteBatch.begin();
            renderSprites();
            spriteBatch.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);

            renderToPipeline.getRenderPipeline().getCurrentBuffer().end();

            sprites.clear();
        }
    }

    @ReceiveEvent
    public void gatherSprites(GatherSprites gatherSprites) {
        long time = timeManager.getTime();
        float seconds = time / 1000f;

        SpriteSink spriteSink = gatherSprites.getSpriteSink();
        for (EntityRef spriteEntity : spriteEntities) {
            SpriteComponent sprite = spriteEntity.getComponent(SpriteComponent.class);
            String fileName = sprite.getFileName();

            processSprite(spriteSink, spriteEntity, sprite, fileName);
        }
        for (EntityRef animatedSpriteEntity : animatedSpriteEntities) {
            AnimatedSpriteComponent sprite = animatedSpriteEntity.getComponent(AnimatedSpriteComponent.class);
            String fileName = animatedSpriteProvider.getSpriteForEntity(animatedSpriteEntity);

            processSprite(spriteSink, animatedSpriteEntity, sprite, fileName);
        }
        for (EntityRef tiledSpriteEntity : tiledSpriteEntities) {
            Position2DComponent position = tiledSpriteEntity.getComponent(Position2DComponent.class);
            Size2DComponent size = tiledSpriteEntity.getComponent(Size2DComponent.class);
            TiledSpriteComponent sprite = tiledSpriteEntity.getComponent(TiledSpriteComponent.class);
            HorizontalOrientationComponent horizontal = tiledSpriteEntity.getComponent(HorizontalOrientationComponent.class);

            if (horizontal != null && !horizontal.isFacingRight())
                spriteSink.addTiledSprite(sprite.getPriority(), sprite.getFileName(),
                        position.getX() - getLeft(size, sprite), position.getY() + getDown(size, sprite),
                        -getWidth(size, sprite), getHeight(size, sprite), sprite.getTileXCount(), sprite.getTileYCount(), Color.WHITE);
            else
                spriteSink.addTiledSprite(sprite.getPriority(), sprite.getFileName(),
                        position.getX() + getLeft(size, sprite), position.getY() + getDown(size, sprite),
                        getWidth(size, sprite), getHeight(size, sprite), sprite.getTileXCount(), sprite.getTileYCount(), Color.WHITE);
        }
    }

    private void processSprite(SpriteSink spriteSink, EntityRef spriteEntity, DisplayableSpriteComponent sprite, String fileName) {
        Position2DComponent position = spriteEntity.getComponent(Position2DComponent.class);
        Size2DComponent size = spriteEntity.getComponent(Size2DComponent.class);
        HorizontalOrientationComponent horizontal = spriteEntity.getComponent(HorizontalOrientationComponent.class);

        Color color = new Color(1, 1, 1, 1);

        if (horizontal != null && !horizontal.isFacingRight())
            spriteSink.addSprite(sprite.getPriority(), "sprites", fileName,
                    position.getX() - getLeft(size, sprite), position.getY() + getDown(size, sprite),
                    -getWidth(size, sprite), getHeight(size, sprite), color);
        else
            spriteSink.addSprite(sprite.getPriority(), "sprites", fileName,
                    position.getX() + getLeft(size, sprite), position.getY() + getDown(size, sprite),
                    getWidth(size, sprite), getHeight(size, sprite), color);
    }

    private void renderSprites() {
        for (RenderableSprite sprite : sprites) {
            sprite.render(spriteBatch);
            sprite.freeObject();
        }
    }

    @Override
    public void destroy() {
        for (Texture texture : tiledTextures.values())
            texture.dispose();

        spriteBatch.dispose();
    }

    private interface RenderableSprite {
        void render(SpriteBatch spriteBatch);

        void freeObject();
    }

    private class NormalRenderableSprite implements RenderableSprite, Prioritable {
        private String textureAtlasId;
        private String texturePath;
        private float x;
        private float y;
        private float width;
        private float height;
        private float priority;
        private Color color;

        @Override
        public float getPriority() {
            return priority;
        }

        @Override
        public void render(SpriteBatch spriteBatch) {
            TextureRegion textureRegion = textureAtlasProvider.getTexture(textureAtlasId, texturePath);
            spriteBatch.setColor(color);
            spriteBatch.draw(textureRegion, x, y, width, height);
        }

        @Override
        public void freeObject() {
            normalPool.free(this);
        }
    }

    private class TiledRenderableSprite implements RenderableSprite, Prioritable {
        private String texturePath;
        private float x;
        private float y;
        private float width;
        private float height;
        private float tileCountX;
        private float tileCountY;
        private float priority;
        private Color color;

        @Override
        public float getPriority() {
            return priority;
        }

        @Override
        public void render(SpriteBatch spriteBatch) {
            spriteBatch.setColor(color);
            spriteBatch.draw(getTexture(texturePath), x, y, width, height, 0, 0, tileCountX, tileCountY);
        }

        @Override
        public void freeObject() {
            tiledPool.free(this);
        }
    }

    private Texture getTexture(String texturePath) {
        Texture texture = tiledTextures.get(texturePath);
        if (texture == null) {
            texture = new Texture(Gdx.files.internal(texturePath));
            texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

            tiledTextures.put(texturePath, texture);
        }
        return texture;
    }

    public interface SpriteSink {
        void addSprite(float priority, String textureAtlasId, String texturePath,
                       float x, float y, float width, float height, Color color);

        void addTiledSprite(float priority, String texturePath,
                            float x, float y, float width, float height,
                            float tileCountX, float tileCountY, Color color);
    }

    private class SpriteSinkImpl implements SpriteSink {
        @Override
        public void addSprite(float priority, String textureAtlasId, String texturePath, float x, float y, float width, float height, Color color) {
            NormalRenderableSprite sprite = normalPool.obtain();
            sprite.priority = priority;
            sprite.textureAtlasId = textureAtlasId;
            sprite.texturePath = texturePath;
            sprite.x = x;
            sprite.y = y;
            sprite.width = width;
            sprite.height = height;
            sprite.color = color;
            sprites.add(sprite);
        }

        @Override
        public void addTiledSprite(float priority, String texturePath, float x, float y, float width, float height, float tileCountX, float tileCountY, Color color) {
            TiledRenderableSprite sprite = tiledPool.obtain();
            sprite.priority = priority;
            sprite.texturePath = texturePath;
            sprite.x = x;
            sprite.y = y;
            sprite.width = width;
            sprite.height = height;
            sprite.tileCountX = tileCountX;
            sprite.tileCountY = tileCountY;
            sprite.color = color;
            sprites.add(sprite);
        }
    }

    private class NormalRenderableSpritePool extends Pool<NormalRenderableSprite> {
        @Override
        protected NormalRenderableSprite newObject() {
            return new NormalRenderableSprite();
        }
    }

    private class TiledRenderableSpritePool extends Pool<TiledRenderableSprite> {
        @Override
        protected TiledRenderableSprite newObject() {
            return new TiledRenderableSprite();
        }
    }
}
