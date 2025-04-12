package io.github.projeccttaichi.runechantshinai.init;

import io.github.projeccttaichi.runechantshinai.constants.Ids;
import io.github.projeccttaichi.runechantshinai.menu.RecordAssemblerMenu;
import io.github.projeccttaichi.runechantshinai.menu.RecordChestMenu;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES =
            DeferredRegister.create(BuiltInRegistries.MENU, Ids.MOD_ID);

    public static final Supplier<MenuType<RecordAssemblerMenu>> RECORD_ASSEMBLER = MENU_TYPES.register("record_assembler", 
            () -> new MenuType<>(RecordAssemblerMenu::new, FeatureFlags.VANILLA_SET));

    public static final Supplier<MenuType<RecordChestMenu>> RECORD_CHEST = MENU_TYPES.register("record_chest", 
            () -> new MenuType<>(RecordChestMenu::new, FeatureFlags.VANILLA_SET));

    public static void init(IEventBus modEventBus) {
        MENU_TYPES.register(modEventBus);
    }
}
