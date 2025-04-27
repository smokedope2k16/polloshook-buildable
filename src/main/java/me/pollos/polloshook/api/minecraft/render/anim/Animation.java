package me.pollos.polloshook.api.minecraft.render.anim;

public abstract class Animation {
   public TimerHelper timerUtil = new TimerHelper();
   protected int duration;
   protected double endPoint;
   protected AnimationDirection direction;
   protected int x = 0;

   public Animation(int ms, double endPoint) {
      this.duration = ms;
      this.endPoint = endPoint;
      this.direction = AnimationDirection.FORWARDS;
   }

   public Animation(int ms, double endPoint, AnimationDirection direction) {
      this.duration = ms;
      this.endPoint = endPoint;
      this.direction = direction;
   }

   public void setX(int x) {
      this.x = x;
   }

   public int getX() {
      return this.x;
   }

   public boolean finished(AnimationDirection direction) {
      return this.hasElapsed() && this.direction.equals(direction);
   }

   public void reset() {
      this.timerUtil.reset();
   }

   public boolean hasElapsed() {
      return this.timerUtil.hasTimeElapsed((long)this.duration);
   }

   public AnimationDirection getDirection() {
      return this.direction;
   }

   public void setDirection(AnimationDirection direction) {
      if (this.direction != direction) {
         this.direction = direction;
         this.timerUtil.setTime(System.currentTimeMillis() - ((long)this.duration - Math.min((long)this.duration, this.timerUtil.getTime())));
      }

   }

   protected boolean correctOutput() {
      return false;
   }

   public double getOutput() {
      if (this.direction == AnimationDirection.FORWARDS) {
         return this.hasElapsed() ? this.endPoint : this.getEquation((double)this.timerUtil.getTime()) * this.endPoint;
      } else if (this.hasElapsed()) {
         return 0.0D;
      } else if (this.correctOutput()) {
         double revTime = (double)Math.min((long)this.duration, Math.max(0L, (long)this.duration - this.timerUtil.getTime()));
         return this.getEquation(revTime) * this.endPoint;
      } else {
         return (1.0D - this.getEquation((double)this.timerUtil.getTime())) * this.endPoint;
      }
   }

   protected abstract double getEquation(double var1);
}
