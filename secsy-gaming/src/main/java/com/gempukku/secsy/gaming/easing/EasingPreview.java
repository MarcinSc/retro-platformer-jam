package com.gempukku.secsy.gaming.easing;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class EasingPreview extends Widget {
    private EasingResolver easingResolver;
    private Skin skin;
    private String recipe;

    public EasingPreview(EasingResolver easingResolver, Skin skin, String recipe) {
        this.easingResolver = easingResolver;
        this.skin = skin;
        this.recipe = recipe;
    }

    public void setRecipe(String recipe) {
        this.recipe = recipe;
    }

    @Override
    public float getPrefHeight() {
        if (isVisible())
            return 100;
        else
            return 0;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        float x = getX();
        float y = getY();
        float width = getWidth();
        float height = getHeight();
        Drawable white = skin.getDrawable("white");

        batch.setColor(0, 0, 0, parentAlpha);
        white.draw(batch, x, y, width, height);

        int stepCount = MathUtils.round(Math.min(width, 200));
        float step = width / stepCount;

        batch.setColor(1, 1, 1, parentAlpha);
        for (int i = 0; i < stepCount; i++) {
            float value = easingResolver.resolveValue(recipe, 1f * i / stepCount);
            white.draw(batch, x + i * step, y + value * (height - 5) + 2, 1, 1);
        }
    }
}
