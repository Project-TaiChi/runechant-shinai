package io.github.projeccttaichi.runechantshinai.block;

import com.mojang.serialization.MapCodec;
import io.github.projeccttaichi.runechantshinai.menu.RecordAssemblerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import static io.github.projeccttaichi.runechantshinai.constants.Names.containerKey;

public class RecordChestBlock extends Block {
    public static final MapCodec<RecordChestBlock> CODEC = simpleCodec(RecordChestBlock::new);
    private static final Component CONTAINER_TITLE = Component.translatable(containerKey("record_assembler"));
    public RecordChestBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    protected @Nullable MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        return new SimpleMenuProvider((id, inventory, player) -> {
            return new RecordAssemblerMenu(id, inventory, player, ContainerLevelAccess.create(level, pos));
        }, CONTAINER_TITLE);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if(!level.isClientSide) {
            player.openMenu(state.getMenuProvider(level, pos));
        }

        return InteractionResult.SUCCESS;
    }
}
