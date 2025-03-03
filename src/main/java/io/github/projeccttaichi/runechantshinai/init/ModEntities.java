package io.github.projeccttaichi.runechantshinai.init;

import io.github.projeccttaichi.runechantshinai.constants.Ids;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static io.github.projeccttaichi.runechantshinai.constants.Locations.modLoc;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = 
            DeferredRegister.createEntities(Ids.MOD_ID);
    
    // 注册法术实体

    public static void init(IEventBus eventBus) {
        ENTITIES.register(eventBus);
    }

    private static ResourceKey<EntityType<?>> entityKey(String name) {
        return ResourceKey.create(Registries.ENTITY_TYPE, modLoc(name));
    }

    private static <T extends Entity> Supplier<EntityType<T>> registerEntityType(String name, EntityType.Builder<T> builder) {
        return () -> builder.build(entityKey(name));
    }
}
