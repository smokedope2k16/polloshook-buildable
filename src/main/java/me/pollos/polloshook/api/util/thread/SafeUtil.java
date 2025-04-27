package me.pollos.polloshook.api.util.thread;


import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;

public final class SafeUtil {
   public static void safe(MinecraftClient mc, SafeUtil.Accept action) {
      safeOr(mc, action, () -> {
      });
   }

   public static void safeOr(MinecraftClient mc, SafeUtil.Accept action, Runnable or) {
      ClientWorld level = mc.world;
      ClientPlayerEntity player = mc.player;
      ClientPlayerInteractionManager gameMode = mc.interactionManager;
      if (level != null && player != null && gameMode != null) {
         action.accept(player, level, gameMode);
      } else {
         or.run();
      }

   }

   
   private SafeUtil() {
      throw new UnsupportedOperationException("This is keyCodec utility class and cannot be instantiated");
   }

   @FunctionalInterface
   public interface Accept {
      void accept(ClientPlayerEntity var1, ClientWorld var2, ClientPlayerInteractionManager var3);
   }
}
