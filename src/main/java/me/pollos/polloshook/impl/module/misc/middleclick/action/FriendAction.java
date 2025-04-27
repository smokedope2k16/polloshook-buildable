package me.pollos.polloshook.impl.module.misc.middleclick.action;

import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.entity.EntityUtil;
import me.pollos.polloshook.impl.module.misc.middleclick.action.actiontype.ActionType;
import me.pollos.polloshook.impl.module.misc.middleclick.action.core.MiddleClickAction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

public class FriendAction extends MiddleClickAction {
   public FriendAction() {
      super(ActionType.FRIEND);
   }

   public boolean check() {
      return mc.targetedEntity == null ? false : mc.targetedEntity instanceof PlayerEntity;
   }

   public void run() {
      if (mc.targetedEntity != null) {
         HitResult var2 = mc.crosshairTarget;
         if (var2 instanceof EntityHitResult) {
            EntityHitResult res = (EntityHitResult)var2;
            Entity var3 = res.getEntity();
            if (var3 instanceof PlayerEntity) {
               PlayerEntity player = (PlayerEntity)var3;
               String name = EntityUtil.getName(player);
               if (Managers.getFriendManager().isFriend(name)) {
                  Managers.getFriendManager().removeFriend(name);
               } else {
                  Managers.getFriendManager().addFriend(name, name);
               }
            }
         }

      }
   }
}
