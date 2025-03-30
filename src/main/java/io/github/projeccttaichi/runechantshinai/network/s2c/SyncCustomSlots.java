package io.github.projeccttaichi.runechantshinai.network.s2c;

import io.github.projeccttaichi.runechantshinai.menu.RecordAssemblerMenu;
import io.github.projeccttaichi.runechantshinai.util.HexGrids;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import static io.github.projeccttaichi.runechantshinai.constants.Locations.modLoc;

public record SyncCustomSlots(
        int containerId,
        int slotType,
        int slotId,
        ItemStack itemStack
) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SyncCustomSlots> TYPE = new CustomPacketPayload.Type<>(modLoc("sync_custom_slots"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncCustomSlots> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            SyncCustomSlots::containerId,
            ByteBufCodecs.VAR_INT,
            SyncCustomSlots::slotType,
            ByteBufCodecs.VAR_INT,
            SyncCustomSlots::slotId,
            ItemStack.OPTIONAL_STREAM_CODEC,
            SyncCustomSlots::itemStack,
            SyncCustomSlots::new
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

        menu.handleSyncCustomSlot(this.slotType(), this.slotId(), this.itemStack());

    }
}
