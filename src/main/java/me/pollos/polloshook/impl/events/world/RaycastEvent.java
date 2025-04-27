package me.pollos.polloshook.impl.events.world;


import me.pollos.polloshook.api.event.events.Event;
import net.minecraft.util.hit.HitResult;

public class RaycastEvent extends Event {
   HitResult result;

   
   public HitResult getResult() {
      return this.result;
   }

   
   public void setResult(HitResult result) {
      this.result = result;
   }

   
   public RaycastEvent(HitResult result) {
      this.result = result;
   }
}
