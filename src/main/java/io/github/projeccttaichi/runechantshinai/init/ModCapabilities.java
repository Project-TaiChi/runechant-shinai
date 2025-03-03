package io.github.projeccttaichi.runechantshinai.init;

import io.github.projeccttaichi.runechantshinai.capability.PlayerMana;
import net.neoforged.neoforge.capabilities.EntityCapability;

import static io.github.projeccttaichi.runechantshinai.constants.Locations.modLoc;

public class ModCapabilities {
    public static final EntityCapability<PlayerMana, Void> PLAYER_MANA_ENTITY_CAPABILITY = EntityCapability.createVoid(modLoc("player_mana"), PlayerMana.class);
}
