package me.pollos.polloshook.impl.module.combat.autoarmour.util;

import java.util.Arrays;
import java.util.List;

import me.pollos.polloshook.api.util.math.Passable;
import me.pollos.polloshook.api.util.math.StopWatch;

public class QuadTimer implements Passable<QuadTimer> {
   private final StopWatch timer1;
   private final StopWatch timer2;
   private final StopWatch timer3;
   private final StopWatch timer4;
   private final List<Passable<?>> timers;

   public boolean passed(double delay) {
      return this.getTimers().stream().allMatch((p) -> {
         return p.passed(delay);
      });
   }

   public boolean passed(long delay) {
      return this.passed((double)delay);
   }

   public boolean sleep(double delay) {
      return this.getTimers().stream().allMatch((p) -> {
         return p.sleep(delay);
      });
   }

   public boolean sleep(long delay) {
      return this.sleep((double)delay);
   }

   public QuadTimer reset() {
      this.getTimers().forEach(Passable::reset);
      return this;
   }

   
   public QuadTimer() {
      this.timers = Arrays.asList(this.timer1 = new StopWatch(), this.timer2 = new StopWatch(), this.timer3 = new StopWatch(), this.timer4 = new StopWatch());
   }

   
   public StopWatch getTimer1() {
      return this.timer1;
   }

   
   public StopWatch getTimer2() {
      return this.timer2;
   }

   
   public StopWatch getTimer3() {
      return this.timer3;
   }

   
   public StopWatch getTimer4() {
      return this.timer4;
   }

   
   private List<Passable<?>> getTimers() {
      return this.timers;
   }
}
