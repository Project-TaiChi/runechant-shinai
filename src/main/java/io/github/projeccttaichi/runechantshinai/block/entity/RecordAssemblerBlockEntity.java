package io.github.projeccttaichi.runechantshinai.block.entity;

import io.github.projeccttaichi.runechantshinai.capability.RecordStorageHandler;
import io.github.projeccttaichi.runechantshinai.init.ModBlockEntities;
import io.github.projeccttaichi.runechantshinai.menu.RecordAssemblerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public class RecordAssemblerBlockEntity extends BlockEntity implements MenuProvider {
    private final ItemStackHandler recordGird;
    private final NonNullList<ItemStack> recordStacks;
    private final ItemStackHandler coreInventory;

    public RecordAssemblerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.RECORD_ASSEMBLER.get(), pos, state);
        recordStacks = NonNullList.create();
        recordGird = new ItemStackHandler(recordStacks) {
            @Override
            protected void onContentsChanged(int slot) {
                RecordAssemblerBlockEntity.this.setChanged();
            }
        };

        coreInventory = new ItemStackHandler(3) {
            @Override
            protected void onContentsChanged(int slot) {
                RecordAssemblerBlockEntity.this.setChanged();
                if (slot == 0) {
                    onCoreChanged();
                }
            }

            @Override
            protected void onLoad() {
                onCoreChanged();
            }
        };
    }


    private void onCoreChanged() {
        // TODO: save stacks to wands

    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        if (tag.contains("Items")) {
            CompoundTag inventoryTag = tag.getCompound("Items");
            coreInventory.deserializeNBT(provider, inventoryTag);
        }
        if (tag.contains("RecordGird")) {
            CompoundTag inventoryTag = tag.getCompound("RecordGird");
            recordGird.deserializeNBT(provider, inventoryTag);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        CompoundTag inventoryTag = coreInventory.serializeNBT(provider);
        tag.put("Items", inventoryTag);
        inventoryTag = recordGird.serializeNBT(provider);
        tag.put("RecordGird", inventoryTag);
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


    public IItemHandlerModifiable getCoreInventory() {
        return coreInventory;
    }

    public ItemStackHandler getRecordGird() {
        return recordGird;
    }

    public RecordStorageHandler getRecordStorage() {
        if (level == null) return RecordStorageHandler.EMPTY;

        for (Direction dir : Direction.values()) {
            BlockEntity be = level.getBlockEntity(worldPosition.relative(dir));
            if (be instanceof RecordChestBlockEntity recordChest) {
                return recordChest.getInventory();
            }
        }

        return RecordStorageHandler.EMPTY;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.runechantshinai.record_chest");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new RecordAssemblerMenu(containerId, playerInventory, player, ContainerLevelAccess.create(level, worldPosition), this.getRecordStorage());
    }

    public void clearContent() {
        for (int i = 0; i < recordStacks.size(); i++) {
            recordStacks.set(i, ItemStack.EMPTY);
        }

        for (int i = 0; i < coreInventory.getSlots(); i++) {
            coreInventory.setStackInSlot(i, ItemStack.EMPTY);
        }
    }

    public void onPlayerRemoved() {
        // TODO: finish wand assembler and drop contents

        for (int i = 0; i < recordStacks.size(); i++) {
            Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), recordStacks.get(i));
        }

        this.clearContent();
    }
}
