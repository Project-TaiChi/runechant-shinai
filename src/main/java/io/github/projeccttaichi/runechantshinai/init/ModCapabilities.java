package io.github.projeccttaichi.runechantshinai.init;

import io.github.projeccttaichi.runechantshinai.capability.PlayerMana;
import io.github.projeccttaichi.runechantshinai.capability.PlayerManaImpl;
import io.github.projeccttaichi.runechantshinai.capability.RecordStorageHandler;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

import static io.github.projeccttaichi.runechantshinai.constants.Locations.modLoc;

public class ModCapabilities {
    public static final EntityCapability<PlayerMana, Void> PLAYER_MANA_ENTITY_CAPABILITY = EntityCapability.createVoid(modLoc("player_mana"), PlayerMana.class);
    public static final BlockCapability<RecordStorageHandler, Void> RECORD_STORAGE_CAPABILITY = BlockCapability.createVoid(modLoc("record_storage"), RecordStorageHandler.class);

    public static void init(IEventBus modEventBus) {
        modEventBus.addListener(ModCapabilities::registerCapabilities);
    }

    private static void registerCapabilities(RegisterCapabilitiesEvent event) {
        // 注册玩家法力值能力
        event.registerEntity(PLAYER_MANA_ENTITY_CAPABILITY, EntityType.PLAYER, (player, context) -> new PlayerManaImpl(player));
        
        // 注册记录存储能力
        event.registerBlock(RECORD_STORAGE_CAPABILITY, ModBlocks.RECORD_CHEST.get());
    }
}
