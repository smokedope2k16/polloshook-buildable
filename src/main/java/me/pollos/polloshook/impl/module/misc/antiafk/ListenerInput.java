package me.pollos.polloshook.impl.module.misc.antiafk;

import java.util.ArrayList;
import java.util.List;
import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.util.math.RandomUtil;
import me.pollos.polloshook.impl.events.keyboard.InputKeyDownEvent;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.util.math.MathHelper;

public class ListenerInput extends ModuleListener<AntiAFK, InputKeyDownEvent> {
   private int activeTicks = 0;
   private boolean flag = false;

   public ListenerInput(AntiAFK module) {
      super(module, InputKeyDownEvent.class);
   }

   public void call(InputKeyDownEvent event) {
      if (event.getInput().equals(((AntiAFK)this.module).input)) {
         if (((AntiAFK)this.module).timer.passed((long)(Integer)((AntiAFK)this.module).frequency.getValue() * 1000L + (long)RandomUtil.getRandom().nextInt(200, 3500))) {
            List<Runnable> actions = new ArrayList();
            if ((Boolean)((AntiAFK)this.module).walk.getValue()) {
               actions.add(() -> {
                  if (event.getBinding().equals(mc.options.forwardKey)) {
                     event.setPressed(true);
                  }

               });
            }

            if ((Boolean)((AntiAFK)this.module).jump.getValue()) {
               actions.add(() -> {
                  if (event.getBinding().equals(mc.options.jumpKey)) {
                     event.setPressed(true);
                  }

               });
            }

            if ((Boolean)((AntiAFK)this.module).sneak.getValue()) {
               actions.add(() -> {
                  if (event.getBinding().equals(mc.options.sneakKey)) {
                     event.setPressed(true);
                  }

               });
            }

            if ((Boolean)((AntiAFK)this.module).rotate.getValue()) {
               actions.add(() -> {
                  float amount = RandomUtil.getRandom().nextBoolean() ? -(Float)((AntiAFK)this.module).amount.getValue() : (Float)((AntiAFK)this.module).amount.getValue();
                  mc.player.setYaw(MathHelper.clamp(mc.player.getYaw() + amount, -180.0F, 180.0F));
                  mc.player.setPitch(MathHelper.clamp(mc.player.getPitch() + amount, -90.0F, 90.0F));
               });
            }

            if (!actions.isEmpty()) {
               int rnd = RandomUtil.getRandom().nextInt(actions.size());
               ((Runnable)actions.get(rnd)).run();
               ++this.activeTicks;
            } else {
               ((AntiAFK)this.module).timer.reset();
            }
         }

         if (this.activeTicks >= RandomUtil.getRandom().nextInt(25, 50)) {
            if (this.flag) {
               KeyBinding.setKeyPressed(mc.options.forwardKey.getDefaultKey(), this.flag = false);
            }

            ((AntiAFK)this.module).timer.reset();
            this.activeTicks = 0;
         }

      }
   }
}
