package me.pollos.polloshook.impl.module.player.fastplace;

import java.util.List;

import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.util.math.StopWatch;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.api.value.value.constant.EnumValue;
import me.pollos.polloshook.api.value.value.list.mode.ListEnum;
import me.pollos.polloshook.api.value.value.list.toggleable.item.ItemListValue;
import me.pollos.polloshook.api.value.value.parents.SupplierParent;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;

public class FastPlace extends ToggleableModule {
   protected final EnumValue<ListEnum> selection;
   protected final NumberValue<Integer> tickDelay;
   protected final NumberValue<Integer> blockDelay;
   protected final NumberValue<Float> startDelay;
   protected final Value<Boolean> ghostFix;
   protected final Value<Boolean> rotate;
   protected final Value<Boolean> safePearl;
   protected final Value<Boolean> entities;
   protected final Value<Boolean> fastDrop;
   protected final Value<Boolean> entireStack;
   protected final NumberValue<Integer> dropDelay;
   protected final Value<Boolean> noMineDelay;
   protected final ItemListValue items;
   protected final StopWatch timer;
   protected final StopWatch accurateTimer;

   public FastPlace() {
      super(new String[]{"FastPlace", "fastuse"}, Category.PLAYER);
      this.selection = new EnumValue(ListEnum.ANY, new String[]{"Selection", "selector", "whitelist", "blacklist"});
      this.tickDelay = new NumberValue(1, 0, 4, new String[]{"TickDelay", "tickdel", "tickd", "tick"});
      this.blockDelay = (new NumberValue(2, 0, 4, new String[]{"BlockTickDelay", "btickdel", "btickd"})).setParent(() -> {
         return this.blockDelayParent().isVisible();
      });
      this.startDelay = new NumberValue(0.0F, 0.0F, 1.0F, 0.01F, new String[]{"StartDelay", "startd"});
      this.ghostFix = new Value(true, new String[]{"GhostFix", "strict"});
      this.rotate = (new Value(true, new String[]{"GhostRotate", "rotatefix"})).setParent(this.ghostFix);
      this.safePearl = new Value(false, new String[]{"SafePearl", "safetypearl"});
      this.entities = (new Value(false, new String[]{"EntityResult", "entities"})).setParent(this.safePearl);
      this.fastDrop = new Value(false, new String[]{"FastDrop", "instantdrop"});
      this.entireStack = (new Value(false, new String[]{"EntireStack", "fullstack"})).setParent(this.fastDrop);
      this.dropDelay = (new NumberValue(2, 0, 10, new String[]{"DropDelay", "dropdel"})).setParent(this.entireStack, true);
      this.noMineDelay = new Value(false, new String[]{"NoMineDelay", "nobreakdelay"});
      this.items = new ItemListValue();
      this.timer = new StopWatch();
      this.accurateTimer = new StopWatch();
      this.offerValues(new Value[]{this.selection, this.tickDelay, this.blockDelay, this.startDelay, this.ghostFix, this.rotate, this.safePearl, this.entities, this.fastDrop, this.entireStack, this.dropDelay, this.noMineDelay, this.items});
      this.offerListeners(new Listener[]{new ListenerTick(this), new ListenerUseItem(this), new ListenerInteract(this)});
   }

   protected void onToggle() {
      this.timer.reset();
      this.accurateTimer.reset();
   }

   protected SupplierParent blockDelayParent() {
      return new SupplierParent(() -> {
         return ((List)this.items.getValue()).stream().anyMatch((i) -> {
            return ((ItemStack)i).getItem() instanceof BlockItem;
         }) || this.selection.getValue() == ListEnum.ANY;
      }, false);
   }

   
   public EnumValue<ListEnum> getSelection() {
      return this.selection;
   }

   
   public NumberValue<Integer> getTickDelay() {
      return this.tickDelay;
   }

   
   public NumberValue<Integer> getBlockDelay() {
      return this.blockDelay;
   }

   
   public NumberValue<Float> getStartDelay() {
      return this.startDelay;
   }

   
   public Value<Boolean> getGhostFix() {
      return this.ghostFix;
   }

   
   public Value<Boolean> getRotate() {
      return this.rotate;
   }

   
   public Value<Boolean> getSafePearl() {
      return this.safePearl;
   }

   
   public Value<Boolean> getEntities() {
      return this.entities;
   }

   
   public Value<Boolean> getFastDrop() {
      return this.fastDrop;
   }

   
   public Value<Boolean> getEntireStack() {
      return this.entireStack;
   }

   
   public NumberValue<Integer> getDropDelay() {
      return this.dropDelay;
   }

   
   public Value<Boolean> getNoMineDelay() {
      return this.noMineDelay;
   }

   
   public ItemListValue getItems() {
      return this.items;
   }

   
   public StopWatch getTimer() {
      return this.timer;
   }

   
   public StopWatch getAccurateTimer() {
      return this.accurateTimer;
   }
}
