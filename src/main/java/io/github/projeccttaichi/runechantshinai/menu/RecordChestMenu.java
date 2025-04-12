package io.github.projeccttaichi.runechantshinai.menu;

import io.github.projeccttaichi.runechantshinai.capability.RecordStorageHandler;
import io.github.projeccttaichi.runechantshinai.init.ModBlocks;
import io.github.projeccttaichi.runechantshinai.init.ModMenuTypes;
import io.github.projeccttaichi.runechantshinai.network.c2s.CustomSlotAction;
import io.github.projeccttaichi.runechantshinai.network.s2c.BatchSyncCustomSlots;
import io.github.projeccttaichi.runechantshinai.network.s2c.SyncCustomSlots;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecordChestMenu extends AbstractContainerMenu implements CustomSlotHandler {
    private final ContainerLevelAccess access;
    private final Player player;
    private final RecordStorageHandler recordStorage;
    private final HashMap<Integer, ItemStack> clientRecords;

    public RecordChestMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, null, new RecordStorageHandler(), ContainerLevelAccess.NULL);
    }

    public RecordChestMenu(int containerId, Inventory playerInventory, Player player, RecordStorageHandler handler,
            ContainerLevelAccess access) {
        super(ModMenuTypes.RECORD_CHEST.get(), containerId);
        this.access = access;
        this.player = player;
        this.clientRecords = new HashMap<>();

        this.recordStorage = handler;

        this.addPlayerInventory(playerInventory);
    }

    private void addPlayerInventory(Inventory playerInventory) {
        // Player inventory
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 140 + row * 18));
            }
        }

        // Player hotbar
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 198));
        }
    }

    public RecordStorageHandler getRecordStorage() {
        return recordStorage;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.access, player, ModBlocks.RECORD_CHEST.get());
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        if (!this.suppressRemoteUpdates) {

            for (int i = 0; i < this.recordStorage.getSlots(); i++) {
                ItemStack stack = this.recordStorage.getStackInSlot(i);
                ItemStack clientStack = this.clientRecords.getOrDefault(i, ItemStack.EMPTY);
                if (!ItemStack.matches(stack, clientStack)) {
                    this.clientRecords.put(i, stack.copy());
                    this.sendRecordSlot2Client(i, stack);
                }
            }
        }
    }

    @Override
    public void sendAllDataToRemote() {
        super.sendAllDataToRemote();

        this.clientRecords.clear();
        // send network packet to client
        List<BatchSyncCustomSlots.Entry> slots = new ArrayList<>();
        for (int i = 0; i < this.recordStorage.getSlots(); i++) {
            ItemStack stack = this.recordStorage.getStackInSlot(i);
            if (!stack.isEmpty()) {
                this.clientRecords.put(i, stack.copy());
                slots.add(new BatchSyncCustomSlots.Entry(i, stack));
            }
        }
        HashMap<Integer, List<BatchSyncCustomSlots.Entry>> data = new HashMap<>();
        data.put(0, slots);
        BatchSyncCustomSlots packet = new BatchSyncCustomSlots(this.containerId, data, true);

        PacketDistributor.sendToPlayer((ServerPlayer) this.player, packet);

    }

    private void sendRecordSlot2Client(int slot, ItemStack stack) {
        this.clientRecords.put(slot, stack.copy());

        SyncCustomSlots packet = new SyncCustomSlots(this.containerId, 0, slot, stack);
        PacketDistributor.sendToPlayer((ServerPlayer) this.player, packet);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            itemstack = slotStack.copy();

            if (index >= 36) {
                // From record storage to player inventory
                if (!this.moveItemStackTo(slotStack, 0, 36, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // From player inventory to record storage
                if (recordStorage.isItemValid(0, slotStack)) {
                    ItemStack remainder = ItemHandlerHelper.insertItemStacked(recordStorage, slotStack, false);
                    if (!remainder.isEmpty()) {
                        return ItemStack.EMPTY;
                    }
                    slot.set(remainder);
                }
            }

            if (slotStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.access.execute((world, pos) -> {
            this.clearContainer(player, new SimpleContainer(recordStorage.getSlots()));
        });
    }


    public void handleSyncCustomSlot(int slotType, int slotId, ItemStack itemStack) {
        if(slotType == 0) {
            this.recordStorage.setStackInSlot(slotId, itemStack);
        }
    }

    @Override
    public void handleCustomSlotAction(IPayloadContext ctx, CustomSlotAction hexSlotAction) {
        
    }

    @Override
    public void onUpdated() {
        this.updateRecordCount();

    }

    private int recordCount = 0;
    private int[] recordMapping = new int[0];

    public void updateRecordCount() {
        this.recordCount = 0;
        if(this.recordMapping.length != this.recordStorage.getSlots()) {
            this.recordMapping = new int[this.recordStorage.getSlots()];
        }
        for(int i = 0; i < this.recordStorage.getSlots(); i++) {
            if(!this.recordStorage.getStackInSlot(i).isEmpty()) {
                this.recordMapping[this.recordCount] = i;
                this.recordCount++;
            }
        }
    }



    public int getRecordCount() {
        return this.recordCount;
    }

    public ItemStack getRecordStack(int slotIndex) {
        if(slotIndex >= 0 && slotIndex < this.recordCount) {
            return this.recordStorage.getStackInSlot(this.recordMapping[slotIndex]);
        } else {
            return ItemStack.EMPTY;
        }
    }
}