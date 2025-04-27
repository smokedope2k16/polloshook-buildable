package me.pollos.polloshook.impl.module.other.hud.elements.consistent.info.util;



public class FPS {
   private long startTime;
   private int fpsCount = 0;

   public FPS(long startTime) {
      this.startTime = startTime;
      this.fpsCount = 0;
   }

   public void updateFPSCount() {
      ++this.fpsCount;
   }

   public boolean isOver() {
      return System.nanoTime() - this.startTime >= 1000000000L;
   }

   public void updateStartTime() {
      while(this.isOver()) {
         this.startTime += 1000000000L;
      }

      this.fpsCount = 0;
   }

   
   public long getStartTime() {
      return this.startTime;
   }

   
   public int getFpsCount() {
      return this.fpsCount;
   }
}
