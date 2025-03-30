package io.github.projeccttaichi.runechantshinai.init;

import io.github.projeccttaichi.runechantshinai.compoment.RecordComponent;
import io.github.projeccttaichi.runechantshinai.constants.Ids;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModComponents {

    public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, Ids.MOD_ID);

    public static final Supplier<DataComponentType<RecordComponent>>
            RECORD_COMPONENT = DATA_COMPONENTS.register("record_component",
            () -> DataComponentType.<RecordComponent>builder()
                    .persistent(RecordComponent.CODEC)
                    .networkSynchronized(RecordComponent.STREAM_CODEC)
                    .cacheEncoding()
                    .build());

    public static void init(IEventBus modbus) {
        DATA_COMPONENTS.register(modbus);
    }
}
