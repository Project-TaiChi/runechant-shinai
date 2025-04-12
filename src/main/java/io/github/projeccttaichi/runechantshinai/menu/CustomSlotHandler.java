package io.github.projeccttaichi.runechantshinai.menu;

import io.github.projeccttaichi.runechantshinai.network.c2s.CustomSlotAction;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public interface CustomSlotHandler {
    void handleSyncCustomSlot(int slotType, int slotId, ItemStack itemStack);

    void handleCustomSlotAction(IPayloadContext ctx, CustomSlotAction hexSlotAction);

    void onUpdated();

}
