package io.github.projeccttaichi.runechantshinai.menu;

import com.google.common.base.Strings;
import io.github.projeccttaichi.runechantshinai.capability.RecordStorageHandler;
import io.github.projeccttaichi.runechantshinai.compoment.RecordComponent;
import io.github.projeccttaichi.runechantshinai.init.ModComponents;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class RecordListModel {
    private final RecordStorageHandler recordStorage;
    private int recordCount = 0;
    private final int[] recordMapping;

    public RecordListModel(RecordStorageHandler recordStorage) {
        this.recordStorage = recordStorage;
        this.recordMapping = new int[this.recordStorage.getSlots()];
    }

    private String nameFilter;

    public static boolean applySearchFilter(@NotNull String name, @NotNull String nameFilter) {
        int nameIndex = 0, filterIndex = 0;

        while (nameIndex < name.length() && filterIndex < nameFilter.length()) {
            if (Character.toLowerCase(name.charAt(nameIndex)) == Character.toLowerCase(nameFilter.charAt(filterIndex))) {
                filterIndex++;
            }
            nameIndex++;
        }

        return filterIndex == nameFilter.length();

    }


    public void update() {
        int recordCount = 0;
        for (int i = 0; i < this.recordStorage.getSlots(); i++) {
            ItemStack stack = this.recordStorage.getStackInSlot(i);
            if (stack.isEmpty()) {
                continue;
            }

            RecordComponent component = stack.get(ModComponents.RECORD_COMPONENT);
            if (component == null) {
                continue;
            }

            if (!Strings.isNullOrEmpty(nameFilter)) {
                String displayName = component.displayName();
                if (!applySearchFilter(displayName, nameFilter)) {
                    continue;
                }
            }

            if (!this.recordStorage.getStackInSlot(i).isEmpty()) {
                this.recordMapping[recordCount] = i;
                recordCount++;
            }
        }

        this.recordCount = recordCount;
    }

    public int count() {
        return recordCount;
    }

    public int containerIndex(int recordIndex) {
        if (recordIndex < 0 || recordIndex >= recordCount) {
            return -1;
        }
        return recordMapping[recordIndex];
    }


    public ItemStack getStack(int recordIndex) {
        int containerIndex = containerIndex(recordIndex);
        if (containerIndex < 0) {
            return ItemStack.EMPTY;
        }
        return recordStorage.getStackInSlot(containerIndex);
    }


    public boolean valid() {
        return recordStorage != null && recordStorage.isValid();
    }
}
