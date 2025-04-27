package me.pollos.polloshook.impl.module.combat.autoweb;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.entity.EntityUtil;
import me.pollos.polloshook.api.minecraft.inventory.ItemUtil;
import me.pollos.polloshook.api.minecraft.movement.PositionUtil;
import me.pollos.polloshook.api.module.BlockPlaceModule;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.util.math.MathUtil;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.api.value.value.constant.EnumValue;
import me.pollos.polloshook.asm.ducks.entity.ILivingEntity;
import me.pollos.polloshook.impl.module.combat.autotrap.util.TrapTarget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class AutoWeb extends BlockPlaceModule {
   protected final Value<Boolean> head = new Value(false, new String[]{"HeadPlace", "head"});
   protected final Value<Boolean> belowFeet = new Value(false, new String[]{"BelowFeet", "floor"});
   protected final Value<Boolean> extend = new Value(false, new String[]{"Extend", "extension"});
   protected final EnumValue<TrapTarget> targetPriority;
   protected final NumberValue<Float> enemyRange;
   protected List<BlockPos> posList;

   public AutoWeb() {
      super(new String[]{"AutoWeb", "webber"}, Category.COMBAT);
      this.targetPriority = new EnumValue(TrapTarget.CLOSEST, new String[]{"Priority", "targeting"});
      this.enemyRange = (new NumberValue(9.0F, 1.0F, 12.0F, 0.1F, new String[]{"EnemyRange", "targetrange"})).withTag("range");
      this.posList = new ArrayList();
      this.offerValues(new Value[]{this.head, this.belowFeet, this.extend, this.targetPriority, this.enemyRange});
      this.offerListeners(new Listener[]{new ListenerMotion(this)});
   }

   protected List<BlockPos> findTargets() {
      PlayerEntity target = this.calcTarget();
      List<BlockPos> posList = new ArrayList();
      return (List)(target == null ? posList : this.getPositions(target));
   }

   private PlayerEntity calcTarget() {
      PlayerEntity closest = null;
      double distance = Double.MAX_VALUE;
      Iterator var4 = mc.world.getPlayers().iterator();

      while(var4.hasNext()) {
         PlayerEntity player = (PlayerEntity)var4.next();
         double playerDist = mc.player.squaredDistanceTo(player);
         if (playerDist < distance && this.isValid(player)) {
            closest = player;
            distance = playerDist;
         }
      }

      return closest;
   }

   private boolean isValid(PlayerEntity player) {
      if (player != null && !EntityUtil.isDead(player) && !player.equals(mc.player) && !EntityUtil.getName(player).equals(EntityUtil.getName(mc.player)) && !Managers.getFriendManager().isFriend(player) && player.squaredDistanceTo(mc.player) <= (double)MathUtil.square((Float)this.enemyRange.getValue())) {
         if (this.targetPriority.getValue() == TrapTarget.UNTRAPPED) {
            List<BlockPos> positions = this.getPositions(player);
            return positions.stream().anyMatch((pos) -> {
               return mc.world.getBlockState(pos).isReplaceable();
            });
         } else {
            return true;
         }
      } else {
         return false;
      }
   }

   private List<BlockPos> getPositions(PlayerEntity player) {
      Box bb = player.getBoundingBox();
      BlockPos serverPos = player.getBlockPos();
      Vec3d interp = ((ILivingEntity)player).getServerVec();
      if (interp.x != 0.0D || interp.y != 0.0D || interp.z != 0.0D) {
         serverPos = BlockPos.ofFloored(interp);
         bb = ((ILivingEntity)player).getServerBoundingBox();
      }

      ArrayList blocks;
      if (!(Boolean)this.extend.getValue()) {
         blocks = new ArrayList();
         blocks.add(serverPos);
         if (!player.isCrawling() && (Boolean)this.head.getValue()) {
            blocks.add(serverPos.up());
         }

         if ((Boolean)this.belowFeet.getValue()) {
            blocks.add(serverPos.down());
         }

         return blocks;
      } else {
         blocks = new ArrayList(PositionUtil.getBlockedPositions(bb));
         List<BlockPos> positions = new ArrayList();
         if ((Boolean)this.head.getValue()) {
            blocks.forEach((pos) -> {
               positions.add(((BlockPos) pos).up());
            });
         }

         if ((Boolean)this.belowFeet.getValue()) {
            blocks.forEach((pos) -> {
               positions.add(((BlockPos) pos).down());
            });
         }

         blocks.addAll(positions);
         return blocks;
      }
   }

   protected int getSlot() {
      return ItemUtil.findHotbarItem(Items.COBWEB);
   }
}
