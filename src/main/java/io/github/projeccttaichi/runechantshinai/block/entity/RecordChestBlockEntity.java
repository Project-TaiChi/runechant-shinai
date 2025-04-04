package io.github.projeccttaichi.runechantshinai.block.entity;

import io.github.projeccttaichi.runechantshinai.capability.RecordStorageHandler;
import io.github.projeccttaichi.runechantshinai.init.ModBlockEntities;
import io.github.projeccttaichi.runechantshinai.init.ModCapabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;

public class RecordChestBlockEntity extends BlockEntity {
    private final RecordStorageHandler inventory;

    public RecordChestBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.RECORD_CHEST.get(), pos, state);
        this.inventory = new RecordStorageHandler() {
            @Override
            public void onContentsChanged(int slot) {
                RecordChestBlockEntity.this.setChanged();
            }
        };
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        if (tag.contains("Items")) {
            CompoundTag inventoryTag = tag.getCompound("Items");
            inventory.deserializeNBT(provider, inventoryTag);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        CompoundTag inventoryTag = inventory.serializeNBT(provider);
        tag.put("Items", inventoryTag);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, provider);
        return tag;
    }

    public RecordStorageHandler getInventory() {
        return inventory;
    }

    public ItemStack getItem(int slot) {
        return inventory.getStackInSlot(slot);
    }

    public void setItem(int slot, ItemStack stack) {
        inventory.setStackInSlot(slot, stack);
    }

    public int getSize() {
        return inventory.getSlots();
    }

    public void clearContent() {
        for (int i = 0; i < inventory.getSlots(); i++) {
            inventory.setStackInSlot(i, ItemStack.EMPTY);
        }
    }
}
