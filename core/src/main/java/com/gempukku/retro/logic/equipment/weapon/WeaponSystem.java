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
import com.gempukku.secsy.gaming.combat.CombatComponent;
import com.gempukku.secsy.gaming.combat.EntityAttacked;
import com.gempukku.secsy.gaming.combat.EntityDamaged;
import com.gempukku.secsy.gaming.combat.VulnerableComponent;
import com.gempukku.secsy.gaming.component.HorizontalOrientationComponent;
import com.gempukku.secsy.gaming.component.Position2DComponent;
import com.gempukku.secsy.gaming.faction.FactionManager;
import com.gempukku.secsy.gaming.physics.basic2d.Basic2dPhysics;
import com.gempukku.secsy.gaming.physics.basic2d.MovingComponent;
import com.gempukku.secsy.gaming.spawn.SpawnManager;
import com.gempukku.secsy.gaming.time.TimeManager;
import com.google.common.base.Predicate;

import javax.annotation.Nullable;
import java.util.List;

@RegisterSystem
public class WeaponSystem {
    @Inject
    private ItemProvider itemProvider;
    @Inject
    private Basic2dPhysics basic2dPhysics;
    @Inject
    private TimeManager timeManager;
    @Inject
    private PlayerProvider playerProvider;
    @Inject
    private SpawnManager spawnManager;
    @Inject
    private FactionManager factionManager;

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

    @ReceiveEvent
    public void weaponPickedUp(ItemAddedToInventory itemAddedToInventory, EntityRef item, WeaponComponent weapon) {
        EntityRef player = playerProvider.getPlayer();
        InventoryComponent inventory = player.getComponent(InventoryComponent.class);
        inventory.setEquippedItem(itemAddedToInventory.getType());
        player.saveChanges();
    }

    @ReceiveEvent
    public void entityAttacked(EntityAttacked entityAttacked, EntityRef attacker, InventoryComponent inventoryComponent) {
        String equippedItemName = inventoryComponent.getEquippedItem();
        EntityRef equippedItem = itemProvider.getItemByName(equippedItemName);
        equippedItem.send(new WeaponAttack(attacker));
    }

    @ReceiveEvent
    public void attackedWithSpawningWeapon(WeaponAttack attackedWith, EntityRef weapon, SpawningWeaponComponent spawningWeapon) {
        EntityRef attacker = attackedWith.getAttacker();
        HorizontalOrientationComponent orientation = attacker.getComponent(HorizontalOrientationComponent.class);
        Position2DComponent position = attacker.getComponent(Position2DComponent.class);
        float x = position.getX() + spawningWeapon.getX() * (orientation.isFacingRight() ? 1 : -1);
        float y = position.getY() + spawningWeapon.getY();

        String prefab = spawningWeapon.getPrefab();
        EntityRef spawnedEntity = spawnManager.spawnEntityAt(prefab, x, y);
        if (!orientation.isFacingRight()) {
            MovingComponent moving = spawnedEntity.getComponent(MovingComponent.class);
            moving.setSpeedX(-moving.getSpeedX());
            spawnedEntity.saveChanges();
        }

        CombatComponent combat = attacker.getComponent(CombatComponent.class);
        combat.setNextAttackTime(timeManager.getTime() + spawningWeapon.getCoolDown());
        attacker.saveChanges();
    }

    @ReceiveEvent
    public void attackedWithMeleeWeapon(WeaponAttack attackedWith, EntityRef weapon, MeleeWeaponComponent meleeWeapon) {
        EntityRef attacker = attackedWith.getAttacker();
        Position2DComponent position = attacker.getComponent(Position2DComponent.class);
        float x = position.getX();
        float y = position.getY();
        HorizontalOrientationComponent orientation = attacker.getComponent(HorizontalOrientationComponent.class);
        Iterable<EntityRef> attackedEntities;
        float minX;
        float maxX;
        if (orientation.isFacingRight()) {
            minX = x + meleeWeapon.getMinimumRange();
            maxX = x + meleeWeapon.getMaximumRange();
        } else {
            minX = x - meleeWeapon.getMaximumRange();
            maxX = x - meleeWeapon.getMinimumRange();
        }
        attackedEntities = basic2dPhysics.getSensorTriggersFor(attacker, "meleeAttack",
                minX, maxX, y + meleeWeapon.getMinimumHeight(), y + meleeWeapon.getMaximumHeight(),
                new Predicate<EntityRef>() {
                    @Override
                    public boolean apply(@Nullable EntityRef entityRef) {
                        return entityRef.hasComponent(VulnerableComponent.class);
                    }
                });
        for (EntityRef attackedEntity : attackedEntities) {
            if (factionManager.isEnemy(attacker, attackedEntity))
                attackedEntity.send(new EntityDamaged(attacker, weapon, meleeWeapon.getDamageAmount()));
        }

        CombatComponent combat = attacker.getComponent(CombatComponent.class);
        combat.setNextAttackTime(timeManager.getTime() + meleeWeapon.getCoolDown());
        attacker.saveChanges();
    }
}
