package com.gempukku.secsy.entity.serialization;

import com.gempukku.secsy.entity.Component;
import com.gempukku.secsy.entity.component.ComponentFieldConverter;
import com.gempukku.secsy.entity.component.InternalComponentManager;
import com.gempukku.secsy.entity.io.ComponentData;
import com.gempukku.secsy.entity.io.EntityData;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

public class JSONEntitySerializer {
    public EntityData readEntityData(String prefabName, JSONObject entity, NameComponentManager nameComponentManager,
                                     InternalComponentManager internalComponentManager, ComponentFieldConverter componentFieldConverter,
                                     Map<String, ? extends EntityData> entityData) {
        EntityInformation entityInformation = new EntityInformation();
        if (entity.get("import") != null) {
            String prefab = (String) entity.get("import");
            EntityData imported = entityData.get(prefab);
            if (imported == null)
                return null;

            for (ComponentData componentData : imported.getComponentsData()) {
                entityInformation.addComponent(new ComponentInformation(componentData));
            }
        }

        for (String componentName : (Iterable<String>) entity.keySet()) {
            if (!componentName.equals("import")) {
                Class<? extends Component> componentByName = nameComponentManager.getComponentByName(componentName);
                if (componentByName == null)
                    throw new IllegalStateException("Unable to find component with name (found in prefab " + prefabName + "): " + componentName);
                ComponentInformation componentInformation = new ComponentInformation(componentByName);
                Map<String, Class<?>> componentFieldTypes = internalComponentManager.getComponentFieldTypes(componentByName);
                Map<String, Class<?>> componentFieldContainingClasses = internalComponentManager.getComponentFieldContainedClasses(componentByName);
                JSONObject componentObject = (JSONObject) entity.get(componentName);
                for (String fieldName : (Iterable<String>) componentObject.keySet()) {
                    Object fieldValue = componentObject.get(fieldName);

                    Class<?> fieldType = componentFieldTypes.get(fieldName);
                    if (fieldType == null)
                        throw new IllegalStateException("Component " + componentName + " does not contain field " + fieldName + " found in prefab " + prefabName);

                    Class<?> fieldContainedClass = componentFieldContainingClasses.get(fieldName);

                    if (fieldValue != null) {
                        if (componentFieldConverter.hasConverterForType(fieldType)) {
                            if (fieldContainedClass != null)
                                fieldValue = componentFieldConverter.convertTo(fieldValue, fieldType, fieldContainedClass);
                            else
                                fieldValue = componentFieldConverter.convertTo(fieldValue, fieldType);
                        }
                    }
                    componentInformation.addField(fieldName, fieldValue);
                }
                entityInformation.addComponent(componentInformation);
            }
        }
        return entityInformation;
    }

    public void writeEntityData(Writer writer, EntityData entityData, NameComponentManager nameComponentManager,
                                final InternalComponentManager internalComponentManager, final ComponentFieldConverter converter) throws IOException {
        JSONObject object = new JSONObject();
        for (ComponentData componentData : entityData.getComponentsData()) {
            Class<? extends Component> componentClass = componentData.getComponentClass();
            String name = nameComponentManager.getNameByComponent(componentClass);
            final Map<String, Class<?>> componentFieldTypes = internalComponentManager.getComponentFieldTypes(componentClass);
            final JSONObject objectValue = new JSONObject();
            componentData.outputFields(
                    new ComponentData.ComponentDataOutput() {
                        @Override
                        public void addField(String field, Object value) {
                            if (value != null) {
                                Class<?> valueClass = value.getClass();
                                if (converter.hasConverterForType(valueClass))
                                    objectValue.put(field, converter.<Object>convertFrom(value, (Class<Object>) (componentFieldTypes.get(field))));
                                else
                                    objectValue.put(field, value);
                            }
                        }
                    }
            );
            object.put(name, objectValue);
        }
        writer.write(object.toJSONString());
    }
}
