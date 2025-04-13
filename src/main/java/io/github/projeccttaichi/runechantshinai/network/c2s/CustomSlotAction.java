package io.github.projeccttaichi.runechantshinai.network.c2s;

import io.github.projeccttaichi.runechantshinai.menu.CustomSlotHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.util.ByIdMap;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.function.IntFunction;

import static io.github.projeccttaichi.runechantshinai.constants.Locations.modLoc;

public record CustomSlotAction(
        int containerId,
        int slotGroup,
        int slotId,
        Action action,
        OptType optType
) implements CustomPacketPayload {


    public enum Action {
        PICK_OR_REPLACE,
        QUICK_MOVE,
        CLEAR
    }

    public enum OptType {
        LEFT_CLICK,
        RIGHT_CLICK,
        MIDDLE_CLICK
    }

    public static final IntFunction<Action> ACTION_BY_ID =
            ByIdMap.continuous(
                    Action::ordinal,
                    Action.values(),
                    ByIdMap.OutOfBoundsStrategy.ZERO
            );
    public static final IntFunction<OptType> OPT_TYPE_BY_ID =
            ByIdMap.continuous(
                    OptType::ordinal,
                    OptType.values(),
                    ByIdMap.OutOfBoundsStrategy.ZERO
            );

    public static final CustomPacketPayload.Type<CustomSlotAction> TYPE = new CustomPacketPayload.Type<>(modLoc("custom_slot_action"));

    public static final StreamCodec<ByteBuf, CustomSlotAction> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            CustomSlotAction::containerId,
            ByteBufCodecs.VAR_INT,
            CustomSlotAction::slotGroup,
            ByteBufCodecs.VAR_INT,
            CustomSlotAction::slotId,
            ByteBufCodecs.idMapper(ACTION_BY_ID, Action::ordinal),
            CustomSlotAction::action,
            ByteBufCodecs.idMapper(OPT_TYPE_BY_ID, OptType::ordinal),
            CustomSlotAction::optType,
            CustomSlotAction::new
    );

    @Override
    @NotNull
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }


    public void handle(IPayloadContext ctx) {

        if (!(ctx.player().containerMenu instanceof CustomSlotHandler menu)) {
            return;
        }


        if (ctx.player().containerMenu.containerId != this.containerId()) {
            return;
        }

        menu.handleCustomSlotAction(ctx, this);

    }
}
