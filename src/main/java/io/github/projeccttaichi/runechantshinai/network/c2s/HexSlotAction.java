package io.github.projeccttaichi.runechantshinai.network.c2s;

import io.github.projeccttaichi.runechantshinai.menu.RecordAssemblerMenu;
import io.github.projeccttaichi.runechantshinai.util.HexGrids;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.util.ByIdMap;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.function.IntFunction;

import static io.github.projeccttaichi.runechantshinai.constants.Locations.modLoc;

public record HexSlotAction(
        int containerId,
        int stateId,
        HexGrids.Axial position,
        Action action


) implements CustomPacketPayload {


    public enum Action {
        PICK_OR_REPLACE,
        QUICK_MOVE,
        CLEAR
    }

    public static final IntFunction<Action> ACTION_BY_ID =
            ByIdMap.continuous(
                    Action::ordinal,
                    Action.values(),
                    ByIdMap.OutOfBoundsStrategy.ZERO
            );

    public static final CustomPacketPayload.Type<HexSlotAction> TYPE = new CustomPacketPayload.Type<>(modLoc("hex_slot_action"));

    public static final StreamCodec<ByteBuf, HexSlotAction> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            HexSlotAction::containerId,
            ByteBufCodecs.VAR_INT,
            HexSlotAction::stateId,
            ByteBufCodecs.VAR_LONG.map(
                    HexGrids.Axial::unpacked,
                    HexGrids.Axial::packed
            ),
            HexSlotAction::position,
            ByteBufCodecs.idMapper(ACTION_BY_ID, Action::ordinal),
            HexSlotAction::action,
            HexSlotAction::new
    );

    @Override
    @NotNull
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }


    public void handle(IPayloadContext ctx) {

        if (!(ctx.player().containerMenu instanceof RecordAssemblerMenu menu)) {
            return;
        }


        if (menu.containerId != this.containerId() || menu.getStateId() != this.stateId()) {
            return;
        }

        menu.handleHexSlotAction(ctx, this);



    }
}
