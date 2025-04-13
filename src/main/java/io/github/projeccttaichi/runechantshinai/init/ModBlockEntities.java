package io.github.projeccttaichi.runechantshinai.init;

import io.github.projeccttaichi.runechantshinai.block.entity.RecordAssemblerBlockEntity;
import io.github.projeccttaichi.runechantshinai.block.entity.RecordChestBlockEntity;
import io.github.projeccttaichi.runechantshinai.constants.Ids;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Ids.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RecordChestBlockEntity>> RECORD_CHEST = 
        BLOCK_ENTITIES.register("record_chest", () -> 
            BlockEntityType.Builder.of(RecordChestBlockEntity::new, ModBlocks.RECORD_CHEST.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RecordAssemblerBlockEntity>> RECORD_ASSEMBLER =
            BLOCK_ENTITIES.register("record_assembler", () ->
                    BlockEntityType.Builder.of(RecordAssemblerBlockEntity::new, ModBlocks.RECORD_ASSEMBLER.get()).build(null));

    public static void init(IEventBus modEventBus) {
        BLOCK_ENTITIES.register(modEventBus);
    }
}
