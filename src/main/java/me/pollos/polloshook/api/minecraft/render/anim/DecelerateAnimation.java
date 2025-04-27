package me.pollos.polloshook.api.minecraft.render.anim;

public class DecelerateAnimation extends Animation {
   public DecelerateAnimation(int ms, double endPoint) {
      super(ms, endPoint);
   }

   protected double getEquation(double x) {
      double x1 = x / (double)this.duration;
      return 1.0D - (x1 - 1.0D) * (x1 - 1.0D);
   }
}
