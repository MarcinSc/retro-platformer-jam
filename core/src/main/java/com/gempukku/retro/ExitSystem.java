package com.gempukku.retro;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.entity.game.GameLoopUpdate;

@RegisterSystem
public class ExitSystem {
    @ReceiveEvent
    public void checkForExit(GameLoopUpdate update) {
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE))
            Gdx.app.exit();
    }
}
