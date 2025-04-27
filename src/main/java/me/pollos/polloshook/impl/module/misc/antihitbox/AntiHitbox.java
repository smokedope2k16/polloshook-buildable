package me.pollos.polloshook.impl.module.misc.antihitbox;

import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.api.value.value.constant.EnumValue;
import me.pollos.polloshook.api.value.value.list.mode.ListEnum;
import me.pollos.polloshook.api.value.value.list.toggleable.item.ItemListValue;

public class AntiHitbox extends ToggleableModule {
   protected final EnumValue<ListEnum> selection;
   protected final Value<Boolean> onlyBlock;
   protected final ItemListValue items;
   protected boolean isValid;

   public AntiHitbox() {
      super(new String[]{"AntiHitbox", "nohitbox", "hitbox"}, Category.MISC);
      this.selection = new EnumValue(ListEnum.ANY, new String[]{"Selection", "whitelist", "blacklist"});
      this.onlyBlock = new Value(false, new String[]{"OnlyBlock", "block"});
      this.items = new ItemListValue();
      this.isValid = false;
      this.offerValues(new Value[]{this.selection, this.onlyBlock, this.items});
      this.offerListeners(new Listener[]{new ListenerTick(this), new ListenerAntiHitbox(this)});
   }
}
