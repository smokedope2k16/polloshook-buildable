package me.pollos.polloshook.impl.module.misc.autoreconnect.util;


import me.pollos.polloshook.api.util.math.Passable;

public class PauseableStopWatch implements Passable<PauseableStopWatch> {
   private volatile long time = System.currentTimeMillis();
   private boolean paused = false;
   private long pauseTime = 0L;

   public boolean passed(double ms) {
      return (double)(System.currentTimeMillis() - this.time - (this.paused ? System.currentTimeMillis() - this.pauseTime : 0L)) >= ms;
   }

   public boolean passed(long ms) {
      return this.passed((double)ms);
   }

   public PauseableStopWatch reset() {
      this.time = System.currentTimeMillis();
      this.paused = false;
      this.pauseTime = 0L;
      return this;
   }

   public boolean sleep(double time) {
      if ((double)this.getTime() >= time) {
         this.reset();
         return true;
      } else {
         return false;
      }
   }

   public boolean sleep(long delay) {
      return this.sleep((double)delay);
   }

   public void togglePause() {
      if (this.paused) {
         this.unpause();
      } else {
         this.pause();
      }

   }

   public long getTime() {
      return this.paused ? this.pauseTime - this.time : System.currentTimeMillis() - this.time;
   }

   public void pause() {
      if (!this.paused) {
         this.pauseTime = System.currentTimeMillis();
         this.paused = true;
      }

   }

   public void unpause() {
      if (this.paused) {
         this.time += System.currentTimeMillis() - this.pauseTime;
         this.paused = false;
      }

   }

   
   public boolean isPaused() {
      return this.paused;
   }

   
   public long getPauseTime() {
      return this.pauseTime;
   }

   
   public void setTime(long time) {
      this.time = time;
   }

   
   public void setPauseTime(long pauseTime) {
      this.pauseTime = pauseTime;
   }
}
