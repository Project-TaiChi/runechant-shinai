package io.github.projeccttaichi.runechantshinai.capability;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.projeccttaichi.runechantshinai.init.ModComponents;
import io.github.projeccttaichi.runechantshinai.init.ModRecords;
import io.github.projeccttaichi.runechantshinai.magic.record.BaseRecord;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.DataComponentUtil;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.slf4j.Logger;

import java.util.Objects;

public class RecordStorageHandler implements IItemHandlerModifiable, INBTSerializable<CompoundTag> {
    private final NonNullList<ItemStack> entries;

    private static final Logger LOGGER = LogUtils.getLogger();

    public RecordStorageHandler() {
        this.entries = NonNullList.withSize(ModRecords.RECORD_REGISTRY.size(), ItemStack.EMPTY);
    }

    private static ResourceLocation getRecordName(int index) {
        BaseRecord record = ModRecords.RECORD_REGISTRY.byId(index);
        if (record == null) {
            return null;
        }

        return ModRecords.RECORD_REGISTRY.getKey(record);
    }

    private static boolean isRecord(ItemStack stack, ResourceLocation name) {
        var component = stack.get(ModComponents.RECORD_COMPONENT.get());
        return component != null && component.id().equals(name);
    }

    private static boolean isRecord(ItemStack stack, int index) {
        return isRecord(stack, getRecordName(index));
    }

    @Override
    public void setStackInSlot(int i, ItemStack itemStack) {
        if (isRecord(itemStack, i)) {
            entries.set(i, itemStack);
        }
    }

    @Override
    public int getSlots() {
        return entries.size();
    }

    @Override
    public ItemStack getStackInSlot(int i) {
        if (i < 0 || i >= entries.size()) {
            return ItemStack.EMPTY;
        }
        return entries.get(i);
    }

    @Override
    public ItemStack insertItem(int index, ItemStack itemStack, boolean simulate) {
        if (index < 0 || index >= entries.size()) {
            return itemStack;
        }

        if (itemStack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        if (!isRecord(itemStack, index)) {
            return itemStack;
        }

        int limit = getSlotLimit(index);
        ItemStack existing = entries.get(index);
        boolean reachedLimit = itemStack.getCount() > limit;
        if (!simulate) {
            if (existing.isEmpty()) {
                this.entries.set(index, reachedLimit ? itemStack.copyWithCount(limit) : itemStack);
            } else {
                existing.grow(reachedLimit ? limit : itemStack.getCount());
            }

            this.onContentsChanged(index);
        }

        return reachedLimit ? itemStack.copyWithCount(itemStack.getCount() - limit) : ItemStack.EMPTY;

    }

    public ItemStack insetAuto(ItemStack stack, boolean simulate) {

        var component = stack.get(ModComponents.RECORD_COMPONENT.get());

        if (component == null) {
            return stack;
        }
        int index = ModRecords.RECORD_REGISTRY.getId(component.id());

        if (index == -1) {
            return stack;
        }

        return insertItem(index, stack, simulate);

    }

    public void onContentsChanged(int slot) {
        // Do nothing
    }

    @Override
    public ItemStack extractItem(int index, int amount, boolean simulate) {
        if (index < 0 || index >= entries.size()) {
            return ItemStack.EMPTY;
        }

        ItemStack existing = entries.get(index);
        if (existing.isEmpty()) {
            return ItemStack.EMPTY;
        }

        int toExtract = Math.min(amount, existing.getMaxStackSize());
        if (existing.getCount() <= toExtract) {
            if (!simulate) {
                this.entries.set(index, ItemStack.EMPTY);
                this.onContentsChanged(index);
                return existing;
            } else {
                return existing.copy();
            }
        } else {
            if (!simulate) {
                this.entries.set(index, existing.copyWithCount(existing.getCount() - toExtract));
                this.onContentsChanged(index);
            }

            return existing.copyWithCount(toExtract);
        }
    }

    @Override
    public int getSlotLimit(int i) {
        return 1024;
    }

    @Override
    public boolean isItemValid(int i, ItemStack itemStack) {
        return true;
    }

    public static final Codec<ItemStack> NON_LIMIT_ITEM_CODEC = Codec.lazyInitialized(
            () -> RecordCodecBuilder.create(
                    p_347288_ -> p_347288_.group(
                            ItemStack.ITEM_NON_AIR_CODEC.fieldOf("id").forGetter(ItemStack::getItemHolder),
                            Codec.INT.fieldOf("count").orElse(1).forGetter(ItemStack::getCount),
                            DataComponentPatch.CODEC
                                    .optionalFieldOf("components", DataComponentPatch.EMPTY)
                                    .forGetter(ItemStack::getComponentsPatch))
                            .apply(p_347288_, ItemStack::new)));

    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        ListTag nbtTagList = new ListTag();

        for (int i = 0; i < this.entries.size(); ++i) {
            ItemStack stack = this.entries.get(i);
            if (!stack.isEmpty()) {
                CompoundTag itemTag = new CompoundTag();
                ResourceLocation name = getRecordName(i);
                Objects.requireNonNull(name);
                itemTag.putString("Name", name.toString());
                var resultTag = DataComponentUtil.wrapEncodingExceptions(stack, NON_LIMIT_ITEM_CODEC, provider,
                        itemTag);
                nbtTagList.add(resultTag);
            }
        }

        CompoundTag nbt = new CompoundTag();
        nbt.put("Records", nbtTagList);
        return nbt;
    }

    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {

        ListTag tagList = nbt.getList("Records", Tag.TAG_COMPOUND);

        for (int i = 0; i < tagList.size(); ++i) {
            CompoundTag itemTags = tagList.getCompound(i);
            String name = itemTags.getString("Name");

            int slot = ModRecords.RECORD_REGISTRY.getId(ResourceLocation.parse(name));

            if (slot >= 0 && slot < this.entries.size()) {

                NON_LIMIT_ITEM_CODEC.parse(provider.createSerializationContext(NbtOps.INSTANCE), itemTags)
                        .resultOrPartial((p_330102_) -> LOGGER.error("Tried to load invalid item: '{}'", p_330102_))
                        .ifPresent((stack) -> {
                            this.entries.set(slot, stack);
                        });
            }
        }
    }
}
