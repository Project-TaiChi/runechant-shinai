package io.github.projeccttaichi.runechantshinai.network;

import io.github.projeccttaichi.runechantshinai.network.c2s.CustomSlotAction;
import io.github.projeccttaichi.runechantshinai.network.s2c.BatchSyncCustomSlots;
import io.github.projeccttaichi.runechantshinai.network.s2c.SyncCustomSlots;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.HandlerThread;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class ModNetwork {
    private static final String PROTOCOL_VERSION = "1.0";
    
    public static void register( RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(PROTOCOL_VERSION);
        registrar.executesOn(HandlerThread.MAIN);

        registrar.playToServer(CustomSlotAction.TYPE, CustomSlotAction.STREAM_CODEC, CustomSlotAction::handle);
        registrar.playToClient(SyncCustomSlots.TYPE, SyncCustomSlots.STREAM_CODEC, SyncCustomSlots::handle);
        registrar.playToClient(BatchSyncCustomSlots.TYPE, BatchSyncCustomSlots.STREAM_CODEC, BatchSyncCustomSlots::handle);
    }


}
