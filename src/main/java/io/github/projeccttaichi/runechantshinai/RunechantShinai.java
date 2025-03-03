package io.github.projeccttaichi.runechantshinai;

import com.mojang.logging.LogUtils;
import io.github.projeccttaichi.runechantshinai.capability.PlayerManaImpl;
import io.github.projeccttaichi.runechantshinai.config.ModConfig;
import io.github.projeccttaichi.runechantshinai.constants.Ids;
import io.github.projeccttaichi.runechantshinai.init.*;
import io.github.projeccttaichi.runechantshinai.network.ModNetwork;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.slf4j.Logger;

@Mod(Ids.MOD_ID)
public class RunechantShinai {
    private static final Logger LOGGER = LogUtils.getLogger();

    public RunechantShinai(IEventBus modEventBus, ModContainer container) {
        LOGGER.info("Initializing Runechant Shinai Mod");
        // 注册各种组件
        ModItems.init(modEventBus);
        ModBlocks.init(modEventBus);
        ModRecipes.init(modEventBus);
        ModMenuTypes.init(modEventBus);
        ModEntities.init(modEventBus);
        ModTags.init();

        // 注册配置文件
        container.registerConfig(Type.COMMON, ModConfig.CONFIG_SPEC, "runechantshinai-common.toml");

        // 注册事件监听器
        modEventBus.addListener(this::registerCapabilities);
        modEventBus.addListener(ModNetwork::register);

    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        // 注册玩家法力值能力
        event.registerEntity(ModCapabilities.PLAYER_MANA_ENTITY_CAPABILITY, EntityType.PLAYER, (player, context) -> new PlayerManaImpl(player));
    }

}
