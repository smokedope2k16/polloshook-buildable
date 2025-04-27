package me.pollos.polloshook.api.value.value.targeting;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

import me.pollos.polloshook.api.interfaces.Minecraftable;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.impl.module.player.blink.Blink;
import me.pollos.polloshook.impl.module.player.fakeplayer.utils.FakePlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.AmbientEntity;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;

public final class TargetUtil implements Minecraftable {
   public static List<PlayerEntity> getEnemies(double targetRange) {
      return getEnemies(targetRange, (p) -> {
         return true;
      });
   }

   public static List<PlayerEntity> getEnemies(double targetRange, Predicate<PlayerEntity> isValid) {
      List<PlayerEntity> targets = new ArrayList();
      Iterator var4 = (new ArrayList(mc.world.getPlayers())).iterator();

      while(true) {
         PlayerEntity player;
         FakePlayerEntity fk;
         do {
            if (!var4.hasNext()) {
               return targets;
            }

            player = (PlayerEntity)var4.next();
            if (!(player instanceof FakePlayerEntity)) {
               break;
            }

            fk = (FakePlayerEntity)player;
         } while(fk.getLabel().equalsIgnoreCase(Blink.FAKE_PLAYER_LABEL));

         if (player != null && player.isAlive() && !player.equals(mc.player) && !Managers.getFriendManager().isFriend(player) && isValid.test(player)) {
            Vec3d pos = mc.player.getPos();
            double dist = player.squaredDistanceTo(pos.x, pos.y, pos.z);
            if (dist <= targetRange) {
               targets.add(player);
            }
         }
      }
   }

   public static PlayerEntity getEnemySimple(double targetRange) {
      PlayerEntity closest = null;
      double distance = targetRange;
      Iterator var5 = (new ArrayList(mc.world.getPlayers())).iterator();

      while(true) {
         PlayerEntity player;
         FakePlayerEntity fk;
         do {
            if (!var5.hasNext()) {
               return closest;
            }

            player = (PlayerEntity)var5.next();
            if (!(player instanceof FakePlayerEntity)) {
               break;
            }

            fk = (FakePlayerEntity)player;
         } while(fk.getLabel().equalsIgnoreCase(Blink.FAKE_PLAYER_LABEL));

         if (player != null && player.isAlive() && !player.equals(mc.player) && !Managers.getFriendManager().isFriend(player)) {
            Vec3d pos = mc.player.getPos();
            double dist = player.squaredDistanceTo(pos.x, pos.y, pos.z);
            if (dist <= distance) {
               closest = player;
               distance = dist;
            }
         }
      }
   }

   public static boolean isMonster(LivingEntity entity) {
      Angerable angerable;
      if (entity instanceof PassiveEntity && entity instanceof Angerable) {
         angerable = (Angerable)entity;
         return angerable.hasAngerTime();
      } else if (entity instanceof HostileEntity && entity instanceof Angerable) {
         angerable = (Angerable)entity;
         return angerable.hasAngerTime();
      } else {
         return entity instanceof HostileEntity;
      }
   }

   public static boolean isFriendly(Entity entity) {
      if (entity instanceof PassiveEntity) {
         return true;
      } else {
         return entity instanceof AmbientEntity || entity instanceof SquidEntity;
      }
   }

   public static boolean hasArmor(PlayerEntity player) {
      Iterator var1 = player.getArmorItems().iterator();

      ItemStack itemStack;
      do {
         if (!var1.hasNext()) {
            return false;
         }

         itemStack = (ItemStack)var1.next();
      } while(itemStack.isEmpty());

      return true;
   }

   
   private TargetUtil() {
      throw new UnsupportedOperationException("This is keyCodec utility class and cannot be instantiated");
   }
}
