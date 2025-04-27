package me.pollos.polloshook.impl.module.movement.liquidspeed;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.entity.EntityUtil;
import me.pollos.polloshook.api.minecraft.entity.PlayerUtil;
import me.pollos.polloshook.api.minecraft.movement.MovementUtil;
import me.pollos.polloshook.api.minecraft.world.EnchantUtil;
import me.pollos.polloshook.asm.ducks.entity.IPlayerEntity;
import me.pollos.polloshook.impl.events.movement.MoveEvent;
import me.pollos.polloshook.impl.module.movement.elytrafly.ElytraFly;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;

public class ListenerMove extends ModuleListener<LiquidSpeed, MoveEvent> {
   public ListenerMove(LiquidSpeed module) {
      super(module, MoveEvent.class);
   }

   public void call(MoveEvent event) {
      IPlayerEntity clientPlayer = (IPlayerEntity)mc.player;
      if (clientPlayer != null && !clientPlayer.isFirstUpdate()) {
         if (!EntityUtil.isAboveWater(mc.player, mc.player.getY() - 0.8D) || mc.player.isSubmergedInWater() || PlayerUtil.isInLava()) {
            if (((LiquidSpeed)this.module).timer.passed(150L) && !clientPlayer.isFirstUpdate() && !PlayerUtil.isSpectator() && !mc.player.getAbilities().flying && PlayerUtil.isInLiquid() && !((ElytraFly)Managers.getModuleManager().get(ElytraFly.class)).isElytra()) {
               boolean lavaSpeedFlag = (Boolean)((LiquidSpeed)this.module).lava.getValue() && mc.player.isFallFlying() && (Boolean)((LiquidSpeed)this.module).elytraFly.getValue();
               if ((Boolean)((LiquidSpeed)this.module).ySpeed.getValue() && !lavaSpeedFlag) {
                  if (mc.player.input.sneaking && !mc.player.input.jumping) {
                     event.setY((double)(-(Float)((LiquidSpeed)this.module).heightDown.getValue()));
                  } else if (mc.player.input.jumping && !mc.player.input.sneaking) {
                     event.setY((double)(Float)((LiquidSpeed)this.module).heightUp.getValue());
                  }
               }

               double[] strafe;
               if ((Boolean)((LiquidSpeed)this.module).water.getValue() && mc.player.isSubmergedInWater() && !mc.player.isSwimming()) {
                  if ((Boolean)((LiquidSpeed)this.module).depthStrider.getValue() && EnchantUtil.getLevel(Enchantments.DEPTH_STRIDER, mc.player.getEquippedStack(EquipmentSlot.FEET)) > 0) {
                     return;
                  }

                  strafe = MovementUtil.strafe((double)((Float)((LiquidSpeed)this.module).waterSpeed.getValue() / 10.0F));
                  event.setXZ(strafe[0], strafe[1]);
               } else if ((Boolean)((LiquidSpeed)this.module).lava.getValue() && mc.player.isInLava()) {
                  if ((Boolean)((LiquidSpeed)this.module).elytraFly.getValue() && mc.player.isFallFlying()) {
                     strafe = MovementUtil.strafe((double)((Float)((LiquidSpeed)this.module).elytraSpeed.getValue() / 10.0F));
                     event.setXZ(strafe[0], strafe[1]);
                     if (!mc.player.input.sneaking && !mc.player.input.jumping) {
                        event.setY(0.0D);
                     } else if ((Boolean)((LiquidSpeed)this.module).ySpeed.getValue()) {
                        float speed = (Float)((LiquidSpeed)this.module).heightUp.getValue() + (Float)((LiquidSpeed)this.module).ySpeedBoost.getValue();
                        event.setY(mc.player.input.sneaking ? (double)(-speed) : (double)speed);
                     }

                     return;
                  }

                  strafe = MovementUtil.strafe((double)((Float)((LiquidSpeed)this.module).lavaSpeed.getValue() / 10.0F));
                  event.setXZ(strafe[0], strafe[1]);
               }

            }
         }
      }
   }
}