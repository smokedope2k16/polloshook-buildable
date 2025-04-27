package me.pollos.polloshook.asm.ducks.util;

import net.minecraft.network.message.LastSeenMessagesCollector;
import net.minecraft.network.message.MessageChain.Packer;

public interface IClientPlayerNetworkManager {
   LastSeenMessagesCollector getLastSeenMessageCollector();

   Packer getMessageChainPacker();
}
