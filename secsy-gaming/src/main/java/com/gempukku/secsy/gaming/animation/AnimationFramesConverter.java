package com.gempukku.secsy.gaming.animation;

import com.gempukku.secsy.entity.component.ComponentFieldTypeConverter;

public class AnimationFramesConverter implements ComponentFieldTypeConverter<AnimationFrames, String> {
    @Override
    public AnimationFrames convertTo(String value) {
        String[] split = value.split(",");
        AnimationFrame[] frames = new AnimationFrame[split.length];
        for (int i = 0; i < split.length; i++) {
            String[] frameSplit = split[i].split("\\*", 2);
            frames[i] = new AnimationFrame(frameSplit[1], Long.parseLong(frameSplit[0]));
        }
        return new AnimationFrames(frames);
    }

    @Override
    public AnimationFrames convertTo(String value, Class<?> containedClass) {
        return convertTo(value);
    }

    @Override
    public String convertFrom(AnimationFrames value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AnimationFrames getDefaultValue() {
        return null;
    }
}
