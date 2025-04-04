package io.github.projeccttaichi.runechantshinai.menu;

import io.github.projeccttaichi.runechantshinai.init.ModBlocks;
import io.github.projeccttaichi.runechantshinai.init.ModMenuTypes;
import io.github.projeccttaichi.runechantshinai.network.c2s.CustomSlotAction;
import io.github.projeccttaichi.runechantshinai.network.s2c.SyncCustomSlots;
import io.github.projeccttaichi.runechantshinai.util.HexGrids;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class RecordAssemblerMenu extends AbstractContainerMenu {

    static class HexSlot {
        HexGrids.Axial position;
        ItemStack itemStack;
        boolean dirty;
    }


    private final ContainerLevelAccess access;
    private final Container container;
    private final Player player;

    // TODO: change this to record instance rather than just the item
    private final HashMap<HexGrids.Axial, HexSlot> wandRecords = new HashMap<>();

    // TODO: change this from record chest
    private final IItemHandlerModifiable slotChest;
    //    private final >
    public int hexSize = 3;

    public Set<HexGrids.Axial> getSlotPositions() {
        return this.wandRecords.keySet();
    }

    public boolean validHexSlot(HexGrids.Axial position) {
        return this.wandRecords.containsKey(position);
    }

    public ItemStack getHexSlotItem(HexGrids.Axial position) {
        return this.wandRecords.get(position).itemStack;
    }

    public int getSlotChestSize() {
        return this.slotChest.getSlots();
//        return this.slots.size();
    }

    public ItemStack getSlotChestStack(int slot) {
        return this.slotChest.getStackInSlot(slot);
//        return this.slots.get(slot).getItem();
    }

    public RecordAssemblerMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, null, ContainerLevelAccess.NULL);
    }

    public RecordAssemblerMenu(int containerId, Inventory playerInventory, Player player, ContainerLevelAccess access) {
        super(ModMenuTypes.RECORD_ASSEMBLER.get(), containerId);
        this.access = access;
        this.player = player;

        this.container = new SimpleContainer(3) {
            @Override
            public void setChanged() {
                super.setChanged();
                RecordAssemblerMenu.this.slotsChanged(this);
            }
        };

        this.slotChest = new ItemStackHandler(9 * 8) {
            @Override
            protected void onContentsChanged(int slot) {
                RecordAssemblerMenu.this.broadcastChanges();
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

//        HexGrids.Axial[] wandRecordPositions = new HexGrids.Axial[]{
//                new HexGrids.Axial(0, 0),
//                new HexGrids.Axial(1, 0),
//                new HexGrids.Axial(0, 1)
//        };

        List<HexGrids.Axial> wandRecordPositions = HexGrids.axialRange(level);
        this.hexSize = level;

        for (HexGrids.Axial position : wandRecordPositions) {
            HexSlot hexSlot = new HexSlot();
            hexSlot.position = position;
            hexSlot.itemStack = ItemStack.EMPTY;
            hexSlot.dirty = true;
            this.wandRecords.put(position, hexSlot);
        }


    }

    @Override
    public void sendAllDataToRemote() {
        super.sendAllDataToRemote();

        for (HexGrids.Axial position : this.wandRecords.keySet()) {
            HexSlot hexSlot = this.wandRecords.get(position);
            this.sendHexSlotData(position, hexSlot.itemStack);
        }
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();

        for (HexGrids.Axial position : this.wandRecords.keySet()) {
            HexSlot hexSlot = this.wandRecords.get(position);
            if (hexSlot.dirty) {
                this.sendHexSlotData(position, hexSlot.itemStack);
                hexSlot.dirty = false;
            }
        }
    }

    private void sendHexSlotData(HexGrids.Axial position, ItemStack itemStack) {
        if (!(player instanceof ServerPlayer serverPlayer) || this.suppressRemoteUpdates) {
            return;
        }
//        PacketDistributor.sendToPlayer(serverPlayer, new SyncCustomSlots(this.containerId, position, itemStack));
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

            for (HexSlot hexSlot : this.wandRecords.values()) {
                if (!hexSlot.itemStack.isEmpty()) {
                    if (player.isAlive() && !((ServerPlayer) player).hasDisconnected()) {
                        player.getInventory().placeItemBackInInventory(hexSlot.itemStack);
                    } else {
                        player.drop(hexSlot.itemStack, false);
                    }
                    hexSlot.itemStack = ItemStack.EMPTY;
                }
            }
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
                rawStack = ItemHandlerHelper.insertItemStacked(this.slotChest, rawStack, false);
                if(!rawStack.isEmpty()) {
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

    public boolean setHexSlotItem(HexGrids.Axial position, ItemStack stack) {

        HexSlot hexSlot = this.wandRecords.get(position);
        if (hexSlot == null) {
            return false;
        }

        if (!this.wandRecords.containsKey(position)) {
            return false;
        }
        // TODO check if the item is valid
        hexSlot.itemStack = stack;
        hexSlot.dirty = true;

        return true;
    }


    private ItemStack takeCustomSlotItem(int slotType, int slotId, int takeCount) {
        ItemStack slotItem = ItemStack.EMPTY;
        takeCount = Math.min(takeCount, slotItem.getMaxStackSize());
        switch (slotType) {
            case SLOT_GROUP_HEX -> {
                slotItem = this.getHexSlotItem(HexGrids.Axial.unpacked(slotId));
                takeCount = Math.min(takeCount, 1);
            }
            case SLOT_GROUP_STORAGE -> {
                slotItem = this.getSlotChestStack(slotId);
                takeCount = Math.min(takeCount, slotItem.getCount());
            }
        }

        if (slotItem.isEmpty() || takeCount <= 0) {
            return ItemStack.EMPTY;
        }

        ItemStack copy = slotItem.copy();
        copy.setCount(takeCount);

        switch (slotType) {
            case SLOT_GROUP_HEX -> {
                this.setHexSlotItem(HexGrids.Axial.unpacked(slotId), ItemStack.EMPTY);
            }
            case SLOT_GROUP_STORAGE -> {
                slotItem.shrink(takeCount);
                this.slotChest.setStackInSlot(slotId, slotItem);
            }
        }
        return copy;
    }

    private ItemStack insertCustomSlotItem(int slotType, int slotId, ItemStack stack) {
        switch (slotType) {
            case SLOT_GROUP_HEX -> {
                ItemStack slotItem = this.getHexSlotItem(HexGrids.Axial.unpacked(slotId));
                if (!slotItem.isEmpty()) {
                    if (stack.getCount() != 1) {
                        // could not insert all
                        return stack;
                    }
                }
                ItemStack toInsert = stack.copy();
                toInsert.setCount(1);
                if (this.setHexSlotItem(HexGrids.Axial.unpacked(slotId), toInsert)) {
                    stack.shrink(1);
                }

                return stack;
            }
            case SLOT_GROUP_STORAGE -> {
                // all items could be inserted
                return ItemHandlerHelper.insertItemStacked(this.slotChest, stack, false);
            }
        }
        return ItemStack.EMPTY;
    }


    private static final int SLOT_GROUP_HEX = 0;
    private static final int SLOT_GROUP_STORAGE = 1;

    public void handleCustomSlotAction(IPayloadContext ctx, CustomSlotAction hexSlotAction) {
        ItemStack carried = this.getCarried();


        ItemStack slotItem = ItemStack.EMPTY;

        switch (hexSlotAction.slotGroup()) {
            case SLOT_GROUP_HEX -> {
                slotItem = this.getHexSlotItem(HexGrids.Axial.unpacked(hexSlotAction.slotId()));
            }
            case SLOT_GROUP_STORAGE -> {
                slotItem = this.getSlotChestStack(hexSlotAction.slotId());
            }
        }

        int takeCount = 0;
        if (!slotItem.isEmpty()) {
            switch (hexSlotAction.pickType()) {
                case SINGLE -> takeCount = 1;
                case HALF -> takeCount = slotItem.getCount() / 2;
                case ALL -> takeCount = slotItem.getCount();
            }
        }


        switch (hexSlotAction.action()) {
            case PICK_OR_REPLACE -> {
                if (carried.isEmpty()) {
                    if (slotItem.isEmpty()) {
                        return;
                    }
                    ItemStack taken = this.takeCustomSlotItem(hexSlotAction.slotGroup(), hexSlotAction.slotId(), takeCount);
                    this.setCarried(taken);
                } else {
                    ItemStack taken = this.insertCustomSlotItem(hexSlotAction.slotGroup(), hexSlotAction.slotId(), carried);
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
                        ItemStack rest = ItemHandlerHelper.insertItemStacked(this.slotChest, slotItem, false);
                        if (!rest.isEmpty()) {
                            // if not all items could be moved to storage, try to move to player inventory
                            this.moveItemStackTo(slotItem, PLAYER_INVENTORY_START, PLAYER_HOTBAR_END, false);
                        }
                        this.setHexSlotItem(HexGrids.Axial.unpacked(hexSlotAction.slotId()), rest);
                    }
                    case SLOT_GROUP_STORAGE -> {
                        // move to player inventory

                        if (this.moveItemStackTo(slotItem, PLAYER_INVENTORY_START, PLAYER_HOTBAR_END, false)) {
                            this.slotChest.setStackInSlot(hexSlotAction.slotId(), slotItem);
                        }
                    }
                }
            }

            case CLEAR -> {
                if(hexSlotAction.slotGroup() != SLOT_GROUP_HEX) {
                    // only clear hex slots
                    return;
                }
                for (HexGrids.Axial position : this.wandRecords.keySet()) {
                    ItemStack item = this.getHexSlotItem(position);
                    if (!item.isEmpty()) {
                        ItemStack rest = ItemHandlerHelper.insertItemStacked(this.slotChest, slotItem, false);
                        if (!rest.isEmpty()) {
                            // if not all items could be moved to storage, try to move to player inventory
                            this.moveItemStackTo(slotItem, PLAYER_INVENTORY_START, PLAYER_HOTBAR_END, false);
                        }
                        this.setHexSlotItem(HexGrids.Axial.unpacked(hexSlotAction.slotId()), rest);
                    }
                }
            }
        }
    }


    public void handleSyncCustomSlot(int slotType, int slotId, ItemStack itemStack) {
        switch(slotType) {
            case SLOT_GROUP_HEX -> {
                this.setHexSlotItem(HexGrids.Axial.unpacked(slotId), itemStack);
            }
            case SLOT_GROUP_STORAGE -> {
                this.slotChest.setStackInSlot(slotId, itemStack);
            }
        }
    }


}
