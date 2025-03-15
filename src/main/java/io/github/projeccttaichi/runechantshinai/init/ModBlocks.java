package io.github.projeccttaichi.runechantshinai.init;

import io.github.projeccttaichi.runechantshinai.block.RecordAssemblerBlock;
import io.github.projeccttaichi.runechantshinai.constants.Ids;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;


public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Ids.MOD_ID);

    public static DeferredBlock<RecordAssemblerBlock> RECORD_ASSEMBLER = BLOCKS.registerBlock("record_assembler", RecordAssemblerBlock::new);

    public static void init(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
