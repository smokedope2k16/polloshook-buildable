package me.pollos.polloshook.impl.module.movement.velocity;

import me.pollos.polloshook.api.command.core.Argument;
import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.CommandModule;
import me.pollos.polloshook.api.util.math.StopWatch;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.api.value.value.constant.EnumValue;
import me.pollos.polloshook.impl.module.movement.velocity.mode.GrimMode;
import me.pollos.polloshook.impl.module.movement.velocity.mode.VelocityMode;
import net.minecraft.util.Formatting;

public class Velocity extends CommandModule {
   protected final EnumValue<VelocityMode> mode;
   protected final EnumValue<GrimMode> grim;
   protected final Value<Boolean> stopInWalls;
   protected final Value<Boolean> serverPos;
   protected final Value<Boolean> clientRots;
   protected final Value<Boolean> lag;
   protected final NumberValue<Float> horizontal;
   protected final NumberValue<Float> vertical;
   protected final Value<Boolean> noPush;
   protected final Value<Boolean> fishingRod;
   protected boolean cancel;
   protected final StopWatch timer;

   public Velocity() {
      super(new String[]{"Velocity", "velo", "antikb"}, Category.MOVEMENT, new String[]{"VelocityPercentage", "Velo%", "VelocityPer"}, new Argument("[H%|V%]"));
      this.mode = new EnumValue(VelocityMode.NORMAL, new String[]{"Mode", "m", "type"});
      this.grim = (new EnumValue(GrimMode.CURRENT, new String[]{"Grim", "grimpos", "grimothy", "grimace"})).setParent(this.mode, VelocityMode.GRIM);
      this.stopInWalls = (new Value(false, new String[]{"StopInWalls", "walls", "wallpause"})).setParent(this.mode, VelocityMode.GRIM);
      this.serverPos = (new Value(false, new String[]{"ServerPos", "server"})).setParent(this.mode, VelocityMode.GRIM);
      this.clientRots = (new Value(false, new String[]{"ClientRotations", "clientrots"})).setParent(this.mode, VelocityMode.GRIM);
      this.lag = (new Value(false, new String[]{"Lag", "l"})).setParent(this.mode, VelocityMode.GRIM);
      this.horizontal = (new NumberValue(0.0F, 0.0F, 100.0F, 1.0F, new String[]{"Horizontal", "h"})).setParent(this.mode, VelocityMode.NORMAL).withTag("%");
      this.vertical = (new NumberValue(0.0F, 0.0F, 100.0F, 1.0F, new String[]{"Vertical", "v"})).setParent(this.mode, VelocityMode.NORMAL).withTag("%");
      this.noPush = new Value(false, new String[]{"NoPush", "noblockpush", "noentitypush"});
      this.fishingRod = new Value(true, new String[]{"FishingRod", "rod"});
      this.cancel = false;
      this.timer = new StopWatch();
      this.offerValues(new Value[]{this.mode, this.grim, this.stopInWalls, this.serverPos, this.clientRots, this.lag, this.horizontal, this.vertical, this.noPush, this.fishingRod});
      this.offerListeners(new Listener[]{new ListenerVelocity(this), new ListenerExplode(this), new ListenerEntityPush(this), new ListenerBlockPush(this), new ListenerPosLook(this), new ListenerFishingHook(this), new ListenerTick(this)});
   }

   protected String getTag() {
      return this.mode.getValue() == VelocityMode.NORMAL ? "H" + String.valueOf(this.horizontal.getValue()) + "%" + String.valueOf(Formatting.BOLD) + "|" + String.valueOf(Formatting.GRAY) + "V" + String.valueOf(this.vertical.getValue()) + "%" : "Grim";
   }

   public String onCommand(String[] args) {
      float percent;
      if (args.length == 2) {
         try { 
            percent = Float.parseFloat(args[1]);

            if (percent < 0.0F || percent > 100.0F) {
                 return "Percentages must be between 0 and 100.";
            }

            this.horizontal.setValue(percent); 
            this.vertical.setValue(percent); 
            return "Set horizontal & vertical velocity to -> %s".formatted(new Object[]{args[1]});
         } catch (NumberFormatException e) {
            return "Invalid number format for percentage: " + args[1];
         }
      } else if (args.length == 3) {
         try {
            percent = Float.parseFloat(args[1]);
            float percentV = Float.parseFloat(args[2]);

            if (percent < 0.0F || percent > 100.0F || percentV < 0.0F || percentV > 100.0F) {
                 return "Percentages must be between 0 and 100.";
            }

            this.horizontal.setValue(percent); 
            this.vertical.setValue(percentV); 
            return "Set horizontal velocity to [%s] & vertical velocity to [%s]".formatted(new Object[]{percent, percentV});
         } catch (NumberFormatException e) {
            return "Invalid number format for percentages.";
         }
      } else {
         return this.getInfo();
      }
   }
   protected boolean notFull() {
      return (Float)this.vertical.getValue() != 0.0F || (Float)this.horizontal.getValue() != 0.0F;
   }

   public boolean liquidPush() {
      return this.isEnabled() && (Boolean)this.noPush.getValue();
   }
}