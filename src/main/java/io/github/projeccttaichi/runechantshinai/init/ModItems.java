package io.github.projeccttaichi.runechantshinai.init;

import io.github.projeccttaichi.runechantshinai.constants.Ids;
import net.minecraft.world.item.BlockItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Ids.MOD_ID);


    public static final DeferredItem<BlockItem> RECORD_ASSEMBLER = ITEMS.registerItem("record_assembler", properties -> new BlockItem(ModBlocks.RECORD_ASSEMBLER.get(), properties));


    public static void init(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
