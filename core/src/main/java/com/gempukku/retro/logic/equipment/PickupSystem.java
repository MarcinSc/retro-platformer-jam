package com.gempukku.retro.logic.equipment;

import com.gempukku.retro.logic.player.PlayerComponent;
import com.gempukku.retro.model.InventoryComponent;
import com.gempukku.retro.model.PickupComponent;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.gaming.physics.basic2d.SensorContactBegin;
import com.gempukku.secsy.gaming.spawn.SpawnManager;

import java.util.LinkedList;
import java.util.List;

@RegisterSystem
public class PickupSystem extends AbstractLifeCycleSystem {
    @Inject
    private SpawnManager spawnManager;
    @Inject
    private ItemProvider itemProvider;

    @ReceiveEvent
    public void pickupContact(SensorContactBegin contact, EntityRef entity, PlayerComponent player) {
        if (contact.getSensorType().equals("body")) {
            EntityRef sensorTrigger = contact.getSensorTrigger();
            if (sensorTrigger.hasComponent(PickupComponent.class)) {
                PickupComponent pickup = sensorTrigger.getComponent(PickupComponent.class);
                String pickupType = pickup.getType();
                spawnManager.despawnEntity(sensorTrigger);

                entity.send(new PickedUpObject(pickupType));
            }
        }
    }

    @ReceiveEvent
    public void addToInventory(PickedUpObject pickedUpObject, EntityRef entity, InventoryComponent inventoryComponent) {
        String pickupType = pickedUpObject.getPickupType();
        EntityRef itemEntity = itemProvider.getItemByName(pickupType);
        if (itemEntity != null) {
            List<String> items = new LinkedList<String>(inventoryComponent.getItems());
            if (!items.contains(pickupType)) {
                items.add(pickupType);
                inventoryComponent.setItems(items);
                entity.saveChanges();
                itemEntity.send(new ItemAddedToInventory(entity, pickupType));
            }
        }
    }
}
