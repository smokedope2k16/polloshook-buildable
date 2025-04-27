package me.pollos.polloshook.impl.module.other.hud.elements.draggable.dotgod;

import me.pollos.polloshook.api.event.listener.SafeModuleListener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.block.BlockUtil;
import me.pollos.polloshook.api.minecraft.entity.EntityUtil;
import me.pollos.polloshook.api.minecraft.rotations.FacingUtil;
import me.pollos.polloshook.impl.events.update.UpdateEvent;
import me.pollos.polloshook.impl.module.combat.aura.Aura;
import me.pollos.polloshook.impl.module.combat.autocrystal.AutoCrystal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Direction;

public class ListenerUpdate extends SafeModuleListener<DotGod, UpdateEvent> {
   public ListenerUpdate(DotGod module) {
      super(module, UpdateEvent.class);
   }

   public void safeCall(UpdateEvent event) {
      Aura AURA_MODULE = (Aura)Managers.getModuleManager().get(Aura.class);
      AutoCrystal AUTOCRYSTAL_MODULE = (AutoCrystal)Managers.getModuleManager().get(AutoCrystal.class);
      ((DotGod)this.module).htrCheck = AURA_MODULE.isEnabled() && AURA_MODULE.getTarget() != null;
      ((DotGod)this.module).plrCheck = AUTOCRYSTAL_MODULE.isEnabled() && AUTOCRYSTAL_MODULE.getEnemy() != null && AUTOCRYSTAL_MODULE.getEnemy().distanceTo(mc.player) <= (Float)AUTOCRYSTAL_MODULE.getPlaceRange().getValue();
      PlayerEntity player = EntityUtil.getClosestEnemy();
      if (player == null) {
         ((DotGod)this.module).lbyCheck = false;
      } else {
         boolean isSurrounded = false;
         int count = 0;
         Direction[] var7 = FacingUtil.HORIZONTALS;
         int var8 = var7.length;

         for(int var9 = 0; var9 < var8; ++var9) {
            Direction dir = var7[var9];
            if (BlockUtil.getBlock(player.getBlockPos().offset(dir)).getDefaultState().blocksMovement()) {
               ++count;
            }

            isSurrounded = count >= 4;
         }

         ((DotGod)this.module).lbyCheck = EntityUtil.isTrapped(player) && isSurrounded;
      }
   }
}
