package me.pollos.polloshook.impl.manager.minecraft.server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.event.bus.SubscriberImpl;
import me.pollos.polloshook.api.interfaces.Minecraftable;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.util.math.TimeVec3d;
import me.pollos.polloshook.asm.ducks.entity.ILivingEntity;
import me.pollos.polloshook.asm.ducks.entity.IPlayerEntity;
import me.pollos.polloshook.impl.events.entity.EntityInterpolationEvent;
import me.pollos.polloshook.impl.events.update.TickEvent;
import me.pollos.polloshook.impl.module.player.fakeplayer.utils.FakePlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class ExtrapolationManager extends SubscriberImpl implements Minecraftable {
   private final ConcurrentHashMap<PlayerEntity, TimeVec3d> extrapolated = new ConcurrentHashMap();

   public ExtrapolationManager() {
      this.listeners.add(new Listener<TickEvent>(TickEvent.class) {
         public void call(TickEvent event) {
            Iterator var2 = ExtrapolationManager.this.extrapolated.entrySet().iterator();

            while(var2.hasNext()) {
               Entry<PlayerEntity, TimeVec3d> entry = (Entry)var2.next();
               if (System.currentTimeMillis() - ((TimeVec3d)entry.getValue()).getTime() > 150L) {
                  ExtrapolationManager.this.extrapolated.remove(entry.getKey());
                  ExtrapolationManager.this.reset((PlayerEntity)entry.getKey());
               }
            }

            if (mc.world != null) {
               mc.executeSync(() -> {
                  ExtrapolationManager.this.extrapolatePlayers();
               });
            }

         }
      });
      this.listeners.add(new Listener<EntityInterpolationEvent>(EntityInterpolationEvent.class) {
         public void call(EntityInterpolationEvent event) {
            LivingEntity var3 = event.getEntity();
            if (var3 instanceof PlayerEntity) {
               PlayerEntity player = (PlayerEntity)var3;
               Vec3d prev = ((ILivingEntity)player).getServerVec();
               Vec3d move = new Vec3d(event.getX() - prev.x, event.getY() - prev.y, event.getZ() - prev.z);
               boolean reset = Double.compare(move.lengthSquared(), 0.0D) == 0 || move.lengthSquared() > MathHelper.square(3.0D);
               if (reset) {
                  ExtrapolationManager.this.extrapolated.remove(player);
                  return;
               }

               if (ExtrapolationManager.this.extrapolated.containsKey(player)) {
                  ExtrapolationManager.this.extrapolated.replace(player, new TimeVec3d(move));
               } else {
                  ExtrapolationManager.this.extrapolated.put(player, new TimeVec3d(move));
               }
            }

         }
      });
   }

   private void reset(PlayerEntity player) {
      Vec3d[] vec3ds = ((IPlayerEntity)player).getPredictedPositions();

      for(int i = 0; i < vec3ds.length; ++i) {
         vec3ds[i] = ((ILivingEntity)player).getServerVec();
      }

   }

   public void extrapolatePlayers() {
      Iterator var1 = (new ArrayList(Managers.getEntitiesManager().getPlayers())).iterator();

      while(true) {
         PlayerEntity player;
         FakePlayerEntity fake;
         Vec3d extrapolatedVec;
         do {
            if (!var1.hasNext()) {
               return;
            }

            player = (PlayerEntity)var1.next();
            fake = new FakePlayerEntity(mc.world, player.getGameProfile(), player.getName().getString());
            fake.noClip = false;
            fake.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.6000000238418579D);
            fake.copyPositionAndRotation(player);
            fake.setOnGround(player.isOnGround());
            Vec3d interp = ((ILivingEntity)player).getServerVec();
            if (interp.x != 0.0D || interp.y != 0.0D || interp.z != 0.0D) {
               fake.setPosition(interp);
            }

            extrapolatedVec = (Vec3d)this.extrapolated.get(player);
         } while(extrapolatedVec == null);

         Vec3d[] predicted = ((IPlayerEntity)player).getPredictedPositions();

         for(int i = 0; i < 9; ++i) {
            fake.move(MovementType.SELF, new Vec3d(extrapolatedVec.x, extrapolatedVec.y, extrapolatedVec.z));
            predicted[i] = fake.getPos();
         }
      }
   }
}