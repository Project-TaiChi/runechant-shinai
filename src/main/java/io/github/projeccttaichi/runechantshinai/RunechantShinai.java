package io.github.projeccttaichi.runechantshinai;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.projeccttaichi.runechantshinai.config.ModConfig;
import io.github.projeccttaichi.runechantshinai.constants.Ids;
import io.github.projeccttaichi.runechantshinai.init.*;
import io.github.projeccttaichi.runechantshinai.network.ModNetwork;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig.Type;
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
        ModCreativeTabs.init(modEventBus);
        ModRecords.init(modEventBus);
        ModComponents.init(modEventBus);
        ModBlockEntities.init(modEventBus);
        ModTags.init();
        ModCapabilities.init(modEventBus);

        // 注册配置文件
        container.registerConfig(Type.COMMON, ModConfig.CONFIG_SPEC, "runechantshinai-common.toml");

        // 注册事件监听器
        modEventBus.addListener(ModNetwork::register);
    }
}
