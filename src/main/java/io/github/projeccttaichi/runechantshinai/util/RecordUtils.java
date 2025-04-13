package io.github.projeccttaichi.runechantshinai.util;

import io.github.projeccttaichi.runechantshinai.init.ModComponents;
import io.github.projeccttaichi.runechantshinai.init.ModRecords;
import io.github.projeccttaichi.runechantshinai.magic.record.BaseRecord;
import io.github.projeccttaichi.runechantshinai.magic.record.RecordType;
import net.minecraft.world.item.ItemStack;

public class RecordUtils {

    public static boolean isRecord(ItemStack stack) {
        var component = stack.get(ModComponents.RECORD_COMPONENT.get());
        return component != null;
    }

    public static boolean isStrongRecord(ItemStack stack) {
        var component = stack.get(ModComponents.RECORD_COMPONENT.get());
        if (component == null) {
            return false;
        }

        BaseRecord baseRecord = ModRecords.RECORD_REGISTRY.get(component.id());
        if (baseRecord == null) {
            return false;
        }
        return baseRecord.getType() == RecordType.Cast || baseRecord.getType() == RecordType.Trigger;
    }
}
