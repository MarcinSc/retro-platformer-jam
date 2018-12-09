package com.gempukku.retro.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;

@RegisterSystem(shared = FontProvider.class)
public class BitmapFontSystem extends AbstractLifeCycleSystem implements FontProvider {

    private BitmapFont font;

    @Override
    public void initialize() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/5px2bus.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 5;
        parameter.color = Color.WHITE;
        font = generator.generateFont(parameter);
        generator.dispose();
    }

    @Override
    public BitmapFont getFont() {
        return font;
    }

    @Override
    public void destroy() {
        font.dispose();
    }
}
