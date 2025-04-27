package me.pollos.polloshook.impl.module.other.irc.util;


import net.minecraft.text.ClickEvent;
import net.minecraft.text.ClickEvent.Action;

public class RunnableClickEvent extends ClickEvent {
   private final Runnable runnable;

   public RunnableClickEvent(Runnable runnable) {
      super((Action)null, "yo");
      this.runnable = runnable;
   }

   
   public Runnable getRunnable() {
      return this.runnable;
   }
}
