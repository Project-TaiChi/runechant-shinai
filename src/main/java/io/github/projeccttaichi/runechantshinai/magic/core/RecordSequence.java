package io.github.projeccttaichi.runechantshinai.magic.core;

import io.github.projeccttaichi.runechantshinai.util.HexGrids;
import io.github.projeccttaichi.runechantshinai.util.RecordUtils;
import net.minecraft.world.item.ItemStack;

import java.util.*;
import java.util.function.Function;

public class RecordSequence {
    private record GridProp(int neighborCount, int strongNeighborCount, int group, boolean hasRecord) {

        boolean isEmpty() {
            return neighborCount == 0 && strongNeighborCount == 0 && !hasRecord;
        }
    }

    private static final GridProp EMPTY_PROP = new GridProp(0, 0, 0, false);

    private final Map<HexGrids.Axial, ItemStack> records;
    private final Map<HexGrids.Axial, GridProp> gridProps = new HashMap<>();
    private final Set<HexGrids.Axial> validPositions;

    public RecordSequence() {
        this.records = new HashMap<>();
        this.validPositions = new HashSet<>();
        this.updateGroup(new HexGrids.Axial(0, 0), increaseNeighbor(true));
        this.updateNeighbors(new HexGrids.Axial(0, 0), increaseNeighbor(false));

        this.refreshValidPositions();
    }

    private void refreshValidPositions() {
        this.validPositions.clear();
        this.validPositions.add(new HexGrids.Axial(0, 0));

        ArrayDeque<HexGrids.Axial> queue = new ArrayDeque<>();
        HashSet<HexGrids.Axial> visited = new HashSet<>();
        queue.add(new HexGrids.Axial(0, 0));
        while (!queue.isEmpty()) {
            HexGrids.Axial axial = queue.removeFirst();
            if (visited.contains(axial)) {
                continue;
            }
            visited.add(axial);
            if (this.records.containsKey(axial)) {
                HexGrids.neighbors(axial).forEach(it -> {
                    validPositions.add(it);
                    queue.add(it);
                });
            }
        }
    }

    public Set<HexGrids.Axial> listAvailable() {
        return this.gridProps.keySet();
    }

    public Set<HexGrids.Axial> listRecords() {
        return this.records.keySet();
    }

    public boolean hasRecord(HexGrids.Axial axial) {
        return this.records.containsKey(axial);
    }

    public ItemStack getRecord(HexGrids.Axial axial) {
        return this.records.getOrDefault(axial, ItemStack.EMPTY);
    }

    private GridProp getProp(HexGrids.Axial axial) {
        return gridProps.getOrDefault(axial, EMPTY_PROP);
    }

    public boolean isVisible(HexGrids.Axial axial) {
        if (hasRecord(axial)) {
            return true;
        }

        if (isValidPosition(axial) || HexGrids.neighbors(axial).anyMatch(this::isValidPosition)) {
            return getProp(axial).neighborCount > 0;
        }

        return getProp(axial).strongNeighborCount > 0;
    }

    public boolean isValidPosition(HexGrids.Axial axial) {
        return this.validPositions.contains(axial);
    }

    public boolean isEnabled(HexGrids.Axial axial) {
        if (hasRecord(axial)) {
            return true;
        }

        if (isValidPosition(axial)) {
            return getProp(axial).strongNeighborCount > 0;
        }

        return false;
    }

    public int getGroup(HexGrids.Axial axial) {
        return getProp(axial).group;
    }


    private void updateGroup(HexGrids.Axial axial, GridProp prop) {
        if (prop.isEmpty()) {
            this.gridProps.remove(axial);
        }
        this.gridProps.put(axial, prop);
    }

    private static Function<GridProp, GridProp> increaseNeighbor(boolean isStrong) {
        if (isStrong) {
            return (prop) -> new GridProp(prop.neighborCount + 1, prop.strongNeighborCount + 1, prop.group + 1, prop.hasRecord);
        }
        return (prop) -> new GridProp(prop.neighborCount + 1, prop.strongNeighborCount, prop.group, prop.hasRecord);
    }

