package me.pollos.polloshook.asm.ducks;

import net.minecraft.client.session.Session;

public interface IMinecraftClient {
   void setItemUseCooldown(int var1);

   int getItemUseCooldown();

   void leftClick();

   void rightClick();

   void setSession(Session var1);

   void $doItemUse();

   boolean is60FPSLimit();

   void $updateWindowTitle();

   void setAttackTicks(int var1);

   String $getWindowTitle();

   boolean isDisconnecting();
}
