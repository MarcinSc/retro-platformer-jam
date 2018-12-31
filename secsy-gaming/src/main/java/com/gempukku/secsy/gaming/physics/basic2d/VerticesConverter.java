package com.gempukku.secsy.gaming.physics.basic2d;

import com.gempukku.secsy.entity.component.ComponentFieldTypeConverter;

import java.util.List;

public class VerticesConverter implements ComponentFieldTypeConverter<Vertices, List> {
    @Override
    public Vertices convertTo(List value) {
        float[] vertices = new float[value.size() * 2];
        for (int i = 0; i < vertices.length; i += 2) {
            String vertexStr = (String) value.get(i / 2);
            String[] split = vertexStr.split(",", 2);

            vertices[i] = Float.parseFloat(split[0]);
            vertices[i + 1] = Float.parseFloat(split[1]);
        }
        return new Vertices(vertices);
    }

    @Override
    public Vertices convertTo(List value, Class<?> containedClass) {
        return convertTo(value);
    }

    @Override
    public List convertFrom(Vertices value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Vertices getDefaultValue() {
        return null;
    }
}