    private static Function<GridProp, GridProp> decreaseNeighbor(boolean isStrong) {
        if (isStrong) {
            return (prop) -> new GridProp(prop.neighborCount - 1, prop.strongNeighborCount - 1, prop.group - 1, prop.hasRecord);
        }
        return (prop) -> new GridProp(prop.neighborCount - 1, prop.strongNeighborCount, prop.group, prop.hasRecord);
    }

    private static Function<GridProp, GridProp> changeStrongNeighbor(int delta) {
        return (prop) -> new GridProp(prop.neighborCount, prop.strongNeighborCount + delta, prop.group, prop.hasRecord);
    }

    private static Function<GridProp, GridProp> changeNeighbor(int delta) {
        return (prop) -> new GridProp(prop.neighborCount + delta, prop.strongNeighborCount, prop.group, prop.hasRecord);
    }

    private static Function<GridProp, GridProp> setHasRecord(boolean hasRecord) {
        return (prop) -> new GridProp(prop.neighborCount, prop.strongNeighborCount, prop.group, hasRecord);
    }

    private static Function<GridProp, GridProp> setGroup(int group) {
        return (prop) -> new GridProp(prop.neighborCount, prop.strongNeighborCount, group, prop.hasRecord);
    }

    private void updateNeighbors(HexGrids.Axial axial, Function<GridProp, GridProp> updateFunc) {
        HexGrids.neighbors(axial).forEach(neighbor -> {
            GridProp neighborProp = getProp(neighbor);
            updateGroup(neighbor, updateFunc.apply(neighborProp));
        });
    }

    private void updateIndirectNeighbors(HexGrids.Axial axial, Function<GridProp, GridProp> updateFunc) {
        HexGrids.indirectNeighbors(axial).forEach(neighbor -> {
            GridProp neighborProp = getProp(neighbor);
            updateGroup(neighbor, updateFunc.apply(neighborProp));
        });
    }

    private void updateGroup(HexGrids.Axial axial, Function<GridProp, GridProp> updateFunc) {
        GridProp prop = getProp(axial);
        updateGroup(axial, updateFunc.apply(prop));
    }

    public ItemStack removeRecord(HexGrids.Axial axial) {
        ItemStack stack = this.records.remove(axial);
        if (stack == null) {
            return ItemStack.EMPTY;
        }
        updateGroup(axial, setHasRecord(false));
        boolean isStrong = RecordUtils.isStrongRecord(stack);
        updateNeighbors(axial, decreaseNeighbor(isStrong));
        if (isStrong) {
            updateIndirectNeighbors(axial, decreaseNeighbor(false));
        }
        refreshValidPositions();
        this.onRemove(axial);
        return stack;
    }

    public boolean putRecord(HexGrids.Axial axial, ItemStack stack) {
        if (stack.isEmpty()) {
            return !removeRecord(axial).isEmpty();
        }
        if (!RecordUtils.isRecord(stack)) {
            return false;
        }
        boolean currentStrong = RecordUtils.isStrongRecord(stack);

        if (!hasRecord(axial)) {
            updateGroup(axial, setHasRecord(true));
            updateNeighbors(axial, increaseNeighbor(currentStrong));
            if (currentStrong) {
                updateIndirectNeighbors(axial, increaseNeighbor(false));
            }
        } else {

            boolean prevStrong = RecordUtils.isStrongRecord(getRecord(axial));
            if (currentStrong != prevStrong) {
                updateNeighbors(axial, changeStrongNeighbor(currentStrong ? 1 : -1));
                updateIndirectNeighbors(axial, changeNeighbor(currentStrong ? 1 : -1));
            }
        }

        this.records.put(axial, stack);
        refreshValidPositions();
        this.onUpdate(axial, stack);
        return true;

    }


    public void clear() {
        this.records.clear();
        this.gridProps.clear();

        this.updateGroup(new HexGrids.Axial(0, 0), increaseNeighbor(true));
        this.updateNeighbors(new HexGrids.Axial(0, 0), increaseNeighbor(false));
    }

    public void onUpdate(HexGrids.Axial axial, ItemStack stack) {

    }

    public void onRemove(HexGrids.Axial axial) {

    }


}
