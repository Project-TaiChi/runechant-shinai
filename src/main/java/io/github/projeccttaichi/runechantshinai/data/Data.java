package io.github.projeccttaichi.runechantshinai.data;

import io.github.projeccttaichi.runechantshinai.constants.Ids;
import io.github.projeccttaichi.runechantshinai.data.provider.*;
import io.github.projeccttaichi.runechantshinai.data.provider.lang.ChineseLanguageProvider;
import io.github.projeccttaichi.runechantshinai.data.provider.lang.EnglishLanguageProvider;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = Ids.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class Data {
    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        var gen = event.getGenerator();
        var packOutput = gen.getPackOutput();
        var helper = event.getExistingFileHelper();
        var lookupProvider = event.getLookupProvider();

        gen.addProvider(event.includeClient(), new EnglishLanguageProvider(packOutput));
        gen.addProvider(event.includeClient(), new ChineseLanguageProvider(packOutput));

        gen.addProvider(event.includeClient(), new ItemModelProvider(packOutput, helper));
        gen.addProvider(event.includeClient(), new BlockStateProvider(packOutput, helper));
        gen.addProvider(event.includeServer(), new RecipeProvider(packOutput, lookupProvider));
        gen.addProvider(event.includeServer(), new DataPackProvider(packOutput, lookupProvider));
        gen.addProvider(event.includeServer(), new LootTableProvider(packOutput, lookupProvider));

        BlockTagsProvider blockTagsProvider = new BlockTagsProvider(packOutput, lookupProvider, helper);
        gen.addProvider(event.includeServer(), blockTagsProvider);
        gen.addProvider(event.includeServer(), new ItemTagsProvider(packOutput, lookupProvider, blockTagsProvider.contentsGetter(), helper));


    }
}
