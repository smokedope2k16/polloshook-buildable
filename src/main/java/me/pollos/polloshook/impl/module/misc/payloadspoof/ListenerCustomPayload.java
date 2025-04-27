package me.pollos.polloshook.impl.module.misc.payloadspoof;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.minecraft.network.PacketUtil;
import me.pollos.polloshook.impl.events.network.PacketEvent;
import net.minecraft.network.packet.BrandCustomPayload;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;

public class ListenerCustomPayload extends ModuleListener<PayloadSpoof, PacketEvent.Send<CustomPayloadC2SPacket>> {
    public ListenerCustomPayload(PayloadSpoof module) {
        super(module, PacketEvent.Send.class, CustomPayloadC2SPacket.class);
    }

    public void call(PacketEvent.Send<CustomPayloadC2SPacket> event) {
        CustomPayloadC2SPacket packet = event.getPacket();
        Identifier id = packet.payload().getId().id(); 
        if (id.equals(BrandCustomPayload.ID)) {
            CustomPayloadC2SPacket spoofedPacket = new CustomPayloadC2SPacket(new BrandCustomPayload("vanilla"));
            PacketUtil.sendNoEvent(spoofedPacket);
            event.setCanceled(true);
        }
    }
}
