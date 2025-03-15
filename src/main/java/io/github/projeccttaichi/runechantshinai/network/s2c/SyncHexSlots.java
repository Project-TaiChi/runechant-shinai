package io.github.projeccttaichi.runechantshinai.network.s2c;

import io.github.projeccttaichi.runechantshinai.menu.RecordAssemblerMenu;
import io.github.projeccttaichi.runechantshinai.util.HexGrids;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import static io.github.projeccttaichi.runechantshinai.constants.Locations.modLoc;

public record SyncHexSlots(
        int containerId,
        HexGrids.Axial position,
        ItemStack itemStack
) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SyncHexSlots> TYPE = new CustomPacketPayload.Type<>(modLoc("sync_hex_slots"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncHexSlots> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            SyncHexSlots::containerId,
            ByteBufCodecs.VAR_LONG.map(
                    HexGrids.Axial::unpacked,
                    HexGrids.Axial::packed
            ),
            SyncHexSlots::position,
            ItemStack.OPTIONAL_STREAM_CODEC,
            SyncHexSlots::itemStack,
            SyncHexSlots::new
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


        if (menu.containerId != this.containerId()) {
            return;
        }

        menu.handleSyncHexSlot(this.position(), this.itemStack());

    }
}
