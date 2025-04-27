package me.pollos.polloshook.impl.module.combat.aura;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import me.pollos.polloshook.api.event.events.Stage;
import me.pollos.polloshook.api.event.listener.SafeModuleListener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.entity.EntityUtil;
import me.pollos.polloshook.api.minecraft.movement.PositionUtil;
import me.pollos.polloshook.api.minecraft.rotations.RaycastUtil;
import me.pollos.polloshook.api.util.math.MathUtil;
import me.pollos.polloshook.api.util.math.StopWatch;
import me.pollos.polloshook.asm.ducks.IMinecraftClient;
import me.pollos.polloshook.impl.events.movement.MotionUpdateEvent;
import me.pollos.polloshook.impl.module.combat.aura.modes.Location;
import me.pollos.polloshook.impl.module.combat.aura.modes.TpsSync;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;

public class ListenerMotion extends SafeModuleListener<Aura, MotionUpdateEvent> {
   protected final StopWatch rotateTimer = new StopWatch();

   public ListenerMotion(Aura module) {
      super(module, MotionUpdateEvent.class, 10000);
   }

   public void safeCall(MotionUpdateEvent event) {
      ((Aura)this.module).target = null;
      if (((Aura)this.module).canAttack()) {
         ((IMinecraftClient)mc).setAttackTicks(0);
         List<LivingEntity> validTargets = new ArrayList<>();
         Iterator<Entity> var3Iterator = mc.world.getEntities().iterator();

         while(var3Iterator.hasNext()) {
            Entity entity = var3Iterator.next();
            if (entity instanceof LivingEntity) {
               LivingEntity living = (LivingEntity)entity;
               if (this.isValid(living)) {
                  validTargets.add(living);
               }
            }
         }

         ((Aura)this.module).targets = validTargets;
         ((Aura)this.module).target = ((Aura)this.module).targets.stream().filter(this::isValid).min(Comparator.comparingDouble((livingx) -> {
            String var2 = ((Aura)this.module).enemyFinding.getTarget().name().toUpperCase();
            byte targetFinderIndex = -1;
            switch(var2.hashCode()) {
            case 87631:
               if (var2.equals("YAW")) {
                  targetFinderIndex = 0;
               }
               break;
            case 62548255:
               if (var2.equals("ARMOR")) {
                  targetFinderIndex = 2;
               }
               break;
            case 1071086581:
               if (var2.equals("DISTANCE")) {
                  targetFinderIndex = 1;
               }
               break;
            case 2127033948:
               if (var2.equals("HEALTH")) {
                  targetFinderIndex = 3;
               }
            }

            Vec3d pos;
            switch(targetFinderIndex) {
            case 0:
               pos = mc.player.getRotationVec(mc.getRenderTickCounter().getTickDelta(true));
               Vec3d livingPos = livingx.getPos().subtract(mc.player.getPos()).normalize();
               double angle = Math.acos(pos.dotProduct(livingPos));
               return (double)((float)Math.toDegrees(angle));
            case 1:
               pos = mc.player.getPos();
               return (double)((float)livingx.squaredDistanceTo(pos.x, pos.y, pos.z));
            case 2:
               return (double)EntityUtil.getArmor(livingx);
            case 3:
               return (double)EntityUtil.getHealth(livingx);
            default:
               return (double)mc.player.distanceTo(livingx);
            }
         })).orElse(null);

         if (event.getStage() == Stage.PRE && ((Aura)this.module).target != null) {
            ((Aura)this.module).rotations = EntityUtil.getRotationsAtLocation((Location)((Aura)this.module).bone.getValue(), ((Aura)this.module).target);
            if ((Boolean)((Aura)this.module).rotate.getValue() || !this.rotateTimer.passed(100L) && (Boolean)((Aura)this.module).rotate.getValue() && ((Aura)this.module).rotations != null) {
               ((Aura)this.module).setRotations(event);
               this.rotateTimer.reset();
            }

            float factoid = 0.0F;
            switch((TpsSync)((Aura)this.module).tpsSync.getValue()) {
            case AVERAGE:
               factoid = 20.0F - Managers.getTpsManager().getCurrentTps();
               break;
            case LATEST:
               factoid = 20.0F - Managers.getTpsManager().getTps();
            }

            if ((Boolean)((Aura)this.module).waitForCrit.getValue() && !mc.player.isOnGround() && mc.player.fallDistance > 0.0F && (double)mc.player.fallDistance < 1.2D && !mc.player.isClimbing() && !mc.player.isTouchingWater()) {
               return;
            }

            ((Aura)this.module).attack(factoid);
         }

      }
   }

   private boolean isValid(LivingEntity entity) {
      Vec3d from = (Boolean)((Aura)this.module).eyesPos.getValue() ? mc.player.getEyePos() : mc.player.getPos();
      if (!this.isInRangeWall(from, entity)) {
         return false;
      } else if (!this.isInRange(from, entity)) {
         return false;
      } else {
         return ((Aura)this.module).enemyFinding.isValidEntityStatus(entity) && ((Aura)this.module).enemyFinding.isValidEntityType(entity);
      }
   }

   private boolean isInRangeWall(Vec3d from, Entity target) {
      if (RaycastUtil.hasLineOfSight(PositionUtil.getEyesPos(target))) {
         return true;
      } else {
         double distance = from.squaredDistanceTo(target.getPos());
         return distance < (double)MathUtil.square(Math.min((Float)((Aura)this.module).wallRange.getValue(), (Float)((Aura)this.module).range.getValue()));
      }
   }

   private boolean isInRange(Vec3d from, Entity target) {
      double distance = (Boolean)((Aura)this.module).protocol.getValue() ? from.squaredDistanceTo(target.getPos()) : from.squaredDistanceTo(MathUtil.getVecRelativeToPlayer(target.getX(), target.getY(), target.getZ(), from, (double)target.getWidth(), (double)target.getHeight()));
      return distance < (double)MathUtil.square((Float)((Aura)this.module).range.getValue());
   }
}