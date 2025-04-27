package me.pollos.polloshook.impl.module.movement.step;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.network.PacketUtil;
import me.pollos.polloshook.impl.events.movement.StepEvent;
import me.pollos.polloshook.impl.module.movement.speed.Speed;
import me.pollos.polloshook.impl.module.movement.step.mode.StepMode;

public class ListenerStep extends ModuleListener<Step, StepEvent> {
   private boolean step;
   private double prevY = 0.0D;

   public ListenerStep(Step module) {
      super(module, StepEvent.class);
   }

   public void call(StepEvent event) {
      if (!((Step)this.module).timer.passed(100L)) {
         event.setHeight(0.6F);
      } else if (((Step)this.module).mode.getValue() == StepMode.VANILLA) {
         if (mc.player.isOnGround() && !mc.player.isClimbing() && !mc.player.input.jumping && mc.player.verticalCollision) {
            event.setHeight((Float)((Step)this.module).height.getValue());
         } else {
            event.setHeight(0.6F);
         }

      } else if (((Step)this.module).mode.getValue() != StepMode.NORMAL || !((Speed)Managers.getModuleManager().get(Speed.class)).isEnabled()) {
         double height = event.getBB().minY - this.prevY;
         switch(event.getStage()) {
         case PRE:
            this.prevY = mc.player.getY();
            if (this.canStep()) {
               this.step = true;
               event.setHeight((Float)((Step)this.module).height.getValue());
            } else {
               this.step = false;
            }
            break;
         case POST:
            if (!this.step) {
               return;
            }

            if (height <= 0.0D) {
               return;
            }

            if (height > (double)event.getHeight()) {
               mc.player.setPosition(mc.player.prevX, this.prevY, mc.player.prevZ);
               return;
            }

            double[] normalOffsets = new double[]{0.42D, height < 1.0D && height > 0.8D ? 0.753D : 0.75D, 1.0D, 1.16D, 1.23D, 1.2D};
            double[] _0_5 = new double[]{0.41999998688698D};
            double[] _1 = new double[]{0.41999998688698D, 0.7531999805212D};
            double[] _1_5 = new double[]{0.42D, 0.753D, 1.001D, 1.084D, 1.006D};
            double[] _1_625 = new double[]{0.425D, 0.821D, 0.699D, 0.599D, 1.022D, 1.372D};
            double[] _1_875 = new double[]{0.425D, 0.821D, 0.699D, 0.599D, 1.022D, 1.372D, 1.652D};
            double[] _2 = new double[]{0.425D, 0.821D, 0.699D, 0.599D, 1.022D, 1.372D, 1.652D, 1.869D};
            double[] _2_5 = new double[]{0.425D, 0.821D, 0.699D, 0.599D, 1.022D, 1.372D, 1.652D, 1.869D, 2.019D, 1.907D};
            switch((StepMode)((Step)this.module).mode.getValue()) {
            case ALTERNATIVE:
               if (height == 0.625D) {
                  this.step(_0_5);
               } else {
                  if (height != 1.0D && height != 0.875D && height != 1.0625D && height != 0.9375D) {
                     if (height == 1.5D) {
                        this.step(_1_5);
                     } else if (height == 1.875D) {
                        this.step(_1_875);
                     } else if (height == 1.625D) {
                        this.step(_1_625);
                     } else if (height == 2.0D) {
                        this.step(_2);
                     } else if (height == 2.5D) {
                        this.step(_2_5);
                     }
                     break;
                  }

                  this.step(_1);
               }
               break;
            case NORMAL:
               int max = height > 1.0D ? normalOffsets.length : 2;

               for(int n = 0; n < max; ++n) {
                  this.step(normalOffsets[n]);
               }
            }

            this.step = false;
         }

      }
   }

   private boolean canStep() {
      return !mc.player.isSubmergedInWater() && mc.player.isOnGround() && !mc.player.isClimbing() && !mc.player.input.jumping && mc.player.verticalCollision && (double)mc.player.fallDistance < 0.1D;
   }

   private void step(double[] offsets) {
      double[] var2 = offsets;
      int var3 = offsets.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         double dub = var2[var4];
         this.step(dub);
      }

   }

   private void step(double offset) {
      PacketUtil.move(mc.player.prevX, this.prevY + offset, mc.player.prevZ, false);
   }
}
