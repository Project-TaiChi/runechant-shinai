package io.github.projeccttaichi.runechantshinai.data.provider;

import io.github.projeccttaichi.runechantshinai.constants.Ids;
import io.github.projeccttaichi.runechantshinai.init.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ItemModelProvider extends net.neoforged.neoforge.client.model.generators.ItemModelProvider {

    public ItemModelProvider(PackOutput output, ExistingFileHelper helper) {
        super(output, Ids.MOD_ID, helper);
    }

    @Override
    protected void registerModels() {

        this.basicItem(ModItems.RECORD.asItem());

    }


}
