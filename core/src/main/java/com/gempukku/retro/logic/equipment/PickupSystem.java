package com.gempukku.retro.logic.equipment;

import com.gempukku.retro.model.InventoryComponent;
import com.gempukku.retro.model.PickupComponent;
import com.gempukku.retro.model.PlayerComponent;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.entity.EntityManager;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.gaming.physics.basic2d.SensorContactBegin;

@RegisterSystem
public class PickupSystem extends AbstractLifeCycleSystem {
    @Inject
    private EntityManager entityManager;
    @Inject
    private ItemProvider itemProvider;

    @ReceiveEvent
    public void pickupContact(SensorContactBegin contact, EntityRef entity, PlayerComponent player) {
        if (contact.getSensorType().equals("body")) {
            EntityRef sensorTrigger = contact.getSensorTrigger();
            if (sensorTrigger.hasComponent(PickupComponent.class)) {
                PickupComponent pickup = sensorTrigger.getComponent(PickupComponent.class);
                String pickupType = pickup.getType();
                entityManager.destroyEntity(sensorTrigger);

                entity.send(new PickedUpObject(pickupType));
            }
        }
    }

    @ReceiveEvent
    public void addToInventory(PickedUpObject pickedUpObject, EntityRef entity, InventoryComponent inventoryComponent) {
        String pickupType = pickedUpObject.getPickupType();
        EntityRef itemEntity = itemProvider.getItemByName(pickupType);
        if (itemEntity != null) {
            inventoryComponent.getItems().add(pickupType);
            entity.saveChanges();
            itemEntity.send(new ItemAddedToInventory(entity, pickupType));
        }
    }
}
