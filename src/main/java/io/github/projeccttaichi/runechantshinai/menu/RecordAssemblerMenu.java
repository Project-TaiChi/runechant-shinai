package io.github.projeccttaichi.runechantshinai.menu;

import io.github.projeccttaichi.runechantshinai.capability.RecordStorageHandler;
import io.github.projeccttaichi.runechantshinai.init.ModBlocks;
import io.github.projeccttaichi.runechantshinai.init.ModMenuTypes;
import io.github.projeccttaichi.runechantshinai.magic.core.RecordSequence;
import io.github.projeccttaichi.runechantshinai.network.c2s.CustomSlotAction;
import io.github.projeccttaichi.runechantshinai.network.s2c.BatchSyncCustomSlots;
import io.github.projeccttaichi.runechantshinai.network.s2c.SyncCustomSlots;
import io.github.projeccttaichi.runechantshinai.util.HexGrids;
import io.github.projeccttaichi.runechantshinai.util.MenuUtils;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
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
import java.util.HashSet;
import java.util.List;

public class RecordAssemblerMenu extends AbstractContainerMenu implements CustomSlotHandler {


    private final ContainerLevelAccess access;
    private final Container container;
    private final Player player;
    private RecordStorageHandler recordStorage;
    private final NonNullList<ItemStack> clientRecordStorage;

    private final RecordSequence recordSequence;
    private final HashMap<HexGrids.Axial, ItemStack> clientRecordSequence = new HashMap<>();
    private final HashSet<HexGrids.Axial> toRemoveRecords = new HashSet<>();

    //    private final >
    public int hexSize = 3;

    public RecordSequence getRecordSequence() {
        return recordSequence;
    }


    private RecordListModel model = null;

    public RecordListModel getModel() {
        return model;
    }

