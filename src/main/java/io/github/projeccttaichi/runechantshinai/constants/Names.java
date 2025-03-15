package io.github.projeccttaichi.runechantshinai.constants;

public class Names {

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

}
