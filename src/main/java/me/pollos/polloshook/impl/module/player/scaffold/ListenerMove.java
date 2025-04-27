package me.pollos.polloshook.impl.module.player.scaffold;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.movement.MovementUtil;
import me.pollos.polloshook.impl.events.movement.MoveEvent;
import me.pollos.polloshook.impl.module.movement.noaccel.NoAccel;

public class ListenerMove extends ModuleListener<Scaffold, MoveEvent> {
   public ListenerMove(Scaffold module) {
      super(module, MoveEvent.class, 2147483637);
   }

   public void call(MoveEvent event) {
      NoAccel NO_ACCEL = (NoAccel)Managers.getModuleManager().get(NoAccel.class);
      if (NO_ACCEL.isEnabled() && !NO_ACCEL.cantNoAccel()) {
         NO_ACCEL.strafe(event, MovementUtil.getDefaultMoveSpeed());
      }

      if (mc.player.isOnGround() && (Boolean)((Scaffold)this.module).safeWalk.getValue()) {
         this.doSafeWalk(event);
      }

   }

   private void doSafeWalk(MoveEvent event) {
      double x = event.getX();
      double y = event.getY();
      double z = event.getZ();
      double increment = 0.05D;

      while(x != 0.0D && this.isOffsetBBEmpty(x, 0.0D)) {
         if (x < increment && x >= -increment) {
            x = 0.0D;
         } else if (x > 0.0D) {
            x -= increment;
         } else {
            x += increment;
         }
      }

      while(z != 0.0D && this.isOffsetBBEmpty(0.0D, z)) {
         if (z < increment && z >= -increment) {
            z = 0.0D;
         } else if (z > 0.0D) {
            z -= increment;
         } else {
            z += increment;
         }
      }

      while(x != 0.0D && z != 0.0D && this.isOffsetBBEmpty(x, z)) {
         if (x < increment && x >= -increment) {
            x = 0.0D;
         } else if (x > 0.0D) {
            x -= increment;
         } else {
            x += increment;
         }

         if (z < increment && z >= -increment) {
            z = 0.0D;
         } else if (z > 0.0D) {
            z -= increment;
         } else {
            z += increment;
         }
      }

      event.setXZ(x, z);
      event.setY(y);
   }

   private boolean isOffsetBBEmpty(double offsetX, double offsetZ) {
      return !mc.world.getCollisions(mc.player, mc.player.getBoundingBox().offset(offsetX, -1.0D, offsetZ)).iterator().hasNext();
   }
}
