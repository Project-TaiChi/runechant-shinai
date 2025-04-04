package io.github.projeccttaichi.runechantshinai.block;

import com.mojang.serialization.MapCodec;
import io.github.projeccttaichi.runechantshinai.block.entity.RecordChestBlockEntity;
import io.github.projeccttaichi.runechantshinai.capability.RecordStorageHandler;
import io.github.projeccttaichi.runechantshinai.init.ModBlockEntities;
import io.github.projeccttaichi.runechantshinai.init.ModCapabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.IBlockCapabilityProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RecordChestBlock extends BaseEntityBlock implements IBlockCapabilityProvider<RecordStorageHandler, Void> {
    public static final MapCodec<RecordChestBlock> CODEC = simpleCodec(RecordChestBlock::new);

    public RecordChestBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RecordChestBlockEntity(pos, state);
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof RecordChestBlockEntity recordChest) {
                // TODO: 打开GUI
                return InteractionResult.CONSUME;
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof RecordChestBlockEntity recordChest) {
                // TODO: 掉落物品
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Nullable
    @Override
    public RecordStorageHandler getCapability(Level level, BlockPos pos, BlockState state, @Nullable BlockEntity be, Void context) {
        if (be instanceof RecordChestBlockEntity recordChest) {
            return recordChest.getInventory();
        }
        return null;
    }
}
