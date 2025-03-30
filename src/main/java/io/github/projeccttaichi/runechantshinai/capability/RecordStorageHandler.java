package io.github.projeccttaichi.runechantshinai.capability;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

public class RecordStorageHandler implements IItemHandlerModifiable {
    private final NonNullList<ItemStack> entries;

    public RecordStorageHandler() {
        this.entries = NonNullList.withSize(9, ItemStack.EMPTY);
    }

    @Override
    public void setStackInSlot(int i, ItemStack itemStack) {

    }

    @Override
    public int getSlots() {
        return 0;
    }

    @Override
    public ItemStack getStackInSlot(int i) {
        return null;
    }

    @Override
    public ItemStack insertItem(int i, ItemStack itemStack, boolean b) {
        return null;
    }

    @Override
    public ItemStack extractItem(int i, int i1, boolean b) {
        return null;
    }

    @Override
    public int getSlotLimit(int i) {
        return 0;
    }

    @Override
    public boolean isItemValid(int i, ItemStack itemStack) {
        return false;
    }
}
