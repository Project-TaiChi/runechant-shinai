package io.github.projeccttaichi.runechantshinai.constants;

import net.minecraft.resources.ResourceLocation;

public final class Locations {

    public static ResourceLocation modLoc(String path) {
        return ResourceLocation.fromNamespaceAndPath(Ids.MOD_ID, path);
    }

    public static ResourceLocation guiLoc(String path) {
        return ResourceLocation.fromNamespaceAndPath(Ids.MOD_ID, "textures/gui/" + path);
    }

    public static ResourceLocation blockLoc(String path) {
        return ResourceLocation.fromNamespaceAndPath(Ids.MOD_ID, "block/" + path);
    }

    public static ResourceLocation itemLoc(String path) {
        return ResourceLocation.fromNamespaceAndPath(Ids.MOD_ID, "item/" + path);
    }

    public static ResourceLocation entityLoc(String path) {
        return ResourceLocation.fromNamespaceAndPath(Ids.MOD_ID, "entity/" + path);
    }

    public static ResourceLocation particleLoc(String path) {
        return ResourceLocation.fromNamespaceAndPath(Ids.MOD_ID, "particle/" + path);
    }

    public static ResourceLocation modelLoc(String path) {
        return ResourceLocation.fromNamespaceAndPath(Ids.MOD_ID, "models/" + path);
    }

    public static ResourceLocation forgeLoc(String path) {
        return ResourceLocation.fromNamespaceAndPath("forge", path);
    }

    public static ResourceLocation vanillaLoc(String path) {
        return ResourceLocation.fromNamespaceAndPath("minecraft", path);
    }

}
