package io.github.projeccttaichi.runechantshinai.data.provider;

import io.github.projeccttaichi.runechantshinai.constants.Ids;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class DataPackProvider extends DatapackBuiltinEntriesProvider {

    public DataPackProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, new RegistrySetBuilder(), Set.of(Ids.MOD_ID));
    }
}
