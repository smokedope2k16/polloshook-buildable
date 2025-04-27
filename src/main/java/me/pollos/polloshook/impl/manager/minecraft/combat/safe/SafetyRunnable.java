package me.pollos.polloshook.impl.manager.minecraft.combat.safe;

import java.util.ArrayList;
import java.util.Iterator;
import me.pollos.polloshook.api.interfaces.Minecraftable;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.block.BlockUtil;
import me.pollos.polloshook.api.minecraft.block.HoleUtil;
import me.pollos.polloshook.api.minecraft.entity.CombatUtil;
import me.pollos.polloshook.api.minecraft.entity.EntityUtil;
import me.pollos.polloshook.api.util.thread.Sphere;
import me.pollos.polloshook.api.util.thread.interfaces.SafeRunnable;
import me.pollos.polloshook.impl.manager.minecraft.combat.SafeManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.BlockPos.Mutable;

public class SafetyRunnable implements Minecraftable, SafeRunnable {
   private final SafeManager manager;
   private final Iterable<Entity> crystals;

   public SafetyRunnable(SafeManager manager, Iterable<Entity> crystals) {
      this.manager = manager;
      this.crystals = crystals;
   }

   public void runSafely() {
      ArrayList<EnderPearlEntity> pearls = new ArrayList();
      Iterator var2 = Managers.getEntitiesManager().getEntities().iterator();

      while(var2.hasNext()) {
         Entity entity = (Entity)var2.next();
         if (entity instanceof EnderPearlEntity) {
            EnderPearlEntity enderPearl = (EnderPearlEntity)entity;
            Entity owner = enderPearl.getOwner();
            if (owner != null) {
               Text ownerText = owner.getName();
               String ownerString = ownerText.getString();
               if (!Managers.getFriendManager().isFriend(ownerString) && owner != mc.player) {
                  pearls.add(enderPearl);
               }
            }
         }
      }

      if (!pearls.isEmpty()) {
         var2 = pearls.iterator();

         while(var2.hasNext()) {
            EnderPearlEntity pearl = (EnderPearlEntity)var2.next();
            if (pearl.distanceTo(mc.player) <= 8.0F) {
               this.markUnsafe();
               return;
            }
         }
      }

      float maxDamage = 4.0F;
      Iterator var17 = this.crystals.iterator();

      float damage;

      do {
         Entity entity;
         do {
            do {
               if (!var17.hasNext()) {
                  boolean fullArmor = true;
                  Iterator var20 = mc.player.getInventory().armor.iterator();

                  while(var20.hasNext()) {
                     ItemStack stack = (ItemStack)var20.next();
                     if (stack.isEmpty()) {
                        fullArmor = false;
                        break;
                     }
                  }

                  Vec3d playerVec = mc.player.getPos();
                  BlockPos position = BlockPos.ofFloored(playerVec.x, playerVec.y, playerVec.z);
                  if (fullArmor && (double)position.getY() == playerVec.y) {
                     boolean doubleHole = HoleUtil.isDoubleHole(position);
                     if (HoleUtil.isHole(position) || doubleHole) {
                        this.markSafe();
                        return;
                     }
                  }

                  if (this.manager.isSafe()) {
                     return;
                  }

                  BlockPos playerPos = BlockPos.ofFloored(Managers.getPositionManager().getVec());
                  int x = playerPos.getX();
                  int y = playerPos.getY();
                  int z = playerPos.getZ();
                  int maxRadius = Sphere.getRadius(6.0D);
                  Mutable pos = new Mutable();

                  for(int i = 1; i < maxRadius; ++i) {
                     Vec3i v = Sphere.get(i);
                     pos.set(x + v.getX(), y + v.getY(), z + v.getZ());
                     if (BlockUtil.canPlaceCrystal(pos, false)) {
                        damage = CombatUtil.getDamage(mc.player, mc.world, 6.0F, (double)((float)pos.getX() + 0.5F), (double)(pos.getY() + 1), (double)((float)pos.getZ() + 0.5F), false, true);
                        if (damage > 4.0F || (double)damage > (double)EntityUtil.getHealth(mc.player) - 1.0D) {
                           this.markUnsafe();
                           return;
                        }
                     }
                  }

                  this.markSafe();
                  return;
               }

               entity = (Entity)var17.next();
            } while(!(entity instanceof EndCrystalEntity));
         } while(!entity.isAlive());

         damage = CombatUtil.getDamage(mc.player, mc.world, 6.0F, entity.getX(), entity.getY(), entity.getZ(), false, true);
      } while(!(damage > 4.0F) && !((double)damage > (double)EntityUtil.getHealth(mc.player) - 1.0D));

      this.markUnsafe();
   }

   private void markSafe() {
      this.manager.setSafe(true);
   }

   private void markUnsafe() {
      this.manager.setSafe(false);
   }
}
