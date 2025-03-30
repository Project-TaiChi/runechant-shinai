package io.github.projeccttaichi.runechantshinai.item;

import io.github.projeccttaichi.runechantshinai.compoment.RecordComponent;
import io.github.projeccttaichi.runechantshinai.init.ModComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

import static io.github.projeccttaichi.runechantshinai.constants.Names.recordKey;

public class RecordItem extends Item {

    public RecordItem(Properties properties) {
        super(properties);
    }


    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> components, TooltipFlag tooltipFlag) {

        RecordComponent recordComponent = stack.get(ModComponents.RECORD_COMPONENT);

        if (recordComponent != null) {
            components.add(Component.translatable(recordKey(recordComponent.id())));
        }

    }
}
