package com.gempukku.retro.logic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.gempukku.retro.logic.equipment.PickedUpObject;
import com.gempukku.retro.logic.player.PlayerComponent;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.gaming.audio.AudioManager;
import com.gempukku.secsy.gaming.combat.EntityAttacked;
import com.gempukku.secsy.gaming.input2d.EntityJumped;

@RegisterSystem
public class PlayerInteractionSounds extends AbstractLifeCycleSystem {
    @Inject
    private AudioManager audioManager;

    private Sound powerupPickupSound;
    private Sound jumpSound;
    private Sound hitSound;

    @Override
    public void initialize() {
        powerupPickupSound = Gdx.audio.newSound(Gdx.files.internal("sounds/powerup.wav"));
        jumpSound = Gdx.audio.newSound(Gdx.files.internal("sounds/jump.wav"));
        hitSound = Gdx.audio.newSound(Gdx.files.internal("sounds/hit.wav"));
    }

    @ReceiveEvent
    public void powerupPickup(PickedUpObject pickedUpObject, EntityRef entity, PlayerComponent player) {
        audioManager.playSound(powerupPickupSound);
    }

    @ReceiveEvent
    public void jump(EntityJumped jumped, EntityRef entity, PlayerComponent player) {
        audioManager.playSound(jumpSound);
    }

    @ReceiveEvent
    public void meleeAttack(EntityAttacked attacked, EntityRef entity, PlayerComponent player) {
        audioManager.playSound(hitSound);
    }

    @Override
    public void destroy() {
        powerupPickupSound.dispose();
        jumpSound.dispose();
        hitSound.dispose();
    }
}
