package me.pollos.polloshook.impl.module.movement.fly;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.update.UpdateEvent;
import net.minecraft.entity.effect.StatusEffects;

public class ListenerUpdate extends ModuleListener<Fly, UpdateEvent> {
   private int antiCounter = 0;

   public ListenerUpdate(Fly module) {
      super(module, UpdateEvent.class);
   }

   public void call(UpdateEvent event) {
      if (mc.player != null) {
         if ((Boolean)((Fly)this.module).spoofGround.getValue()) {
            mc.player.setOnGround(false);
         }

         if ((Boolean)((Fly)this.module).antiKick.getValue()) {
            ++this.antiCounter;
            if (this.antiCounter >= 22 && !mc.player.hasStatusEffect(StatusEffects.LEVITATION) && !mc.player.isFallFlying() && !mc.player.isOnGround() && !mc.world.getBlockCollisions(mc.player, mc.player.getBoundingBox().expand(0.0625D).expand(0.0D, -0.55D, 0.0D)).iterator().hasNext()) {
               mc.player.setPos(mc.player.getX(), mc.player.getY() - 0.03126D, mc.player.getZ());
               this.antiCounter = 0;
            }
         }

      }
   }
}