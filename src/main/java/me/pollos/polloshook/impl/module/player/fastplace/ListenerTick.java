package me.pollos.polloshook.impl.module.player.fastplace;

import me.pollos.polloshook.api.event.listener.SafeModuleListener;
import me.pollos.polloshook.api.util.math.StopWatch;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.asm.ducks.IMinecraftClient;
import me.pollos.polloshook.asm.ducks.util.IKeybinding;
import me.pollos.polloshook.asm.ducks.world.IClientPlayerInteractionManager;
import me.pollos.polloshook.impl.events.update.TickEvent;
import net.minecraft.item.BlockItem;
import net.minecraft.util.Hand;

public class ListenerTick extends SafeModuleListener<FastPlace, TickEvent.Post> {
   protected final StopWatch dropTimer = (new StopWatch()).reset();

   public ListenerTick(FastPlace module) {
      super(module, TickEvent.Post.class);
   }

   public void safeCall(TickEvent.Post event) {
      if ((Boolean)((FastPlace)this.module).noMineDelay.getValue()) {
         ((IClientPlayerInteractionManager)mc.interactionManager).setBlockHitDelay(0);
      }

      if ((Boolean)((FastPlace)this.module).fastDrop.getValue() && mc.options.dropKey.isPressed() && !mc.player.getMainHandStack().isEmpty() && this.dropTimer.passed((long)(Integer)((FastPlace)this.module).dropDelay.getValue() * 25L)) {
         ((IKeybinding)mc.options.dropKey).setWasPressed(!(Boolean)((FastPlace)this.module).entireStack.getValue());
         mc.player.dropSelectedItem((Boolean)((FastPlace)this.module).entireStack.getValue());
         mc.player.swingHand(Hand.MAIN_HAND);
         this.dropTimer.reset();
      }

      if (((FastPlace)this.module).items.isValid(mc.player.getMainHandStack().getItem(), ((FastPlace)this.module).selection) || ((FastPlace)this.module).items.isValid(mc.player.getOffHandStack().getItem(), ((FastPlace)this.module).selection)) {
         if (!mc.options.useKey.isPressed()) {
            ((FastPlace)this.module).timer.reset();
            return;
         }

         if (!((FastPlace)this.module).timer.passed((double)((Float)((FastPlace)this.module).startDelay.getValue() * 1000.0F))) {
            return;
         }

         IMinecraftClient client = (IMinecraftClient)mc;
         boolean bl = mc.player.getMainHandStack().getItem() instanceof BlockItem || mc.player.getOffHandStack().getItem() instanceof BlockItem;
         NumberValue<Integer> delay = bl ? ((FastPlace)this.module).blockDelay : ((FastPlace)this.module).tickDelay;
         if ((Integer)delay.getValue() < client.getItemUseCooldown()) {
            client.setItemUseCooldown((Integer)((FastPlace)this.module).tickDelay.getValue());
         }
      }

   }
}
