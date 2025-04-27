package me.pollos.polloshook.impl.module.misc.antiaim;

import me.pollos.polloshook.api.event.events.Stage;
import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.entity.EntityUtil;
import me.pollos.polloshook.api.minecraft.entity.PlayerUtil;
import me.pollos.polloshook.api.util.math.RandomUtil;
import me.pollos.polloshook.api.value.value.targeting.TargetUtil;
import me.pollos.polloshook.impl.events.movement.MotionUpdateEvent;
import me.pollos.polloshook.impl.module.combat.aura.modes.Location;
import me.pollos.polloshook.impl.module.misc.antiaim.mode.AntiAimPitch;
import me.pollos.polloshook.impl.module.misc.antiaim.mode.AntiAimYaw;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.MathHelper;

public class ListenerMotion extends ModuleListener<AntiAim, MotionUpdateEvent> {
   private static final float IGNORE_NUMBER = -100001.0F;
   private float lastYaw = -999.0F;

   public ListenerMotion(AntiAim module) {
      super(module, MotionUpdateEvent.class);
   }

   public void call(MotionUpdateEvent event) {
      if (event.getStage() == Stage.PRE && !event.isModified()) {
         if (!mc.options.attackKey.isPressed() || mc.crosshairTarget.getType() == Type.MISS) {
            if (!mc.options.useKey.isPressed() || PlayerUtil.isEating()) {
               float[] yawBounds = new float[]{180.0F, -180.0F};
               float[] pitchBounds = (Boolean)((AntiAim)this.module).illegalAngles.getValue() ? new float[]{180.0F, -180.0F} : new float[]{90.0F, -90.0F};
               ((AntiAim)this.module).customYaw.setMaximum(yawBounds[0]);
               ((AntiAim)this.module).customYaw.setMinimum(yawBounds[1]);
               ((AntiAim)this.module).customPitch.setMaximum(pitchBounds[0]);
               ((AntiAim)this.module).customPitch.setMinimum(pitchBounds[1]);
               float var10000;
               switch((AntiAimYaw)((AntiAim)this.module).yaw.getValue()) {
               case OFF:
                  var10000 = -100001.0F;
                  break;
               case ZERO:
                  var10000 = 0.0F;
                  break;
               case STARE:
                  PlayerEntity target = TargetUtil.getEnemySimple(2.147483647E9D);
                  var10000 = target != null ? EntityUtil.getRotationsAtLocation(Location.HEAD, target)[0] : -100001.0F;
                  break;
               case CUSTOM:
                  var10000 = (Float)((AntiAim)this.module).customYaw.getValue();
                  break;
               case SPIN:
                  var10000 = this.getSpinYaw() % 360.0F;
                  break;
               case RANDOM:
                  var10000 = (float)RandomUtil.getRandom().nextInt((int)yawBounds[1], (int)yawBounds[0]);
                  break;
               default:
                  throw new MatchException((String)null, (Throwable)null);
               }

               float yaw = var10000;
               this.lastYaw = yaw;
               switch((AntiAimPitch)((AntiAim)this.module).pitch.getValue()) {
               case OFF:
                  var10000 = -100001.0F;
                  break;
               case UP:
                  var10000 = -90.0F;
                  break;
               case ZERO:
                  var10000 = 0.0F;
                  break;
               case DOWN:
                  var10000 = 90.0F;
                  break;
               case STARE:
                  PlayerEntity target = TargetUtil.getEnemySimple(2.147483647E9D);
                  var10000 = target != null ? EntityUtil.getRotationsAtLocation(Location.HEAD, target)[1] : -100001.0F;
                  break;
               case CUSTOM:
                  var10000 = (Float)((AntiAim)this.module).customPitch.getValue();
                  break;
               case RANDOM:
                  var10000 = (float)RandomUtil.getRandom().nextInt((int)pitchBounds[1], (int)pitchBounds[0]);
                  break;
               default:
                  throw new MatchException((String)null, (Throwable)null);
               }

               float pitch = var10000;
               float finalYaw = yaw != -100001.0F && !Float.isNaN(yaw) ? this.wrapToBounds(yaw, yawBounds) : mc.player.getYaw();
               float finalPitch = pitch != -100001.0F && !Float.isNaN(yaw) ? this.wrapToBounds(pitch, pitchBounds) : mc.player.getPitch();
               Managers.getRotationManager().setRotations(finalYaw, finalPitch, event);
               this.lastYaw = finalYaw;
            }
         }
      }
   }

   private float wrapToBounds(float value, float[] bounds) {
      return ((AntiAim)this.module).yaw.getValue() == AntiAimYaw.STARE ? value : MathHelper.clamp(value, bounds[1], bounds[0]);
   }

   private float getSpinYaw() {
      float targetYaw = this.lastYaw + (float)(Integer)((AntiAim)this.module).spinSpeed.getValue();
      float wrapped = MathHelper.wrapDegrees(targetYaw);
      if (wrapped < -180.0F) {
         targetYaw = -180.0F;
      } else if (wrapped > 180.0F) {
         targetYaw = 180.0F;
      }

      return MathHelper.wrapDegrees(targetYaw);
   }
}
