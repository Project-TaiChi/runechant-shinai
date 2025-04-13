package io.github.projeccttaichi.runechantshinai.block;

import com.mojang.serialization.MapCodec;
import io.github.projeccttaichi.runechantshinai.block.entity.RecordChestBlockEntity;
import io.github.projeccttaichi.runechantshinai.init.ModBlockEntities;
import io.github.projeccttaichi.runechantshinai.init.ModMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.IBlockCapabilityProvider;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RecordChestBlock extends BaseEntityBlock implements IBlockCapabilityProvider<IItemHandler, Direction> {
    public static final MapCodec<RecordChestBlock> CODEC = simpleCodec(RecordChestBlock::new);

    public RecordChestBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends RecordChestBlock> codec() {
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
                player.openMenu(recordChest);
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
                for(int i = 0; i < recordChest.getSize(); i++) {
                    Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), recordChest.getItem(i));
                }
                level.updateNeighbourForOutputSignal(pos, state.getBlock());
                recordChest.clearContent();
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Nullable
    @Override
    public IItemHandler getCapability(Level level, BlockPos pos, BlockState state, @Nullable BlockEntity be, Direction context) {
        if (be instanceof RecordChestBlockEntity recordChest) {
            return recordChest.getInventory();
        }
        return null;
    }
}
