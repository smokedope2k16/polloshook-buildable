package me.pollos.polloshook.asm.ducks.world;

import net.minecraft.client.network.SequencedPacketCreator;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;

public interface IClientPlayerInteractionManager {
   void syncItem();

   void setBlockHitDelay(int var1);

   void sendPacketWithSequence(ClientWorld var1, SequencedPacketCreator var2);

   boolean hittingPos(BlockPos var1);

   void setHittingPosBool(boolean var1);

   void setBreakingProgress(float var1);

   void setCurrentBreakingPos(BlockPos var1);
}
