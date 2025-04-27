package me.pollos.polloshook.impl.module.render.freecam;

import me.pollos.polloshook.api.event.events.Stage;
import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.entity.EntityUtil;
import me.pollos.polloshook.api.minecraft.rotations.RotationsUtil;
import me.pollos.polloshook.impl.events.movement.MotionUpdateEvent;
import me.pollos.polloshook.impl.module.combat.aura.modes.Location;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;

public class ListenerMotion extends ModuleListener<Freecam, MotionUpdateEvent> {
   public ListenerMotion(Freecam module) {
      super(module, MotionUpdateEvent.class, 15000);
   }

   public void call(MotionUpdateEvent event) {
      if ((Boolean)((Freecam)this.module).rotate.getValue() && event.getStage() == Stage.PRE && mc.crosshairTarget != null) {
         if (mc.crosshairTarget.getType() == Type.MISS) {
            return;
         }

         HitResult var4 = mc.crosshairTarget;
         float[] rots;
         if (var4 instanceof BlockHitResult) {
            BlockHitResult result = (BlockHitResult)var4;
            rots = RotationsUtil.getRotationsFacing(result.getBlockPos(), result.getSide());
            if (Float.isNaN(rots[0]) || Float.isNaN(rots[1])) {
               return;
            }

            Managers.getRotationManager().setRotations(rots, event);
         } else {
            var4 = mc.crosshairTarget;
            if (var4 instanceof EntityHitResult) {
               EntityHitResult result = (EntityHitResult)var4;
               rots = EntityUtil.getRotationsAtLocation(Location.BODY, result.getEntity());
               Managers.getRotationManager().setRotations(rots, event);
            }
         }
      }

   }
}
