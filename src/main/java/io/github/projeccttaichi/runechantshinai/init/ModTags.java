package io.github.projeccttaichi.runechantshinai.init;

import io.github.projeccttaichi.runechantshinai.RunechantShinai;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import static io.github.projeccttaichi.runechantshinai.constants.Locations.forgeLoc;
import static io.github.projeccttaichi.runechantshinai.constants.Locations.modLoc;

/**
 * 存储模组中使用的所有标签
 */
public class ModTags {
    public static class Blocks {
        // 方块标签
        public static final TagKey<Block> RUNE_BLOCKS = tag("rune_blocks");
        public static final TagKey<Block> NEEDS_RUNE_TOOL = tag("needs_rune_tool");
        public static final TagKey<Block> MANA_CONDUCTOR = tag("mana_conductor");
        public static final TagKey<Block> RUNE_POWER_SOURCE = tag("rune_power_source");
        
        // 生成标签
        private static TagKey<Block> tag(String name) {
            return BlockTags.create(modLoc(name));
        }
        
        // 使用Minecraft命名空间的标签
        private static TagKey<Block> forgeTag(String name) {
            return BlockTags.create(forgeLoc(name));
        }
    }
    
    public static class Items {
        
        public static final TagKey<Item> RECORDS = tag("records");
        
        // 生成标签
        private static TagKey<Item> tag(String name) {
            return ItemTags.create(modLoc(name));
        }
        
        // 使用Forge命名空间的标签
        private static TagKey<Item> forgeTag(String name) {
            return ItemTags.create(forgeLoc(name));
        }
    }
    
    /**
     * 初始化标签 - 没有实际代码，但为API完整性保留
     */
    public static void init() {
        // 初始化静态类，确保标签被加载
    }
}
