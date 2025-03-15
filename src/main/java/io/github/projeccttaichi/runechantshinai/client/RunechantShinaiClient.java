package io.github.projeccttaichi.runechantshinai.client;

import com.mojang.logging.LogUtils;
import io.github.projeccttaichi.runechantshinai.client.gui.RecordAssemblerScreen;
import io.github.projeccttaichi.runechantshinai.constants.Ids;
import io.github.projeccttaichi.runechantshinai.init.ModMenuTypes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import org.slf4j.Logger;

@Mod(value = Ids.MOD_ID, dist = Dist.CLIENT)
public class RunechantShinaiClient {
    private static final Logger LOGGER = LogUtils.getLogger();

    public RunechantShinaiClient(IEventBus modEventBus, ModContainer container) {
        LOGGER.info("Initializing Runechant Shinai Mod (Client)");

        modEventBus.addListener(this::registerScreens);
    }



    // Event is listened to on the mod event bus
    private void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenuTypes.RECORD_ASSEMBLER.get(), RecordAssemblerScreen::new);
    }
}
