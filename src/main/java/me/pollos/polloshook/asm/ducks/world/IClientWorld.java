package me.pollos.polloshook.asm.ducks.world;

import net.minecraft.client.network.PendingUpdateManager;

public interface IClientWorld {
   PendingUpdateManager getPendingUpdateManager();
}
