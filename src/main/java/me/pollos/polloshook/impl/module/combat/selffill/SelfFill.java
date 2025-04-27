package me.pollos.polloshook.impl.module.combat.selffill;

import java.util.List;
import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.block.BlockUtil;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.util.math.StopWatch;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.api.value.value.constant.EnumValue;
import me.pollos.polloshook.api.value.value.list.mode.ListEnum;
import me.pollos.polloshook.api.value.value.list.toggleable.item.ItemListValue;
import me.pollos.polloshook.api.value.value.list.toggleable.item.ToggleableItem;
import me.pollos.polloshook.impl.module.player.blink.Blink;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class SelfFill extends ToggleableModule {
   protected final EnumValue<ListEnum> selection;
   protected final Value<Boolean> swing;
   protected final Value<Boolean> rotate;
   protected final Value<Boolean> altSwap;
   protected final Value<Boolean> dynamic;
   protected final Value<Boolean> attack;
   protected final Value<Boolean> jumpDisable;
   protected final ItemListValue items;
   private final StopWatch timer;
   private double enablePosY;

   public SelfFill() {
      super(new String[]{"SelfFill", "burrow", "blocklag"}, Category.COMBAT);
      this.selection = new EnumValue(ListEnum.ANY, new String[]{"Selection", "whitelist", "blacklist"});
      this.swing = new Value(true, new String[]{"Swing", "punch"});
      this.rotate = new Value(true, new String[]{"Rotate"});
      this.altSwap = new Value(false, new String[]{"AltSwap", "alternativeswitch"});
      this.dynamic = new Value(false, new String[]{"Dynamic", "dynamo"});
      this.attack = new Value(false, new String[]{"Attack"});
      this.jumpDisable = new Value(false, new String[]{"JumpDisable", "disable"});
      this.items = new ItemListValue();
      this.timer = new StopWatch();
      this.offerValues(new Value[]{this.selection, this.swing, this.rotate, this.altSwap, this.dynamic, this.attack, this.jumpDisable, this.items});
      this.offerListeners(new Listener[]{new ListenerMotion(this)});
      this.initializeBlocks();
   }

   protected void onEnable() {
      if (mc.player != null) {
         this.enablePosY = mc.player.getY();
         Blink BLINK = (Blink)Managers.getModuleManager().get(Blink.class);
         if (BLINK.isEnabled()) {
            BLINK.toggle();
         }

      }
   }

   protected double getY(Entity entity) {
      double d = this.getY(entity, 2.1D, 10.0D, true);
      if (Double.isNaN(d)) {
         d = this.getY(entity, -3.0D, -10.0D, false);
         if (Double.isNaN(d) || mc.player.getY() < 0.0D) {
            return mc.player.getY() + 1.242610501394747D;
         }
      }

      return d;
   }

   protected double getY(Entity entity, double min, double max, boolean add) {
      double x = entity.getX();
      double y = entity.getY();
      double z = entity.getZ();
      boolean air = false;
      BlockPos last = null;
      double off = min;

      while(true) {
         if (add) {
            if (!(off < max)) {
               break;
            }
         } else if (!(off > max)) {
            break;
         }

         BlockPos pos = new BlockPos(MathHelper.floor(x), MathHelper.floor(y - off), MathHelper.floor(z));
         BlockState state = BlockUtil.getState(pos);
         if (state.blocksMovement() && state.getBlock() != Blocks.AIR) {
            air = false;
         } else {
            if (air) {
               if (add) {
                  return (double)pos.getY();
               }

               return (double)last.getY();
            }

            air = true;
         }

         last = pos;
         off = add ? ++off : --off;
      }

      return Double.NaN;
   }

   protected boolean isInsideBlock() {
      double x = mc.player.getX();
      double y = mc.player.getY() + 0.2D;
      double z = mc.player.getZ();
      return BlockUtil.getState(new BlockPos(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z))).blocksMovement();
   }

   protected void setEnablePosY() {
      if (this.timer.passed(250L)) {
         this.enablePosY = mc.player.getY();
         this.timer.reset();
      }

   }

   protected void handleJump() {
      if ((Boolean)this.jumpDisable.getValue() && mc.player.getY() > this.enablePosY) {
         this.setEnabled(false);
      }

   }

   private void initializeBlocks() {
      if (((List)this.items.getValue()).stream().noneMatch((o) -> {
         return ((ToggleableItem) o).getItem() == Items.OBSIDIAN;
      })) {
         ((List)this.items.getValue()).add(new ToggleableItem(Items.OBSIDIAN, true));
      }

      if (((List)this.items.getValue()).stream().noneMatch((o) -> {
         return ((ToggleableItem) o).getItem() == Items.ENDER_CHEST;
      })) {
         ((List)this.items.getValue()).add(new ToggleableItem(Items.ENDER_CHEST, true));
      }

   }
}