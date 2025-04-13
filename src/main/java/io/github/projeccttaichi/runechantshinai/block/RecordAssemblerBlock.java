package io.github.projeccttaichi.runechantshinai.block;

import com.mojang.serialization.MapCodec;
import io.github.projeccttaichi.runechantshinai.block.entity.RecordAssemblerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import static io.github.projeccttaichi.runechantshinai.constants.Names.containerKey;

public class RecordAssemblerBlock extends BaseEntityBlock {
    public static final MapCodec<RecordAssemblerBlock> CODEC = simpleCodec(RecordAssemblerBlock::new);
    public RecordAssemblerBlock(Properties properties) {
        super(properties);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected MapCodec<? extends RecordAssemblerBlock> codec() {
        return CODEC;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof RecordAssemblerBlockEntity assemblerBE) {
                player.openMenu(assemblerBE);
                return InteractionResult.CONSUME;
            }
        }
        return InteractionResult.SUCCESS;
    }
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RecordAssemblerBlockEntity(pos, state);
    }



    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof RecordAssemblerBlockEntity assemblerBE) {
                assemblerBE.onPlayerRemoved();
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }
}
