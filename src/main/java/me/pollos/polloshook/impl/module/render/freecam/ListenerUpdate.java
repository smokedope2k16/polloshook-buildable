package me.pollos.polloshook.impl.module.render.freecam;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.movement.MovementUtil;
import me.pollos.polloshook.impl.events.update.UpdateEvent;
import me.pollos.polloshook.impl.module.misc.antiafk.AntiAFK;
import me.pollos.polloshook.impl.module.movement.autowalk.AutoWalk;
import me.pollos.polloshook.impl.module.render.freecam.entity.FreecamEntity;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.util.math.Vec3d;

public class ListenerUpdate extends ModuleListener<Freecam, UpdateEvent> {
   public ListenerUpdate(Freecam module) {
      super(module, UpdateEvent.class);
   }

   public void call(UpdateEvent event) {
      if (((Freecam)this.module).render != null && !(mc.currentScreen instanceof DeathScreen)) {
         ((Freecam)this.module).input.tick(false, 0.0F);
         this.tickFreecamEntity();
         AutoWalk AUTO_WALK = (AutoWalk)Managers.getModuleManager().get(AutoWalk.class);
         AntiAFK ANTI_AFK = (AntiAFK)Managers.getModuleManager().get(AntiAFK.class);
         if (!AUTO_WALK.isEnabled() && !ANTI_AFK.isEnabled()) {
            mc.player.forwardSpeed = 0.0F;
            mc.player.sidewaysSpeed = 0.0F;
            mc.player.setJumping(false);
         }

         if (mc.getCameraEntity() != ((Freecam)this.module).render) {
            mc.setCameraEntity(((Freecam)this.module).render);
         }

      } else {
         ((Freecam)this.module).setEnabled(false);
      }
   }

   private void tickFreecamEntity() {
      FreecamEntity render = ((Freecam)this.module).render;
      render.noClip = true;
      render.setAbsorptionAmount(mc.player.getAbsorptionAmount());
      render.setHealth(mc.player.getHealth());
      render.setAir(mc.player.getAir());
      render.getHungerManager().setFoodLevel(mc.player.getHungerManager().getFoodLevel());
      render.getHungerManager().setSaturationLevel(mc.player.getHungerManager().getSaturationLevel());
      render.setVelocity(Vec3d.ZERO);
      render.setMainArm(mc.player.getMainArm());
      render.hurtTime = mc.player.hurtTime;
      render.maxHurtTime = mc.player.maxHurtTime;
      render.setYaw(render.getYaw() % 360.0F);
      render.setPitch(render.getPitch() % 360.0F);
      render.prevYaw = render.getYaw();
      render.prevPitch = render.getPitch();

      for(render.prevHeadYaw = render.headYaw; render.getYaw() - render.prevYaw < -180.0F; render.prevYaw -= 360.0F) {
      }

      while(render.getYaw() - render.prevYaw >= 180.0F) {
         render.prevYaw += 360.0F;
      }

      while(render.getPitch() - render.prevPitch < -180.0F) {
         render.prevPitch -= 360.0F;
      }

      while(render.getPitch() - render.prevPitch >= 180.0F) {
         render.prevPitch += 360.0F;
      }

      while(render.headYaw - render.prevHeadYaw < -180.0F) {
         render.prevHeadYaw -= 360.0F;
      }

      while(render.headYaw - render.prevHeadYaw >= 180.0F) {
         render.prevHeadYaw += 360.0F;
      }

      render.lastRenderX = render.getX();
      render.lastRenderY = render.getY();
      render.lastRenderZ = render.getZ();
      render.prevX = render.getX();
      render.prevY = render.getY();
      render.prevZ = render.getZ();
      double[] dir = MovementUtil.strafe(render, ((Freecam)this.module).input, (double)(Float)((Freecam)this.module).horizontal.getValue());
      if (((Freecam)this.module).input.movementForward == 0.0F && ((Freecam)this.module).input.movementSideways == 0.0F) {
         render.setVelocity(0.0D, render.getVelocity().y, 0.0D);
      } else {
         render.setVelocity(dir[0], render.getVelocity().y, dir[1]);
      }

      Vec3d velocity = render.getVelocity();
      if (((Freecam)this.module).input.jumping) {
         render.setVelocity(velocity.add(0.0D, (double)(Float)((Freecam)this.module).vertical.getValue(), 0.0D));
      }

      if (((Freecam)this.module).input.sneaking) {
         render.setVelocity(velocity.add(0.0D, (double)(-(Float)((Freecam)this.module).vertical.getValue()), 0.0D));
      }

      render.setBoundingBox(render.getBoundingBox().offset(render.getVelocity()));
      ((Freecam)this.module).setPositionBB(render);
   }
}
