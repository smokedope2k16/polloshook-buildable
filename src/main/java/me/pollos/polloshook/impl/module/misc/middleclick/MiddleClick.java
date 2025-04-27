package me.pollos.polloshook.impl.module.misc.middleclick;

import java.util.Arrays;
import java.util.List;
import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.api.value.value.constant.EnumValue;
import me.pollos.polloshook.impl.module.misc.middleclick.action.FireworkAction;
import me.pollos.polloshook.impl.module.misc.middleclick.action.FriendAction;
import me.pollos.polloshook.impl.module.misc.middleclick.action.PearlAction;
import me.pollos.polloshook.impl.module.misc.middleclick.action.actiontype.ActionType;
import me.pollos.polloshook.impl.module.misc.middleclick.action.core.MiddleClickAction;

public class MiddleClick extends ToggleableModule {
   protected final EnumValue<ActionType> priority;
   protected final Value<Boolean> noPickBlock;
   protected final Value<Boolean> throwPearls;
   protected final Value<Boolean> addFriends;
   protected final Value<Boolean> useFirework;
   protected List<MiddleClickAction> actions;

   public MiddleClick() {
      super(new String[]{"MiddleClick", "midclick", "mcf", "mcp"}, Category.MISC);
      this.priority = new EnumValue(ActionType.PEARL, new String[]{"Priority"});
      this.noPickBlock = new Value(false, new String[]{"NoPickBlock", "pickblock"});
      this.throwPearls = new Value(true, new String[]{"ThrowPearls", "pearls"});
      this.addFriends = new Value(true, new String[]{"AddFriends", "friends"});
      this.useFirework = new Value(false, new String[]{"UseFirework", "firework"});
      this.offerValues(new Value[]{this.priority, this.noPickBlock, this.throwPearls, this.addFriends, this.useFirework});
      this.offerListeners(new Listener[]{new ListenerMouse(this), new ListenerPickBlock(this)});
   }

   public void onLoad() {
      this.actions = Arrays.asList((new FireworkAction()).setParent(this.useFirework), (new PearlAction()).setParent(this.throwPearls), (new FriendAction()).setParent(this.addFriends));
   }
}
