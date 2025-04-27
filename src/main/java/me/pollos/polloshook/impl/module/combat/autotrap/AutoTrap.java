package me.pollos.polloshook.impl.module.combat.autotrap;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.entity.EntityUtil;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.TrapModule;
import me.pollos.polloshook.api.util.math.MathUtil;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.api.value.value.constant.EnumValue;
import me.pollos.polloshook.api.value.value.targeting.TargetUtil;
import me.pollos.polloshook.impl.module.combat.autotrap.util.TrapTarget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

public class AutoTrap extends TrapModule {
   protected final NumberValue<Float> enemyRange = (new NumberValue(8.5F, 1.0F, 15.0F, 0.1F, new String[]{"EnemyRange", "targetrange"})).withTag("range");
   protected final EnumValue<TrapTarget> targetPriority;
   protected final Map<PlayerEntity, Double> speeds;
   protected final Map<PlayerEntity, List<BlockPos>> cached;
   protected PlayerEntity target;

   public AutoTrap() {
      super(new String[]{"AutoTrap", "instanttrap", "autotrap"}, Category.COMBAT);
      this.targetPriority = new EnumValue(TrapTarget.CLOSEST, new String[]{"Priority", "targeting"});
      this.speeds = new HashMap();
      this.cached = new HashMap();
      this.offerValues(new Value[]{this.enemyRange, this.targetPriority});
      this.offerListeners(new Listener[]{new ListenerMotion(this)});
      this.removeFeet();
   }

   protected void onToggle() {
      this.target = null;
      this.speeds.clear();
      this.cached.clear();
   }

   protected void removeFeet() {
      if (this.feet != null) {
         this.feet.setValue(false);
      }

      this.getValues().remove(this.feet);
   }

   protected List<PlayerEntity> getUnblockedEntities() {
      List<PlayerEntity> dudes = TargetUtil.getEnemies((double)(Float)this.enemyRange.getValue(), this::isValidSimple);
      dudes.removeIf((d) -> {
         return EntityUtil.isTrapped(d, !(Boolean)this.onlyHead.getValue());
      });
      return dudes;
   }

   protected boolean isValidSimple(PlayerEntity player) {
      return player != null && !EntityUtil.isDead(player) && !player.equals(mc.player) && !EntityUtil.getName(player).equals(EntityUtil.getName(mc.player)) && !Managers.getFriendManager().isFriend(player) && player.squaredDistanceTo(mc.player) <= (double)MathUtil.square((Float)this.enemyRange.getValue());
   }

   protected PlayerEntity smartCheck() {
      if (this.getUnblockedEntities().size() <= 1) {
         return null;
      } else {
         Iterator var1 = this.getUnblockedEntities().iterator();

         PlayerEntity player;
         do {
            if (!var1.hasNext()) {
               return null;
            }

            player = (PlayerEntity)var1.next();
         } while(!(player.distanceTo(mc.player) < 0.1F));

         return player;
      }
   }

   protected boolean isValid(PlayerEntity player) {
      if (this.isValidSimple(player)) {
         boolean cpvCheck = this.targetPriority.getValue() != TrapTarget.SMART || this.smartCheck() != player;
         if (Managers.getSpeedManager().getSpeed(player) <= 22.0D && cpvCheck) {
            if (this.targetPriority.getValue() != TrapTarget.UNTRAPPED && this.targetPriority.getValue() != TrapTarget.SMART) {
               return true;
            }

            List<BlockPos> positions = this.getBlocked(player);
            this.cached.put(player, positions);
            return positions.stream().anyMatch((pos) -> {
               return mc.world.getBlockState(pos).isReplaceable();
            });
         }
      }

      return false;
   }
}
