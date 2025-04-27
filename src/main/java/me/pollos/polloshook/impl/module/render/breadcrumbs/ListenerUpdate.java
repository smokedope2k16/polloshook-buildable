package me.pollos.polloshook.impl.module.render.breadcrumbs;

import java.util.ArrayList;
import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.util.color.ColorUtil;
import me.pollos.polloshook.impl.events.update.UpdateEvent;
import me.pollos.polloshook.impl.module.render.breadcrumbs.util.TraceVectors;
import me.pollos.polloshook.impl.module.render.breadcrumbs.util.TrackedVertex;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.entity.projectile.thrown.EggEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.entity.projectile.thrown.ExperienceBottleEntity;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.util.math.Vec3d;

public class ListenerUpdate extends ModuleListener<BreadCrumbs, UpdateEvent> {
   public ListenerUpdate(BreadCrumbs module) {
      super(module, UpdateEvent.class);
   }

   public void call(UpdateEvent event) {
      mc.world.getEntities().forEach((entity) -> {
         if (entity.age > 1 && this.isValid(entity)) {
            this.trackPos(entity);
         }

      });
      ((BreadCrumbs)this.module).selfTracked.removeIf((timedVec3d) -> {
         Long time = timedVec3d.time();
         float alphaFactor = 5.0F * (Float)((BreadCrumbs)this.module).alphaFactor.getValue() / 10.0F;
         float factored = Math.max(0.1F, (Float)((BreadCrumbs)this.module).timeout.getValue() - alphaFactor);
         int alpha = (int)ColorUtil.fade((double)time, (double)(factored * 1000.0F));
         return alpha <= 0 || (float)(System.currentTimeMillis() - time) > factored * 1000.0F;
      });
      ((BreadCrumbs)this.module).thrownEntities.forEach((id, thrownEntity) -> {
         thrownEntity.vertices().removeIf((vertex) -> {
            return (float)(System.currentTimeMillis() - vertex.time()) > (Float)((BreadCrumbs)this.module).timeout.getValue() * 1000.0F;
         });
         if (thrownEntity.vertices().isEmpty()) {
            ((BreadCrumbs)this.module).thrownEntities.remove(id);
         }

      });
   }

   protected boolean isValid(Entity entity) {
      if (entity instanceof EnderPearlEntity && (Boolean)((BreadCrumbs)this.module).pearls.getValue()) {
         return true;
      } else if (entity instanceof ArrowEntity && (Boolean)((BreadCrumbs)this.module).arrows.getValue()) {
         return true;
      } else if ((Boolean)((BreadCrumbs)this.module).others.getValue() && (entity instanceof SnowballEntity || entity instanceof EggEntity || entity instanceof TridentEntity || entity instanceof PotionEntity)) {
         return true;
      } else {
         return entity instanceof ExperienceBottleEntity && (Boolean)((BreadCrumbs)this.module).bottles.getValue();
      }
   }

   protected void trackPos(Entity entity) {
      if (!((BreadCrumbs)this.module).thrownEntities.containsKey(entity.getId())) {
         boolean var10000;
         label17: {
            if (entity instanceof EnderPearlEntity) {
               EnderPearlEntity enderPearl = (EnderPearlEntity)entity;
               if (enderPearl.getOwner() != null && Managers.getFriendManager().isFriend(enderPearl.getOwner().getName().getString())) {
                  var10000 = true;
                  break label17;
               }
            }

            var10000 = false;
         }

         boolean isFriend = var10000;
         ArrayList<TraceVectors> list = new ArrayList();
         list.add(new TraceVectors(new Vec3d(entity.lastRenderX, entity.lastRenderY, entity.lastRenderZ), new Vec3d(entity.getX(), entity.getY(), entity.getZ()), System.currentTimeMillis()));
         ((BreadCrumbs)this.module).thrownEntities.put(entity.getId(), new TrackedVertex(list, isFriend));
      } else {
         ((TrackedVertex)((BreadCrumbs)this.module).thrownEntities.get(entity.getId())).vertices().add(new TraceVectors(new Vec3d(entity.lastRenderX, entity.lastRenderY, entity.lastRenderZ), new Vec3d(entity.getX(), entity.getY(), entity.getZ()), System.currentTimeMillis()));
      }

   }
}
