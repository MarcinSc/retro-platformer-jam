package com.gempukku.secsy.gaming.physics.basic2d;

import com.gempukku.secsy.entity.component.ComponentFieldTypeConverter;
import org.json.simple.JSONObject;

public class SensorConverter implements ComponentFieldTypeConverter<SensorDef, JSONObject> {
    @Override
    public SensorDef convertTo(JSONObject value) {
        return new SensorDef((String) value.get("type"),
                convertToFloat(value.get("left")), convertToFloat(value.get("right")),
                convertToFloat(value.get("down")), convertToFloat(value.get("up")));
    }

    private float convertToFloat(Object value) {
        if (value == null)
            return 0;
        return ((Number) value).floatValue();
    }

    @Override
    public SensorDef convertTo(JSONObject value, Class<?> containedClass) {
        return convertTo(value);
    }

    @Override
    public JSONObject convertFrom(SensorDef value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SensorDef getDefaultValue() {
        return null;
    }
}
