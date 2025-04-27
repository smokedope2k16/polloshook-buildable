package me.pollos.polloshook.api.util.thread;


import me.pollos.polloshook.api.util.logging.ClientLogger;
import me.pollos.polloshook.impl.module.other.hud.elements.consistent.info.util.FPS;

public class FPSThread {
   private int fpsCount = 0;
   private final FPS[] timers = new FPS[20];

   public FPSThread() {
      long startTime = System.nanoTime();

      for(int i = 0; i < this.timers.length; ++i) {
         long plus = startTime + (long)i * 1000000000L / (long)this.timers.length;
         this.timers[i] = new FPS(plus);
      }

   }

   private Thread getThread() {
      return new Thread("FPS Calculation Thread") {
         public void run() {
            FPS[] var1 = FPSThread.this.timers;
            int var2 = var1.length;

            for(int var3 = 0; var3 < var2; ++var3) {
               FPS fps = var1[var3];
               fps.updateFPSCount();
               if (fps.isOver()) {
                  FPSThread.this.fpsCount = fps.getFpsCount();
                  fps.updateStartTime();
               }
            }

         }
      };
   }

   public void start() {
      if (this.getThread().isAlive()) {
         ClientLogger.getLogger().warn("FPS Thread started while alive");
      } else {
         this.getThread().start();
      }
   }

   
   public int getFpsCount() {
      return this.fpsCount;
   }

   
   public FPS[] getTimers() {
      return this.timers;
   }
}
