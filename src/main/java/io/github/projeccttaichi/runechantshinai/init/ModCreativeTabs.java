package io.github.projeccttaichi.runechantshinai.init;

import io.github.projeccttaichi.runechantshinai.constants.Ids;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static io.github.projeccttaichi.runechantshinai.constants.Ids.MOD_ID;

public class ModCreativeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB, MOD_ID);

    //CREATIVE_MODE_TABS is a DeferredRegister<CreativeModeTab>
    public static final Supplier<CreativeModeTab> MAIN_TAB = CREATIVE_MODE_TABS.register("example", () -> CreativeModeTab.builder()
            //Set the title of the tab. Don't forget to add a translation!
            .title(Component.translatable("itemGroup." + MOD_ID + ".main"))
            //Set the icon of the tab.
            .icon(() -> new ItemStack(Blocks.CRAFTING_TABLE.asItem()))
            //Add your items to the tab.
            .displayItems((params, output) -> {
                output.accept(new ItemStack(ModBlocks.RECORD_ASSEMBLER.asItem()));
            })
            .build()
    );

    public static void init(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
