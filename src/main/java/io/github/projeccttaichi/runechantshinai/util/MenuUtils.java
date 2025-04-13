package io.github.projeccttaichi.runechantshinai.util;

import io.github.projeccttaichi.runechantshinai.network.c2s.CustomSlotAction;
import net.minecraft.world.item.ItemStack;

public class MenuUtils {

    public static int takeItemCountByOpt(ItemStack slotStack, CustomSlotAction.OptType optType) {
        if (slotStack.isEmpty()) {
            return 0;
        }
        int count = Math.min(slotStack.getCount(), slotStack.getMaxStackSize());

        return switch (optType) {
            case LEFT_CLICK -> count;
            case RIGHT_CLICK -> count / 2;
            default -> 0;
        };

    }



    public static int takeItemCountByOptPreferringSingle(ItemStack slotStack, CustomSlotAction.OptType optType) {
        if (slotStack.isEmpty()) {
            return 0;
        }
        int count = Math.min(slotStack.getCount(), slotStack.getMaxStackSize());

        return switch (optType) {
            case LEFT_CLICK -> 1;
            case RIGHT_CLICK -> count;
            default -> 0;
        };

    }

    public static int insertItemCountByOpt(ItemStack carryStack, CustomSlotAction.OptType optType) {
        if (carryStack.isEmpty()) {
            return 0;
        }

        return switch (optType) {
            case LEFT_CLICK -> carryStack.getCount();
            case RIGHT_CLICK -> 1;
            default -> 0;
        };
    }
}
