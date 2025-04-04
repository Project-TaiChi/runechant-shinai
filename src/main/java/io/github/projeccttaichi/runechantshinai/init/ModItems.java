package io.github.projeccttaichi.runechantshinai.init;

import io.github.projeccttaichi.runechantshinai.compoment.RecordComponent;
import io.github.projeccttaichi.runechantshinai.constants.Ids;
import io.github.projeccttaichi.runechantshinai.item.RecordItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Ids.MOD_ID);

    public static final DeferredItem<BlockItem> RECORD_ASSEMBLER = ITEMS.register("record_assembler", 
        () -> new BlockItem(ModBlocks.RECORD_ASSEMBLER.get(), new Item.Properties()));

    public static final DeferredItem<BlockItem> RECORD_CHEST = ITEMS.register("record_chest", 
        () -> new BlockItem(ModBlocks.RECORD_CHEST.get(), new Item.Properties()));

    public static final DeferredItem<RecordItem> RECORD = ITEMS.register("record", 
        () -> new RecordItem(new Item.Properties().component(ModComponents.RECORD_COMPONENT, RecordComponent.EMPTY)));

    public static void init(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
