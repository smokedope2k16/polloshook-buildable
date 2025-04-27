package me.pollos.polloshook.impl.module.player.automine;

import java.util.Iterator;
import java.util.List;
import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.entity.EntityUtil;
import me.pollos.polloshook.api.minecraft.entity.PlayerUtil;
import me.pollos.polloshook.api.minecraft.movement.PositionUtil;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.util.logging.ClientLogger;
import me.pollos.polloshook.api.util.math.MathUtil;
import me.pollos.polloshook.api.value.value.list.mode.ListEnum;
import me.pollos.polloshook.api.value.value.targeting.TargetUtil;
import me.pollos.polloshook.impl.events.update.UpdateEvent;
import me.pollos.polloshook.impl.module.player.automine.util.AutoMineTarget;
import me.pollos.polloshook.impl.module.player.fastbreak.FastBreak;
import net.minecraft.block.AirBlock;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class ListenerUpdate extends ModuleListener<AutoMine, UpdateEvent> {
   public ListenerUpdate(AutoMine module) {
      super(module, UpdateEvent.class);
   }

   public void call(UpdateEvent event) {
      if (mc.player != null) {
         FastBreak FAST_BREAK = (FastBreak) Managers.getModuleManager().get(FastBreak.class);
         if (FAST_BREAK.getTimer().passed(250L)) {
            Vec3d playerVec = PositionUtil.getEyesPos();
            if (((AutoMine) this.module).attackPos != null) {
               if (playerVec.squaredDistanceTo(((AutoMine) this.module).attackPos.getPos()
                     .toCenterPos()) > (double) MathUtil.square((Float) ((AutoMine) this.module).range.getValue())) {
                  ((AutoMine) this.module).attackPos = null;
                  return;
               }

               if (playerVec.squaredDistanceTo(((AutoMine) this.module).attackPos.getPos()
                     .toCenterPos()) > (double) MathUtil.square(FAST_BREAK.getRange())) {
                  ((AutoMine) this.module).attackPos = null;
                  return;
               }
            }

            boolean check = FAST_BREAK.getPos() != null && !(FAST_BREAK.getBlock() instanceof AirBlock);
            if (!PlayerUtil.isCreative() && !PlayerUtil.isSpectator()
                  && (!check || ((AutoMine) this.module).attackPos != null
                        && ((AutoMine) this.module).attackPos.getPos().equals(FAST_BREAK.getPos()))) {
               if (!FAST_BREAK.isEnabled()) {
                  ClientLogger.getLogger().log(String.valueOf(Formatting.RED) + "Enable FastBreak to use this module");
                  ((AutoMine) this.module).setEnabled(false);
               } else if (FAST_BREAK.getSelection() != ListEnum.ANY
                     && (Boolean) ((AutoMine) this.module).enderChests.getValue()
                     && (((List) FAST_BREAK.getBlocks().getValue()).stream().anyMatch((tb) -> {
                        return ((FastBreak) tb).getBlock().equals(Blocks.ENDER_CHEST)
                              && ((ToggleableModule) tb).isEnabled();
                     }) && FAST_BREAK.getSelection() == ListEnum.BLACKLIST
                           || ((List) FAST_BREAK.getBlocks().getValue()).stream().noneMatch((tb) -> {
                              return ((FastBreak) tb).getBlock().equals(Blocks.ENDER_CHEST);
                           }) && FAST_BREAK.getSelection() == ListEnum.WHITELIST)) {
                  ClientLogger.getLogger()
                        .log(String.valueOf(Formatting.RED) + "WhiteList \"ender_chest\" in FastBreak to use this");
                  ((AutoMine) this.module).enderChests.setValue(false);
               } else {
                  boolean multiTask = PlayerUtil.isEating() || PlayerUtil.isDrinking() || PlayerUtil.isUsingBow();
                  if ((Boolean) ((AutoMine) this.module).multitask.getValue() || !multiTask) {
                     ((AutoMine) this.module).enemies.clear();
                     List<AutoMineTarget> targets = ((AutoMine) this.module).getSelfPositions();
                     List<PlayerEntity> enemies = TargetUtil.getEnemies(
                           (double) MathUtil.square((Float) ((AutoMine) this.module).enemyRange.getValue()));
                     Iterator var8 = enemies.iterator();

                     while (true) {
                        PlayerEntity target;
                        do {
                           do {
                              if (!var8.hasNext()) {
                                 ((AutoMine) this.module).enemies.addAll(enemies);
                                 if (targets.isEmpty()) {
                                    if ((Boolean) ((AutoMine) this.module).actualReset.getValue()) {
                                       ((AutoMine) this.module).attack();
                                       ((AutoMine) this.module).attackPos = null;
                                    }

                                    return;
                                 }

                                 ((AutoMine) this.module).attackBestPos(targets);
                                 return;
                              }

                              target = (PlayerEntity) var8.next();
                           } while (EntityUtil.isDead(target));

                           double y = (double) MathHelper.floor(target.getY());
                        } while (!EntityUtil.isSafe(target) && (Boolean) ((AutoMine) this.module).stopIfAir.getValue());

                        List<AutoMineTarget> positions = ((AutoMine) this.module).getPossiblePositions(target);
                        targets.addAll(positions);
                     }
                  }
               }
            }
         }
      }
   }
}