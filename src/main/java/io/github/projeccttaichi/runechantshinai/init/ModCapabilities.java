package io.github.projeccttaichi.runechantshinai.init;

import io.github.projeccttaichi.runechantshinai.capability.PlayerMana;
import io.github.projeccttaichi.runechantshinai.capability.PlayerManaImpl;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

import static io.github.projeccttaichi.runechantshinai.constants.Locations.modLoc;

public class ModCapabilities {
    public static final EntityCapability<PlayerMana, Void> PLAYER_MANA_ENTITY_CAPABILITY = EntityCapability.createVoid(modLoc("player_mana"), PlayerMana.class);

    public static void init(IEventBus modEventBus) {
        modEventBus.addListener(ModCapabilities::registerCapabilities);
    }

    private static void registerCapabilities(RegisterCapabilitiesEvent event) {
        // 注册玩家法力值能力
        event.registerEntity(PLAYER_MANA_ENTITY_CAPABILITY, EntityType.PLAYER, (player, context) -> new PlayerManaImpl(player));
    }
}
