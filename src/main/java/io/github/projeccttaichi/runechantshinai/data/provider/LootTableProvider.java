package io.github.projeccttaichi.runechantshinai.data.provider;

import com.google.common.collect.Iterables;
import io.github.projeccttaichi.runechantshinai.init.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.neoforge.registries.DeferredHolder;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class LootTableProvider extends net.minecraft.data.loot.LootTableProvider {

    public LootTableProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, Set.of(), List.of(new SubProviderEntry(TaiBlockLoot::new, LootContextParamSets.BLOCK)), registries);
    }

    public static class TaiBlockLoot extends BlockLootSubProvider {

        protected TaiBlockLoot(HolderLookup.Provider lookupProvider) {
            super(Set.of(), FeatureFlags.REGISTRY.allFlags(), lookupProvider);
        }

        @Override
        protected void generate() {
            this.dropSelf(ModBlocks.RECORD_ASSEMBLER.get());
        }

        @Nonnull
        @Override
        protected Iterable<Block> getKnownBlocks() {
            // 模组自定义的方块战利品表必须覆盖此方法，以绕过对原版方块战利品表的检查（此处返回该模组的所有方块）
            return Iterables.transform(ModBlocks.BLOCKS.getEntries(), DeferredHolder::get);
        }

    }
}
