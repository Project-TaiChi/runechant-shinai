package io.github.projeccttaichi.runechantshinai.constants;

import net.minecraft.resources.ResourceLocation;

public final class Names {

    public static String dotKey(String prefix, String tail) {
        StringBuilder sb = new StringBuilder();
        sb.append(prefix);
        sb.append(".");
        sb.append(Ids.MOD_ID);
        sb.append(".");
        sb.append(tail);
        return sb.toString();
    }

    public static String containerKey(String tail) {
        return dotKey("container", tail);
    }

    public static String recordKey(ResourceLocation loc) {
        StringBuilder sb = new StringBuilder();
        sb.append("record.");
        sb.append(loc.getNamespace());
        sb.append(".");
        sb.append(loc.getPath());
        return sb.toString();
    }


    public static String recordKey(ResourceLocation loc, String suffix) {
        StringBuilder sb = new StringBuilder();
        sb.append("record.");
        sb.append(loc.getNamespace());
        sb.append(".");
        sb.append(loc.getPath());
        sb.append(".");
        sb.append(suffix);
        return sb.toString();
    }

}
