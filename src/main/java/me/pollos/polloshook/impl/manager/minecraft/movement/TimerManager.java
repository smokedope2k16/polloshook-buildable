package me.pollos.polloshook.impl.manager.minecraft.movement;


import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.event.bus.SubscriberImpl;
import me.pollos.polloshook.api.interfaces.Minecraftable;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.impl.events.update.TickEvent;
import me.pollos.polloshook.impl.module.misc.timer.Timer;

public class TimerManager extends SubscriberImpl implements Minecraftable {
   private float timer = 1.0F;
   private boolean yieldTimer = false;

   public TimerManager() {
      this.listeners.add(new Listener<TickEvent>(TickEvent.class) {
         public void call(TickEvent event) {
            if (mc.player == null) {
               TimerManager.this.reset();
            } else {
               Timer TIMER_MODULE = (Timer)Managers.getModuleManager().get(Timer.class);
               if (TIMER_MODULE.isEnabled()) {
                  TIMER_MODULE.onTimerManagerTick();
               } else {
                  TimerManager.this.set(TimerManager.this.timer);
               }
            }

         }
      });
   }

   public void set(float timer) {
      this.setTimer(timer <= 0.0F ? 0.1F : timer);
   }

   public void reset() {
      this.setYieldTimer(true);
      this.setTimer(1.0F);
      this.setYieldTimer(false);
   }

   
   public void setTimer(float timer) {
      this.timer = timer;
   }

   
   public void setYieldTimer(boolean yieldTimer) {
      this.yieldTimer = yieldTimer;
   }

   
   public float getTimer() {
      return this.timer;
   }

   
   public boolean isYieldTimer() {
      return this.yieldTimer;
   }
}
