package io.github.projeccttaichi.runechantshinai.compoment;

import com.mojang.serialization.Codec;
import io.github.projeccttaichi.runechantshinai.init.ModComponents;
import io.github.projeccttaichi.runechantshinai.magic.record.BaseRecord;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Holder;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import static io.github.projeccttaichi.runechantshinai.constants.Locations.modLoc;

public record RecordComponent(ResourceLocation id) {
    public static final Codec<RecordComponent> CODEC = ResourceLocation.CODEC.xmap(RecordComponent::new, RecordComponent::id);
    public static final StreamCodec<ByteBuf, RecordComponent> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC,
            RecordComponent::id,
            RecordComponent::new
    );


    public static RecordComponent EMPTY = new RecordComponent(modLoc("empty"));

    public static ItemStack createItemStack(Item item, Holder<BaseRecord> record) {
        ItemStack stack = new ItemStack(item);
        RecordComponent component = new RecordComponent(record.getKey().location());
        stack.set(ModComponents.RECORD_COMPONENT, component);
        return stack;
    }
}
