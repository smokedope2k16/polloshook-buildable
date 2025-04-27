package me.pollos.polloshook.api.minecraft.render.anim;

public class TimerHelper {
   public long lastMS = System.currentTimeMillis();

   public void reset() {
      this.lastMS = System.currentTimeMillis();
   }

   public boolean hasTimeElapsed(long time, boolean reset) {
      if (System.currentTimeMillis() - this.lastMS <= time) {
         return false;
      } else if (!reset) {
         return true;
      } else {
         this.reset();
         return true;
      }
   }

   public boolean hasTimeElapsed(long time) {
      return System.currentTimeMillis() - this.lastMS > time;
   }

   public long getTime() {
      return System.currentTimeMillis() - this.lastMS;
   }

   public void setTime(long time) {
      this.lastMS = time;
   }
}
