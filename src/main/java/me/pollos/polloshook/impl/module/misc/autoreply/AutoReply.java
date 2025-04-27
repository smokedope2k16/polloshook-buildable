package me.pollos.polloshook.impl.module.misc.autoreply;

import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.util.obj.timedmessage.TimedMessageSystem;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.api.value.value.constant.EnumValue;
import me.pollos.polloshook.impl.module.misc.autoreply.modes.AutoReplyMode;

public class AutoReply extends ToggleableModule {
   protected final EnumValue<AutoReplyMode> mode;
   protected final NumberValue<Float> threshold;
   protected final NumberValue<Float> delay;
   protected final TimedMessageSystem system;

   public AutoReply() {
      super(new String[]{"AutoReply", "reply"}, Category.MISC);
      this.mode = new EnumValue(AutoReplyMode.COORDS, new String[]{"Mode", "message"});
      this.threshold = (new NumberValue(5.0F, 0.1F, 10.0F, 0.1F, new String[]{"Threshold", "distance"})).setParent(this.mode, AutoReplyMode.COORDS).withTag("thousand");
      this.delay = (new NumberValue(0.5F, 0.0F, 15.0F, 0.1F, new String[]{"Delay", "del"})).withTag("second");
      this.system = TimedMessageSystem.create(this, "autoreply_system").subscribe();
      this.offerValues(new Value[]{this.mode, this.threshold, this.delay});
      this.offerListeners(new Listener[]{new ListenerReceive(this)});
      this.system.setValue(this.delay);
   }

   protected String getTag() {
      return this.mode.getStylizedName();
   }
}
