package com.gempukku.secsy.gaming.component.converter;

import com.gempukku.secsy.entity.component.ComponentFieldConverter;
import com.gempukku.secsy.entity.component.ComponentFieldTypeConverter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MapConverter implements ComponentFieldTypeConverter<Map, Map> {
    private ComponentFieldConverter componentFieldConverter;

    public MapConverter(ComponentFieldConverter componentFieldConverter) {
        this.componentFieldConverter = componentFieldConverter;
    }

    @Override
    public Map convertTo(Map value) {
        return new HashMap(value);
    }

    @Override
    public Map convertTo(Map value, Class<?> containedClass) {
        if (componentFieldConverter.hasConverterForType(containedClass)) {
            Map<String, Object> result = new HashMap<String, Object>();
            Set<Map.Entry> entrySet = value.entrySet();
            for (Map.Entry entry : entrySet) {
                result.put((String) entry.getKey(), componentFieldConverter.convertTo(entry.getValue(), containedClass));
            }
            return result;
        } else {
            return convertTo(value);
        }
    }

    @Override
    public Map<String, ?> convertFrom(Map value) {
        return value;
    }

    @Override
    public Map getDefaultValue() {
        return new HashMap();
    }
}
