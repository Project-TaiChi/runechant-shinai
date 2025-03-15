package io.github.projeccttaichi.runechantshinai.network;

import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.projeccttaichi.runechantshinai.network.c2s.HexSlotAction;
import io.github.projeccttaichi.runechantshinai.network.s2c.SyncHexSlots;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.HandlerThread;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class ModNetwork {
    private static final String PROTOCOL_VERSION = "1.0";
    
    public static void register( RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(PROTOCOL_VERSION);
        registrar.executesOn(HandlerThread.MAIN);

        registrar.playToServer(HexSlotAction.TYPE, HexSlotAction.STREAM_CODEC, HexSlotAction::handle);
        registrar.playToClient(SyncHexSlots.TYPE, SyncHexSlots.STREAM_CODEC, SyncHexSlots::handle);
    }


}
