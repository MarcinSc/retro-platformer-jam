package com.gempukku.retro.logic.equipment.weapon;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.gempukku.retro.logic.equipment.ItemAddedToInventory;
import com.gempukku.retro.logic.equipment.ItemProvider;
import com.gempukku.retro.logic.player.PlayerProvider;
import com.gempukku.retro.model.InventoryComponent;
import com.gempukku.retro.model.WeaponComponent;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.entity.game.GameLoopUpdate;
import com.gempukku.secsy.gaming.inventory.InventoryProvider;

import java.util.List;

@RegisterSystem(shared = InventoryProvider.class)
public class WeaponInventorySystem implements InventoryProvider {
    @Inject
    private PlayerProvider playerProvider;
    @Inject
    private ItemProvider itemProvider;

    private boolean previousPressed;
    private boolean nextPressed;

    private static final int PREVIOUS_KEY = Input.Keys.LEFT_BRACKET;
    private static final int NEXT_KEY = Input.Keys.RIGHT_BRACKET;

    @ReceiveEvent
    public void update(GameLoopUpdate update) {
        boolean previous = Gdx.input.isKeyPressed(PREVIOUS_KEY);
        if (previous && !previousPressed) {
            switchToPreviousWeapon();
            previousPressed = true;
        } else if (!previous) {
            previousPressed = false;
        }

        boolean next = Gdx.input.isKeyPressed(NEXT_KEY);
        if (next && !nextPressed) {
            switchToNextWeapon();
            nextPressed = true;
        } else if (!next) {
            nextPressed = false;
        }
    }

    @ReceiveEvent
    public void weaponPickedUp(ItemAddedToInventory itemAddedToInventory, EntityRef item, WeaponComponent weapon) {
        EntityRef player = playerProvider.getPlayer();
        InventoryComponent inventory = player.getComponent(InventoryComponent.class);
        inventory.setEquippedItem(itemAddedToInventory.getType());
        player.saveChanges();
    }

    @Override
    public EntityRef getEquippedItem(EntityRef entity) {
        InventoryComponent inventory = entity.getComponent(InventoryComponent.class);
        return itemProvider.getItemByName(inventory.getEquippedItem());
    }

    private void switchToPreviousWeapon() {
        EntityRef player = playerProvider.getPlayer();
        InventoryComponent equipmentComp = player.getComponent(InventoryComponent.class);
        String equipped = equipmentComp.getEquippedItem();
        List<String> equipmentList = equipmentComp.getItems();

        String previousWeapon = null;
        for (String itemName : equipmentList) {
            if (itemName.equals(equipped)
                    && previousWeapon != null) {
                break;
            }
            EntityRef item = itemProvider.getItemByName(itemName);
            if (item.hasComponent(WeaponComponent.class)) {
                previousWeapon = itemName;
            }
        }

        equipmentComp.setEquippedItem(previousWeapon);
        player.saveChanges();
    }

    private void switchToNextWeapon() {
        EntityRef player = playerProvider.getPlayer();
        InventoryComponent equipmentComp = player.getComponent(InventoryComponent.class);
        String equipped = equipmentComp.getEquippedItem();
        List<String> equipmentList = equipmentComp.getItems();

        String equipWeapon = null;
        String lastWeapon = null;
        for (String itemName : equipmentList) {
            EntityRef item = itemProvider.getItemByName(itemName);
            if (item.hasComponent(WeaponComponent.class)) {
                if (equipWeapon == null)
                    equipWeapon = itemName;
                if (lastWeapon != null && lastWeapon.equals(equipped)) {
                    equipWeapon = itemName;
                    break;
                }
                lastWeapon = itemName;
            }
        }

        equipmentComp.setEquippedItem(equipWeapon);
        player.saveChanges();
    }

}
