package io.github.projeccttaichi.runechantshinai.init;

import io.github.projeccttaichi.runechantshinai.block.RecordAssemblerBlock;
import io.github.projeccttaichi.runechantshinai.block.RecordChestBlock;
import io.github.projeccttaichi.runechantshinai.constants.Ids;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Ids.MOD_ID);

    public static DeferredBlock<RecordAssemblerBlock> RECORD_ASSEMBLER = BLOCKS.register("record_assembler", 
        () -> new RecordAssemblerBlock(BlockBehaviour.Properties.of()
            .strength(2.5F)
            .requiresCorrectToolForDrops()
        ));
    
    public static DeferredBlock<RecordChestBlock> RECORD_CHEST = BLOCKS.register("record_chest", 
        () -> new RecordChestBlock(BlockBehaviour.Properties.of()
            .strength(2.5F)
            .requiresCorrectToolForDrops()
        ));

    public static void init(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
