package com.gempukku.secsy.gaming.animation;

import com.gempukku.secsy.entity.component.ComponentFieldTypeConverter;

public class AnimationResolverConverter implements ComponentFieldTypeConverter<AnimationResolver, String> {
    @Override
    public AnimationResolver convertTo(String value) {
        String[] split = value.split(",", 2);

        return new DefaultAnimationResolver(split[0], Long.parseLong(split[1]));
    }

    @Override
    public AnimationResolver convertTo(String value, Class<?> containedClass) {
        return convertTo(value);
    }

    @Override
    public String convertFrom(AnimationResolver value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AnimationResolver getDefaultValue() {
        return null;
    }
}
