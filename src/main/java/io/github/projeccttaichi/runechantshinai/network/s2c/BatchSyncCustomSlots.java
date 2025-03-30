package io.github.projeccttaichi.runechantshinai.network.s2c;

import io.github.projeccttaichi.runechantshinai.menu.RecordAssemblerMenu;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.github.projeccttaichi.runechantshinai.constants.Locations.modLoc;

public record BatchSyncCustomSlots(
        int containerId,
        Map<Integer, List<Entry>> data
) implements CustomPacketPayload {
    public static final Type<BatchSyncCustomSlots> TYPE = new Type<>(modLoc("batch_sync_custom_slots"));


    public record Entry(
            int slotId,
            ItemStack itemStack
    ) {
        public static final StreamCodec<RegistryFriendlyByteBuf, Entry> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_INT,
                Entry::slotId,
                ItemStack.OPTIONAL_STREAM_CODEC,
                Entry::itemStack,
                Entry::new
        );
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, BatchSyncCustomSlots> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            BatchSyncCustomSlots::containerId,
            ByteBufCodecs.map(
                    HashMap::new,
                    ByteBufCodecs.VAR_INT,
                    Entry.STREAM_CODEC.apply(ByteBufCodecs.list())
            ),
            BatchSyncCustomSlots::data,
            BatchSyncCustomSlots::new
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


        for (Map.Entry<Integer, List<Entry>> entry : data.entrySet()) {
            for (Entry e : entry.getValue()) {
                menu.handleSyncCustomSlot(entry.getKey(), e.slotId(), e.itemStack());
            }
        }

    }
}
