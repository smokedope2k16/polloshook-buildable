package me.pollos.polloshook.impl.module.combat.autoarmour.util;

import java.util.Arrays;
import java.util.List;

import me.pollos.polloshook.api.util.math.Passable;
import me.pollos.polloshook.api.util.math.StopWatch;

public class AutoArmorTimer implements Passable<AutoArmorTimer> {
   private final QuadTimer autoArmorTimer;
   private final StopWatch autoTotemTimer;
   private final List<Passable<?>> timers;

   public boolean passed(double ms) {
      return this.getTimers().stream().allMatch((p) -> {
         return p.passed(ms);
      });
   }

   public boolean passed(long ms) {
      return this.passed((double)ms);
   }

   public boolean sleep(double delay) {
      return this.getTimers().stream().allMatch((p) -> {
         return p.sleep(delay);
      });
   }

   public boolean sleep(long delay) {
      return this.sleep((double)delay);
   }

   public AutoArmorTimer reset() {
      this.getTimers().forEach(Passable::reset);
      return this;
   }

   
   public AutoArmorTimer() {
      this.timers = Arrays.asList(this.autoArmorTimer = new QuadTimer(), this.autoTotemTimer = new StopWatch());
   }

   
   public QuadTimer getAutoArmorTimer() {
      return this.autoArmorTimer;
   }

   
   public StopWatch getAutoTotemTimer() {
      return this.autoTotemTimer;
   }

   
   private List<Passable<?>> getTimers() {
      return this.timers;
   }
}
