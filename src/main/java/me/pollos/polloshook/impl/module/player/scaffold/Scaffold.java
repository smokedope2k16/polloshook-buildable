package me.pollos.polloshook.impl.module.player.scaffold;

import java.util.ArrayList;
import java.util.List;
import me.pollos.polloshook.PollosHook;
import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.minecraft.inventory.InventoryUtil;
import me.pollos.polloshook.api.minecraft.movement.MovementUtil;
import me.pollos.polloshook.api.minecraft.render.RenderPosition;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.util.math.StopWatch;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.api.value.value.constant.EnumValue;
import me.pollos.polloshook.api.value.value.list.mode.ListEnum;
import me.pollos.polloshook.api.value.value.list.toggleable.item.ItemListValue;
import me.pollos.polloshook.impl.module.player.fastbreak.mode.SwapMode;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;

public class Scaffold extends ToggleableModule {
   protected final EnumValue<ListEnum> selection;
   protected final Value<Boolean> swing;
   protected final EnumValue<SwapMode> swap;
   protected final Value<Boolean> rotate;
   protected final Value<Boolean> tower;
   protected final Value<Boolean> strict;
   protected final Value<Boolean> safeWalk;
   protected final Value<Boolean> render;
   protected final NumberValue<Float> delay;
   protected final ItemListValue items;
   protected final List<RenderPosition> positionList;
   protected final StopWatch rotateTimer;
   protected final StopWatch placeTimer;

   public Scaffold() {
      super(new String[]{"Scaffold", "blockfly"}, Category.PLAYER);
      this.selection = new EnumValue(ListEnum.ANY, new String[]{"Selection", "selector", "whitelist", "blacklist"});
      this.swing = new Value(true, new String[]{"Swing", "punch"});
      this.swap = new EnumValue(SwapMode.SILENT, new String[]{"Swap", "s"});
      this.rotate = new Value(false, new String[]{"Rotate", "rotations"});
      this.tower = new Value(false, new String[]{"Tower", "t", "tow"});
      this.strict = new Value(false, new String[]{"Strict", "str", "alternative"});
      this.safeWalk = new Value(false, new String[]{"SafeWalk", "safe"});
      this.render = new Value(false, new String[]{"Render", "r", "debug"});
      this.delay = new NumberValue(0.8F, 0.1F, 2.5F, 0.1F, new String[]{"Delay", "del"});
      this.items = new ItemListValue();
      this.positionList = new ArrayList();
      this.rotateTimer = new StopWatch();
      this.placeTimer = new StopWatch();
      this.offerValues(new Value[]{this.selection, this.swing, this.swap, this.rotate, this.tower, this.strict, this.safeWalk, this.render, this.delay, this.items});
      this.offerListeners(new Listener[]{new ListenerMotion(this), new ListenerMove(this), new ListenerPush(this)});
      PollosHook.getEventBus().register(new ListenerRender(this));
   }

   protected boolean isTowering() {
      return (Boolean)this.tower.getValue() && mc.player.input.jumping && !mc.player.input.sneaking && !MovementUtil.isMoving();
   }

   protected int findItemSlot() {
      Item var2 = mc.player.getMainHandStack().getItem();
      if (var2 instanceof BlockItem) {
         BlockItem item = (BlockItem)var2;
         if (this.items.isValid(item, (EnumValue)this.selection)) {
            return mc.player.getInventory().selectedSlot;
         }
      }

      int slot = -1;

      for(int i = 9; i >= 0; --i) {
         Item var4 = InventoryUtil.getStack(i).getItem();
         if (var4 instanceof BlockItem) {
            BlockItem item = (BlockItem)var4;
            if (this.items.isValid(item, (EnumValue)this.selection)) {
               slot = i;
            }
         }
      }

      return slot;
   }

   protected BlockPos findNextPos() {
      BlockPos underPos = mc.player.getBlockPos().down();
      return mc.world.getBlockState(underPos).isReplaceable() && mc.world.getBlockState(underPos.up()).isReplaceable() ? underPos : null;
   }
}