    public RecordAssemblerMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, null, ContainerLevelAccess.NULL, new RecordStorageHandler());

        this.model = new RecordListModel(this.recordStorage);
    }

    public RecordAssemblerMenu(int containerId, Inventory playerInventory, Player player, ContainerLevelAccess access, RecordStorageHandler recordStorage) {
        super(ModMenuTypes.RECORD_ASSEMBLER.get(), containerId);
        this.access = access;
        this.player = player;
        this.recordStorage = recordStorage;
        this.clientRecordStorage = NonNullList.withSize(this.recordStorage.getSlots(), ItemStack.EMPTY);

        this.recordSequence = new RecordSequence() {
            @Override
            public void onRemove(HexGrids.Axial axial) {
                RecordAssemblerMenu.this.toRemoveRecords.add(axial);
            }
        };

        this.container = new SimpleContainer(3) {
            @Override
            public void setChanged() {
                super.setChanged();
                RecordAssemblerMenu.this.slotsChanged(this);
            }
        };

        this.addWandSlots();
        this.addUpgradeSlots();
        this.addStandardInventorySlots(playerInventory, 104, 216);


        this.initWandRecords(10);
    }

    private void addStandardInventorySlots(Inventory playerInventory, int x, int y) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, x + col * 18, y + row * 18));
            }
        }

        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, x + col * 18, y + 58));
        }
    }

    private void initWandRecords(int level) {

    }

    @Override
    public void sendAllDataToRemote() {
        super.sendAllDataToRemote();

//        for (HexGrids.Axial position : this.wandRecords.keySet()) {
//            HexSlot hexSlot = this.wandRecords.get(position);
//            this.sendHexSlotData(position, hexSlot.itemStack);
//        }

        // send network packet to client
        List<BatchSyncCustomSlots.Entry> storageSlots = new ArrayList<>();
        this.clientRecordStorage.clear();
        for (int i = 0; i < this.recordStorage.getSlots(); i++) {
            ItemStack stack = this.recordStorage.getStackInSlot(i);
            if (!stack.isEmpty()) {
                this.clientRecordStorage.set(i, stack.copy());
                storageSlots.add(new BatchSyncCustomSlots.Entry(i, stack));
            }
        }

        List<BatchSyncCustomSlots.Entry> hexSlots = new ArrayList<>();
        this.clientRecordSequence.clear();
        for (HexGrids.Axial position : this.recordSequence.listRecords()) {
            ItemStack stack = this.recordSequence.getRecord(position);
            this.clientRecordSequence.put(position, stack.copy());
            hexSlots.add(new BatchSyncCustomSlots.Entry(position.packed(), stack));
        }

        HashMap<Integer, List<BatchSyncCustomSlots.Entry>> data = new HashMap<>();
        data.put(SLOT_GROUP_STORAGE, storageSlots);
        data.put(SLOT_GROUP_HEX, hexSlots);
        BatchSyncCustomSlots packet = new BatchSyncCustomSlots(this.containerId, data, true);

        PacketDistributor.sendToPlayer((ServerPlayer) this.player, packet);
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();

        if (!this.suppressRemoteUpdates) {
            for (HexGrids.Axial position : this.recordSequence.listRecords()) {
                ItemStack stack = this.recordSequence.getRecord(position);
                ItemStack clientStack = this.clientRecordSequence.getOrDefault(position, ItemStack.EMPTY);
                if (!ItemStack.matches(stack, clientStack)) {
                    this.sendSlot2Client(SLOT_GROUP_HEX, position.packed(), stack);
                }
            }

            for (HexGrids.Axial position : this.toRemoveRecords) {
                ItemStack stack = this.recordSequence.getRecord(position);
                if (stack.isEmpty()) {
                    this.clientRecordSequence.remove(position);
                    this.sendSlot2Client(SLOT_GROUP_HEX, position.packed(), ItemStack.EMPTY);
                }
            }
            this.toRemoveRecords.clear();

            for (int i = 0; i < this.recordStorage.getSlots(); i++) {
                ItemStack stack = this.recordStorage.getStackInSlot(i);
                ItemStack clientStack = this.clientRecordStorage.get(i);
                if (!ItemStack.matches(stack, clientStack)) {
                    this.sendSlot2Client(SLOT_GROUP_STORAGE, i, stack);
                }
            }
        }

    }

    private void sendSlot2Client(int slotGroup, int slotId, ItemStack stack) {
        if (this.player == null) {
            return;
        }
        switch (slotGroup) {
            case SLOT_GROUP_HEX -> {
                SyncCustomSlots packet = new SyncCustomSlots(this.containerId, SLOT_GROUP_HEX, slotId, stack);
                PacketDistributor.sendToPlayer((ServerPlayer) this.player, packet);
            }
            case SLOT_GROUP_STORAGE -> {
                if (stack.isEmpty()) {
                    this.clientRecordStorage.remove(slotId);
                } else {
                    this.clientRecordStorage.set(slotId, stack.copy());
                }

                SyncCustomSlots packet = new SyncCustomSlots(this.containerId, SLOT_GROUP_STORAGE, slotId, stack);
                PacketDistributor.sendToPlayer((ServerPlayer) this.player, packet);
            }
        }
    }


    private void addWandSlots() {
        this.addSlot(new Slot(this.container, 0, 66, 16));
        this.addSlot(new Slot(this.container, 1, 19, 16));
        this.addSlot(new Slot(this.container, 2, 39, 16));
    }

    private void addUpgradeSlots() {
//        for (int i = 0; i < 3; i++) {
//            this.addSlot(new Slot(this.container, i + 3, 188, 115 + (i * 18)));
//        }
//
//        this.addSlot(new Slot(this.container, 6, 188, 173));
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.access.execute((world, blockPos) -> {
            this.clearContainer(player, this.container);

            for (HexGrids.Axial hexPos : this.recordSequence.listRecords()) {
                ItemStack stack = this.recordSequence.getRecord(hexPos);

                if (player.isAlive() && !((ServerPlayer) player).hasDisconnected()) {
                    player.getInventory().placeItemBackInInventory(stack);
                } else {
                    player.drop(stack, false);
                }
            }

            this.recordSequence.clear();
        });
    }

    final int PLAYER_INVENTORY_START = 3;
    final int PLAYER_HOTBAR_START = PLAYER_INVENTORY_START + 27;
    final int PLAYER_HOTBAR_END = PLAYER_HOTBAR_START + 9;

    @Override
    public ItemStack quickMoveStack(Player player, int quickMovedSlotIndex) {
        // The quick moved slot stack
        ItemStack quickMovedStack = ItemStack.EMPTY;
        // The quick moved slot
        Slot quickMovedSlot = this.slots.get(quickMovedSlotIndex);


        // If the slot is in the valid range and the slot is not empty
        if (quickMovedSlot != null && quickMovedSlot.hasItem()) {
            // Get the raw stack to move
            ItemStack rawStack = quickMovedSlot.getItem();
            // Set the slot stack to a copy of the raw stack
            quickMovedStack = rawStack.copy();

            // if the quick move was performed on the player inventory or hotbar slot
            if (quickMovedSlotIndex >= PLAYER_INVENTORY_START && quickMovedSlotIndex < PLAYER_HOTBAR_END) {
                // Try move to storage
                rawStack = this.recordStorage.insetAuto(rawStack, false);
                if (!rawStack.isEmpty()) {
                    // Try to move the inventory/hotbar slot into the data inventory input slots
                    if (!this.moveItemStackTo(rawStack, 0, PLAYER_INVENTORY_START, false)) {
                        // If cannot move and in player inventory slot, try to move to hotbar
                        if (quickMovedSlotIndex < PLAYER_HOTBAR_START) {
                            if (!this.moveItemStackTo(rawStack, PLAYER_HOTBAR_START, PLAYER_HOTBAR_END, false)) {
                                // If cannot move, no longer quick move
                                return ItemStack.EMPTY;
                            }
                        }
                        // Else try to move hotbar into player inventory slot
                        else if (!this.moveItemStackTo(rawStack, PLAYER_INVENTORY_START, PLAYER_HOTBAR_START, false)) {
                            // If cannot move, no longer quick move
                            return ItemStack.EMPTY;
                        }
                    }
                }

            }
            // Else if the quick move was performed on the data inventory input slots, try to move to player inventory/hotbar
            else if (!this.moveItemStackTo(rawStack, PLAYER_INVENTORY_START, PLAYER_HOTBAR_END, false)) {
                // If cannot move, no longer quick move
                return ItemStack.EMPTY;
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
    public boolean stillValid(Player player) {
        return stillValid(this.access, player, ModBlocks.RECORD_ASSEMBLER.get());
    }


    private ItemStack takeCustomSlotItem(int slotType, int slotId, CustomSlotAction.OptType optType) {
        switch (slotType) {
            case SLOT_GROUP_HEX -> {
                HexGrids.Axial position = HexGrids.Axial.unpacked(slotId);
                ItemStack slotStack = this.recordSequence.getRecord(position);
                this.recordSequence.removeRecord(position);
                return slotStack.copyWithCount(1);
            }
            case SLOT_GROUP_STORAGE -> {
                ItemStack slotStack = this.recordStorage.getStackInSlot(slotId);
                int takeCount = MenuUtils.takeItemCountByOptPreferringSingle(slotStack, optType);
                return this.recordStorage.extractItem(slotId, takeCount, false);
            }

            default -> {
                return ItemStack.EMPTY;
            }
        }
    }

    private ItemStack insertCustomSlotItem(int slotType, int slotId, ItemStack stack, CustomSlotAction.OptType optType) {
        switch (slotType) {
            case SLOT_GROUP_HEX -> {
                HexGrids.Axial position = HexGrids.Axial.unpacked(slotId);

                if (!this.recordSequence.isEnabled(position)) {
                    return stack;
                }

                ItemStack slotItem = this.recordSequence.getRecord(position);
                if (!slotItem.isEmpty()) {
                    if (stack.getCount() != 1) {
                        // could not insert all
                        return stack;
                    }
                    if (this.recordSequence.putRecord(position, stack.copy())) {
                        stack = slotItem;
                    }
                } else {
                    ItemStack toInsert = stack.copyWithCount(1);
                    if (this.recordSequence.putRecord(position, toInsert)) {
                        stack.shrink(1);
                    }
                }

                return stack;
            }
            case SLOT_GROUP_STORAGE -> {
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
        }
        return ItemStack.EMPTY;
    }


    public static final int SLOT_GROUP_HEX = 0;
    public static final int SLOT_GROUP_STORAGE = 1;

    @Override
    public void handleCustomSlotAction(IPayloadContext ctx, CustomSlotAction hexSlotAction) {
        ItemStack carried = this.getCarried();


        ItemStack slotItem = ItemStack.EMPTY;

        switch (hexSlotAction.slotGroup()) {
            case SLOT_GROUP_HEX -> {
                slotItem = this.recordSequence.getRecord(HexGrids.Axial.unpacked(hexSlotAction.slotId()));
            }
            case SLOT_GROUP_STORAGE -> {
                slotItem = this.recordStorage.getStackInSlot(hexSlotAction.slotId());
            }
        }


        switch (hexSlotAction.action()) {
            case PICK_OR_REPLACE -> {
                if (carried.isEmpty()) {
                    if (slotItem.isEmpty()) {
                        return;
                    }
                    ItemStack taken = this.takeCustomSlotItem(hexSlotAction.slotGroup(), hexSlotAction.slotId(), hexSlotAction.optType());
                    this.setCarried(taken);
                } else {
                    ItemStack taken = this.insertCustomSlotItem(hexSlotAction.slotGroup(), hexSlotAction.slotId(), carried, hexSlotAction.optType());
                    this.setCarried(taken);
                }
            }

            case QUICK_MOVE -> {
                if (slotItem.isEmpty()) {
                    return;
                }

                switch (hexSlotAction.slotGroup()) {
                    case SLOT_GROUP_HEX -> {
                        // first trying to move to storage
                        ItemStack rest = this.recordStorage.insetAuto(slotItem, false);
                        if (!rest.isEmpty()) {
                            // if not all items could be moved to storage, try to move to player inventory
                            this.moveItemStackTo(slotItem, PLAYER_INVENTORY_START, PLAYER_HOTBAR_END, false);
                        }

                        if (rest.isEmpty()) {
                            this.recordSequence.removeRecord(HexGrids.Axial.unpacked(hexSlotAction.slotId()));
                        }
                    }
                    case SLOT_GROUP_STORAGE -> {
                        // move to player inventory
                        if (this.moveItemStackTo(slotItem, PLAYER_INVENTORY_START, PLAYER_HOTBAR_END, false)) {
                            this.recordStorage.setStackInSlot(hexSlotAction.slotId(), slotItem);
                        }
                    }
                }
            }

            case CLEAR -> {
                if (hexSlotAction.slotGroup() != SLOT_GROUP_HEX) {
                    // only clear hex slots
                    return;
                }

                for (HexGrids.Axial position : this.recordSequence.listRecords()) {
                    ItemStack item = this.recordSequence.removeRecord(position);
                    if (!item.isEmpty()) {
                        ItemStack rest = this.recordStorage.insetAuto(item, false);
                        if (!rest.isEmpty()) {
                            // if not all items could be moved to storage, try to move to player inventory
                            this.moveItemStackTo(slotItem, PLAYER_INVENTORY_START, PLAYER_HOTBAR_END, false);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onUpdated() {
        this.model.update();
    }


    @Override
    public void handleSyncCustomSlot(int slotType, int slotId, ItemStack itemStack) {
        switch (slotType) {
            case SLOT_GROUP_HEX -> {
                this.recordSequence.putRecord(HexGrids.Axial.unpacked(slotId), itemStack);
            }
            case SLOT_GROUP_STORAGE -> {
                this.recordStorage.setStackInSlot(slotId, itemStack);
            }
        }
    }


}
