package com.gempukku.secsy.entity.serialization;

import com.gempukku.secsy.entity.Component;
import com.gempukku.secsy.entity.io.ComponentData;
import com.gempukku.secsy.entity.io.EntityData;
import com.gempukku.secsy.entity.io.StoredEntityData;

import java.util.HashMap;
import java.util.Map;

public class EntityInformation implements StoredEntityData {
    private int entityId = 0;
    private Map<Class<? extends Component>, ComponentInformation> components = new HashMap<Class<? extends Component>, ComponentInformation>();

    public EntityInformation() {
    }

    public EntityInformation(EntityData toCopy) {
        for (ComponentData componentData : toCopy.getComponentsData()) {
            addComponent(new ComponentInformation(componentData));
        }
    }


    public Iterable<ComponentInformation> getComponentsData() {
        return components.values();
    }

    @Override
    public ComponentData getComponentData(Class<? extends Component> componentClass) {
        return components.get(componentClass);
    }

    @Override
    public boolean hasComponent(Class<? extends Component> componentClass) {
        return components.containsKey(componentClass);
    }

    public void addComponent(ComponentInformation componentInformation) {
        components.put(componentInformation.getComponentClass(), componentInformation);
    }

    @Override
    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }
}
