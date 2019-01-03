package com.gempukku.secsy.gaming.input;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.Array;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.util.PriorityCollection;

@RegisterSystem(profiles = "input", shared = {InputProvider.class, InputProcessor.class})
public class InputSystem implements InputProvider, InputProcessor {
    private PriorityCollection<InputProcessor> processors = new PriorityCollection<InputProcessor>();

    private InputMultiplexer inputMultiplexer = new InputMultiplexer();

    @Override
    public void registerInputProcessor(InputProcessor inputProcessor, float priority) {
        processors.remove(inputProcessor);
        processors.put(inputProcessor, priority);
        setToMultiplexer();
    }

    @Override
    public void deregisterInputProcessor(InputProcessor inputProcessor) {
        processors.remove(inputProcessor);
        setToMultiplexer();
    }

    private void setToMultiplexer() {
        Array<InputProcessor> arr = new Array<InputProcessor>(processors.getSize());
        for (InputProcessor processor : processors) {
            arr.add(processor);
        }
        inputMultiplexer.setProcessors(arr);
    }

    @Override
    public boolean keyDown(int keycode) {
        return inputMultiplexer.keyDown(keycode);
    }

    @Override
    public boolean keyUp(int keycode) {
        return inputMultiplexer.keyUp(keycode);
    }

    @Override
    public boolean keyTyped(char character) {
        return inputMultiplexer.keyTyped(character);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return inputMultiplexer.touchDown(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return inputMultiplexer.touchUp(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return inputMultiplexer.touchDragged(screenX, screenY, pointer);
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return inputMultiplexer.mouseMoved(screenX, screenY);
    }

    @Override
    public boolean scrolled(int amount) {
        return inputMultiplexer.scrolled(amount);
    }
}
