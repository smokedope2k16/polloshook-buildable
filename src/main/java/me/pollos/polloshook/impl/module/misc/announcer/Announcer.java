package me.pollos.polloshook.impl.module.misc.announcer;

import java.util.LinkedHashMap;
import java.util.Map.Entry;
import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.inventory.ItemUtil;
import me.pollos.polloshook.api.minecraft.network.NetworkUtil;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.util.logging.ClientLogger;
import me.pollos.polloshook.api.util.math.MathUtil;
import me.pollos.polloshook.api.util.math.RandomUtil;
import me.pollos.polloshook.api.util.math.StopWatch;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.StringValue;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.api.value.value.constant.EnumValue;
import me.pollos.polloshook.impl.module.misc.announcer.modes.AnnouncerAction;
import me.pollos.polloshook.impl.module.misc.announcer.modes.AnnouncerMode;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class Announcer extends ToggleableModule {
   protected final EnumValue<AnnouncerMode> mode;
   protected final Value<Boolean> blocks;
   protected final Value<Boolean> place;
   protected final Value<Boolean> eat;
   protected final Value<Boolean> attack;
   protected final Value<Boolean> worldTime;
   protected final NumberValue<Float> worldTimeDelay;
   protected final Value<Boolean> kills;
   protected final Value<Boolean> walk;
   protected final Value<Boolean> greeter;
   protected final Value<Boolean> join;
   protected final Value<Boolean> leave;
   protected final Value<Boolean> shuffle;
   protected final StringValue name;
   protected final Value<Boolean> green;
   protected final LinkedHashMap<AnnouncerAction, Float> queued;
   protected final StopWatch timer;
   protected final StopWatch worldTimer;
   protected final StopWatch walkTimer;
   protected String joinMessage;
   protected String leaveMessage;
   private String placeMessage;
   private String breakMessage;
   private String eatMessage;
   private String attackMessage;
   private String killMessage;
   private String walkMessage;
   protected Block brokenBlock;
   protected Block placeBlock;
   protected ItemStack foodStack;
   protected float walkDistance;
   protected String attackPlayer;
   protected String joinPlayer;
   protected String leavePlayer;
   protected String killPlayer;
   private final String[] joinMessages;
   private final String[] leaveMessages;

   public Announcer() {
      super(new String[]{"Announcer", "announce", "annoy"}, Category.MISC);
      this.mode = new EnumValue(AnnouncerMode.BROADCAST, new String[]{"Mode"});
      this.blocks = new Value(true, new String[]{"BlocksDestroyed", "breaking", "broken"});
      this.place = new Value(true, new String[]{"Place", "placing"});
      this.eat = new Value(true, new String[]{"Eat", "eating"});
      this.attack = new Value(true, new String[]{"Attack", "attacking"});
      this.worldTime = new Value(true, new String[]{"WorldTime", "world"});
      this.worldTimeDelay = (new NumberValue(60.0F, 30.0F, 300.0F, new String[]{"Delay", "del", "d"})).setParent(this.worldTime).withTag("second");
      this.kills = new Value(false, new String[]{"Kills", "kill"});
      this.walk = new Value(false, new String[]{"Walk", "step", "move"});
      this.greeter = new Value(true, new String[]{"Greeter", "welcomer"});
      this.join = (new Value(true, new String[]{"Join", "joins"})).setParent(this.greeter);
      this.leave = (new Value(true, new String[]{"Leave", "leaves"})).setParent(this.greeter);
      this.shuffle = (new Value(true, new String[]{"ShuffleMessages", "shuffle"})).setParent(this.greeter);
      this.name = new StringValue("<none>", new String[]{"Label", "name", "client"});
      this.green = (new Value(false, new String[]{"Green", "g"})).setParent(this.mode, AnnouncerMode.BROADCAST);
      this.queued = new LinkedHashMap();
      this.timer = new StopWatch();
      this.worldTimer = new StopWatch();
      this.walkTimer = new StopWatch();
      this.joinMessages = new String[]{"Good to see you, ", "Greetings, ", "Hello, ", "Howdy, ", "Hey, ", "Good evening, ", "What it do, ", "Wassup, ", "Sup, ", "Oh, You're back again ", "Good to see you again, ", "Nice to see you, ", "Aww, it's you "};
      this.leaveMessages = new String[]{"See you later, ", "Catch ya later, ", "See you next time, ", "Farewell, ", "Bye, ", "Good bye, ", "Later, ", "Bye nigga, ", "Cya, ", "Well, It was nice to have you here, ", "Bye, Bye ", "Hope you had keyCodec good time, "};
      this.offerValues(new Value[]{this.mode, this.blocks, this.place, this.eat, this.attack, this.worldTime, this.worldTimeDelay, this.kills, this.walk, this.greeter, this.join, this.leave, this.shuffle, this.name, this.green});
      this.offerListeners(new Listener[]{new ListenerUpdate(this), new ListenerBreakBlock(this), new ListenerFinishUse(this), new ListenerPlaceBlock(this), new ListenerJoin(this), new ListenerLeave(this), new ListenerMotion(this), new ListenerDeath(this), new ListenerAttack(this)});
   }

   protected String getTag() {
      return this.mode.getStylizedName();
   }

   protected void onEnable() {
      this.clear();
   }

   protected void onDisable() {
      this.clear();
   }

   public void onWorldLoad() {
      this.clear();
   }

   protected void clear() {
      this.walkDistance = 0.0F;
      this.foodStack = null;
      this.placeBlock = null;
      this.brokenBlock = null;
      this.worldTimer.reset();
      this.timer.reset();
   }

   protected boolean isValid(ItemStack stack) {
      return !stack.isEmpty() && ItemUtil.isFood(stack) && !stack.getItem().equals(Items.AIR);
   }

   protected void addEvent(AnnouncerAction type) {
      if (this.queued.containsKey(type)) {
         this.queued.put(type, (Float)this.queued.get(type) + 1.0F);
      } else {
         this.queued.put(type, 1.0F);
      }

   }

   protected void setStrings() {
      String ty = ((String)this.name.getValue()).equalsIgnoreCase("<none>") ? "" : " thanks to " + (String)this.name.getValue() + "!";
      this.eatMessage = "I just ate [count] [name]" + ty;
      this.placeMessage = "I just placed [count] [block]" + ty;
      this.breakMessage = "I just destroyed [count] [block]" + ty;
      this.attackMessage = "I just attacked [player]" + ty;
      this.killMessage = "( •_•)>⌐■-■ I just killed [player]" + ty + " (⌐■_■)";
      this.walkMessage = "I just walked [dist] blocks" + ty;
      this.setJoinAndLeaveMessages();
   }

   protected String getMessage(AnnouncerAction type, float count) {
      switch(type) {
         case BREAK:
            return this.breakMessage
                    .replace("[count]", String.valueOf((int) count))
                    .replace("[block]", this.brokenBlock.getName().getString())
                    .replace(".0", "");
         case PLACE:
            return this.placeMessage
                    .replace("[count]", String.valueOf((int) count))
                    .replace("[block]", this.placeBlock.getName().getString())
                    .replace(".0", "");
         case EAT:
            float eatenCount = (count == 2.0F ? 1.0F : count);
            return this.eatMessage
                    .replace("[count]", String.valueOf((int) eatenCount))
                    .replace("[name]", this.foodStack.getName().getString())
                    .replace(".0", "");
         case ATTACK:
            return this.attackMessage.replace("[player]", this.attackPlayer);
         case JOIN:
            return this.joinMessage.replace("[player]", this.joinPlayer);
         case LEAVE:
            return this.leaveMessage.replace("[player]", this.leavePlayer);
         case KILL:
            return this.killMessage.replace("[player]", this.killPlayer);
         case WALK:
            double roundedDistance = MathUtil.round((double)this.walkDistance, 1);
            return this.walkMessage
                    .replace("[dist]", String.valueOf(roundedDistance))
                    .replace(".0", "");
         default:
            throw new MatchException(null, null);
      }
   }


   protected void execute(Entry<AnnouncerAction, Float> entry) {
      switch((AnnouncerMode)this.mode.getValue()) {
      case BROADCAST:
         if ((Float)entry.getValue() <= 1.0F || entry.getKey() == AnnouncerAction.WALK && this.walkDistance <= 1.0F) {
            return;
         }

         String message = this.getMessage((AnnouncerAction)entry.getKey(), (Float)entry.getValue());
         if ((Boolean)this.green.getValue()) {
            message = "> " + message;
         }

         NetworkUtil.sendInChat(message);
         break;
      case CLIENTSIDE:
         ClientLogger.getLogger().log(this.getMessage((AnnouncerAction)entry.getKey(), (Float)entry.getValue()));
      }

   }

   private void setJoinAndLeaveMessages() {
      if ((Boolean)this.shuffle.getValue()) {
         String var10001;
         if (Managers.getFriendManager().isFriend(this.joinPlayer)) {
            var10001 = "My friend [player] joined the server";
         } else {
            String[] var1 = this.joinMessages;
            var10001 = var1[RandomUtil.getRandom().nextInt(this.joinMessages.length)] + "[player]";
         }

         this.joinMessage = var10001;
         var10001 = this.leaveMessages[RandomUtil.getRandom().nextInt(this.leaveMessages.length)];
         this.leaveMessage = var10001 + "[player]";
      } else {
         this.joinMessage = Managers.getFriendManager().isFriend(this.joinPlayer) ? "My friend [player] joined the server" : "[player] joined the server";
         this.leaveMessage = "[player] left the server";
      }

   }
}
