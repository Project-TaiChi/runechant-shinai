package io.github.projeccttaichi.runechantshinai.menu;

import io.github.projeccttaichi.runechantshinai.capability.RecordStorageHandler;
import io.github.projeccttaichi.runechantshinai.init.ModBlocks;
import io.github.projeccttaichi.runechantshinai.init.ModMenuTypes;
import io.github.projeccttaichi.runechantshinai.network.c2s.CustomSlotAction;
import io.github.projeccttaichi.runechantshinai.network.s2c.BatchSyncCustomSlots;
import io.github.projeccttaichi.runechantshinai.network.s2c.SyncCustomSlots;
import io.github.projeccttaichi.runechantshinai.util.MenuUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RecordChestMenu extends AbstractContainerMenu implements CustomSlotHandler {
    private final ContainerLevelAccess access;
    private final Player player;
    private final RecordStorageHandler recordStorage;
    private final HashMap<Integer, ItemStack> clientRecords;

    private RecordListModel model = null;

    public RecordChestMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, null, new RecordStorageHandler(), ContainerLevelAccess.NULL);

        this.model = new RecordListModel(recordStorage);
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

    public RecordListModel getModel() {
        return this.model;
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
        // The quick moved slot stack
        ItemStack quickMovedStack = ItemStack.EMPTY;
        // The quick moved slot
        Slot quickMovedSlot = this.slots.get(index);


        // If the slot is in the valid range and the slot is not empty
        if (quickMovedSlot != null && quickMovedSlot.hasItem()) {
            // Get the raw stack to move
            ItemStack rawStack = quickMovedSlot.getItem();
            // Set the slot stack to a copy of the raw stack
            quickMovedStack = rawStack.copy();

            // Try move to storage
            rawStack = this.recordStorage.insetAuto(rawStack, false);
            if (!rawStack.isEmpty()) {
                // Try to move the inventory/hotbar slot into the data inventory input slots
                if (!this.moveItemStackTo(rawStack, 0, 0, false)) {
                    // If cannot move and in player inventory slot, try to move to hotbar
                    if (index < 27) {
                        if (!this.moveItemStackTo(rawStack, 27, 27 + 9, false)) {
                            // If cannot move, no longer quick move
                            return ItemStack.EMPTY;
                        }
                    }
                    // Else try to move hotbar into player inventory slot
                    else if (!this.moveItemStackTo(rawStack, 0, 27, false)) {
                        // If cannot move, no longer quick move
                        return ItemStack.EMPTY;
                    }
                }
            }


            if (rawStack.isEmpty()) {
                // If the raw stack has completely moved out of the slot, set the slot to the empty stack
                quickMovedSlot.set(ItemStack.EMPTY);
            } else {
                // Otherwise, notify the slot that that the stack count has changed
                quickMovedSlot.setChanged();
            }

            /*
                The following if statement and Slot#onTake call can be removed if the
                menu does not represent a container that can transform stacks (e.g.
                chests).
            */
            if (rawStack.getCount() == quickMovedStack.getCount()) {
                // If the raw stack was not able to be moved to another slot, no longer quick move
                return ItemStack.EMPTY;
            }
            // Execute logic on what to do post move with the remaining stack
            quickMovedSlot.onTake(player, rawStack);
        }
        return quickMovedStack; // Return the slot stack
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.access.execute((world, pos) -> {
            this.clearContainer(player, new SimpleContainer(recordStorage.getSlots()));
        });
    }


    public void handleSyncCustomSlot(int slotType, int slotId, ItemStack itemStack) {
        if (slotType == 0) {
            this.recordStorage.setStackInSlot(slotId, itemStack);
        }
    }


    private ItemStack takeCustomSlotItem(int slotType, int slotId, CustomSlotAction.OptType optType) {
        if (slotType != 0) {
            return ItemStack.EMPTY;
        }
        ItemStack slotStack = this.recordStorage.getStackInSlot(slotId);
        int takeCount = MenuUtils.takeItemCountByOpt(slotStack, optType);
        return this.recordStorage.extractItem(slotId, takeCount, false);
    }

    private ItemStack insertCustomSlotItem(int slotType, int slotId, ItemStack stack, CustomSlotAction.OptType optType) {
        if (slotType != 0) {
            return stack;
        }
        switch (optType) {
            case LEFT_CLICK -> {
                return this.recordStorage.insetAuto(stack, false);
            }
            case RIGHT_CLICK -> {

                ItemStack toInsert = stack.copyWithCount(1);
                ItemStack rest = this.recordStorage.insetAuto(toInsert, false);
                if (rest.isEmpty()) {
                    stack.shrink(1);
                }
                return stack;

            }
            default -> {
                return stack;
            }
        }
    }


    @Override
    public void handleCustomSlotAction(IPayloadContext ctx, CustomSlotAction action) {
        ItemStack carried = this.getCarried();

        if (action.slotGroup() != 0) {
            return;
        }


        ItemStack slotItem = this.recordStorage.getStackInSlot(action.slotId());


        switch (action.action()) {
            case PICK_OR_REPLACE -> {

                if (carried.isEmpty()) {
                    if (slotItem.isEmpty()) {
                        return;
                    }
                    ItemStack taken = this.takeCustomSlotItem(action.slotGroup(), action.slotId(), action.optType());
                    this.setCarried(taken);
                } else {
                    ItemStack taken = this.insertCustomSlotItem(action.slotGroup(), action.slotId(), carried, action.optType());
                    this.setCarried(taken);
                }
            }

            case QUICK_MOVE -> {
                if (slotItem.isEmpty()) {
                    return;
                }

                if (this.moveItemStackTo(slotItem, 0, 36, false)) {
                    this.recordStorage.setStackInSlot(action.slotId(), ItemStack.EMPTY);
                }
            }

            case CLEAR -> {
                // Ignore
            }
        }
    }

    @Override
    public void onUpdated() {
        if (this.model != null) {
            this.model.update();
        }

    }
}