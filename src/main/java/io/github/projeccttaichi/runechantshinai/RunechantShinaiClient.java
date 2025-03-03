package io.github.projeccttaichi.runechantshinai;

import com.mojang.logging.LogUtils;
import io.github.projeccttaichi.runechantshinai.capability.PlayerManaImpl;
import io.github.projeccttaichi.runechantshinai.constants.Ids;
import io.github.projeccttaichi.runechantshinai.init.*;
import io.github.projeccttaichi.runechantshinai.network.ModNetwork;
import net.minecraft.world.entity.EntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.slf4j.Logger;

@Mod(value = Ids.MOD_ID, dist = Dist.CLIENT)
public class RunechantShinaiClient {
    private static final Logger LOGGER = LogUtils.getLogger();

    public RunechantShinaiClient(IEventBus modEventBus, ModContainer container) {
        LOGGER.info("Initializing Runechant Shinai Mod (Client)");
    }

}
