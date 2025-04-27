package me.pollos.polloshook.impl.module.player.fastbreak;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.awt.Color;
import java.util.Iterator;
import java.util.List;

import me.pollos.polloshook.PollosHook;
import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.event.events.Stage;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.block.BlockUtil;
import me.pollos.polloshook.api.minecraft.block.MineUtil;
import me.pollos.polloshook.api.minecraft.inventory.InventoryUtil;
import me.pollos.polloshook.api.minecraft.network.PacketUtil;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.util.logging.ClientLogger;
import me.pollos.polloshook.api.util.math.MathUtil;
import me.pollos.polloshook.api.util.math.StopWatch;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.api.value.value.constant.EnumValue;
import me.pollos.polloshook.api.value.value.list.mode.ListEnum;
import me.pollos.polloshook.api.value.value.list.toggleable.block.BlockListValue;
import me.pollos.polloshook.asm.ducks.world.IClientPlayerInteractionManager;
import me.pollos.polloshook.impl.events.block.SpeedMineEvent;
import me.pollos.polloshook.impl.events.render.RenderRotationsEvent;
import me.pollos.polloshook.impl.module.combat.autocrystal.AutoCrystal;
import me.pollos.polloshook.impl.module.combat.autocrystal.mode.Timing;
import me.pollos.polloshook.impl.module.player.fastbreak.mode.MineMode;
import me.pollos.polloshook.impl.module.player.fastbreak.mode.RenderMode;
import me.pollos.polloshook.impl.module.player.fastbreak.mode.SwapMode;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.OnGroundOnly;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class FastBreak extends ToggleableModule {
   protected final EnumValue<ListEnum> selection;
   protected final EnumValue<MineMode> mode;
   protected final NumberValue<Float> damage;
   protected final Value<Boolean> rotation;
   protected final NumberValue<Float> range;
   protected final EnumValue<SwapMode> swap;
   protected final Value<Boolean> forceDesync;
   protected final Value<Boolean> strict;
   protected final Value<Boolean> fast;
   protected final Value<Boolean> multiTask;
   protected final EnumValue<RenderMode> renderMode;
   protected final NumberValue<Integer> boxAlpha;
   protected final NumberValue<Integer> outlineAlpha;
   protected final NumberValue<Float> lineWidth;
   protected final Value<Boolean> damageColor;
   protected final Value<Boolean> drawCircle;
   protected final Value<Boolean> debug;
   protected final BlockListValue blocks;
   protected final StopWatch instantTimer;
   protected final StopWatch timer;
   protected final float[] damages;
   protected float maxDamage;
   protected boolean crystalAttack;
   protected boolean render;
   protected boolean shouldAbort;
   protected BlockPos pos;
   protected BlockState state;
   protected Direction direction;

   public FastBreak() {
      super(new String[]{"FastBreak", "speedmine", "fastmine"}, Category.PLAYER);
      this.selection = new EnumValue(ListEnum.ANY, new String[]{"Selection", "selector", "whitelist", "blacklist"});
      this.mode = new EnumValue(MineMode.PACKET, new String[]{"Mode", "m", "type"});
      this.damage = new NumberValue(1.0F, 0.7F, 1.0F, 0.1F, new String[]{"Damage", "maxDamage"});
      this.rotation = new Value(false, new String[]{"Rotations", "rotate"});
      this.range = (new NumberValue(4.5F, 0.1F, 6.0F, 0.1F, new String[]{"Range", "dist", "r"})).withTag("range");
      this.swap = new EnumValue(SwapMode.SILENT, new String[]{"Switch", "swap"});
      this.forceDesync = (new Value(false, new String[]{"ForceDesync", "onedottwelve", "1.12.2"})).setParent(this.swap, SwapMode.ALTERNATIVE);
      this.strict = (new Value(false, new String[]{"SwitchReset", "swapreset", "strict"})).setParent(() -> {
         return this.swap.getValue() != SwapMode.SILENT;
      });
      this.fast = (new Value(true, new String[]{"Fast", "f", "fat"})).setParent(this.mode, MineMode.PACKET);
      this.multiTask = new Value(true, new String[]{"MultiTask", "whilemining", "whileeating"});
      this.renderMode = new EnumValue(RenderMode.STATIC, new String[]{"Render", "rendermode"});
      this.boxAlpha = new NumberValue(30, 0, 255, new String[]{"BoxAlpha", "boxa"});
      this.outlineAlpha = new NumberValue(150, 0, 255, new String[]{"OutlineAlpha", "outlinea"});
      this.lineWidth = new NumberValue(1.3F, 1.0F, 5.0F, 0.1F, new String[]{"LineWidth", "wirewidth"});
      this.damageColor = new Value(false, new String[]{"DamageColor", "damagecolor", "damage"});
      this.drawCircle = new Value(false, new String[]{"DrawCircle", "circle"});
      this.debug = new Value(false, new String[]{"Debug", "debugger"});
      this.blocks = new BlockListValue();
      this.instantTimer = new StopWatch();
      this.timer = new StopWatch();
      this.damages = new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F};
      this.render = false;
      this.offerValues(new Value[]{this.selection, this.mode, this.damage, this.rotation, this.range, this.swap, this.forceDesync, this.strict, this.fast, this.multiTask, this.renderMode, this.boxAlpha, this.outlineAlpha, this.lineWidth, this.damageColor, this.drawCircle, this.debug, this.blocks});
      this.offerListeners(new Listener[]{new ListenerAttackBlock(this), new ListenerDamageBlock(this), new ListenerRender(this), new Listener2DRender(this), new ListenerUpdate(this), new ListenerBlockChange(this), new ListenerSwap(this), new ListenerLogout(this)});
      this.mode.addObserver((value) -> {
         this.reset();
      });
   }

   protected void onToggle() {
      this.reset();
   }

   protected String getTag() {
      return String.format("%.1f", this.maxDamage);
   }

   public Block getBlock() {
      return mc.world.getBlockState(this.pos).getBlock();
   }

   public float getRange() {
      return (Float)this.range.getValue();
   }

   public boolean isValid(BlockPos pos) {
      if (pos == null) {
         return false;
      } else if (BlockUtil.isAir(pos) && this.mode.getValue() == MineMode.INSTANT) {
         return true;
      } else {
         return BlockUtil.getBlock(pos) != Blocks.LAVA && BlockUtil.getBlock(pos) != Blocks.BEDROCK ? this.blocks.isValid(BlockUtil.getBlock(pos), this.selection) : false;
      }
   }

   public void abortCurrentPos() {
      PacketUtil.swing();
      PacketUtil.send(this.getAbortPacket());
      PacketUtil.swing();
      ((IClientPlayerInteractionManager)mc.interactionManager).setHittingPosBool(false);
      ((IClientPlayerInteractionManager)mc.interactionManager).setBreakingProgress(0.0F);
      mc.world.setBlockBreakingInfo(mc.player.getId(), this.pos, -1);
      this.reset();
      this.debugLog(String.valueOf(Formatting.RED) + "Aborting");
   }

   protected void reset() {
      this.pos = null;
      this.direction = null;
      this.maxDamage = 0.0F;
      this.crystalAttack = false;
      this.render = false;

      for(int i = 0; i < 9; ++i) {
         this.damages[i] = 0.0F;
      }

   }

   protected void softReset() {
      this.maxDamage = 0.0F;

      for(int i = 0; i < 9; ++i) {
         this.damages[i] = 0.0F;
      }

   }

   protected void updateDamages() {
      this.maxDamage = 0.0F;

      for(int i = 0; i < 9; ++i) {
         ItemStack stack = InventoryUtil.getStack(i);
         boolean onGround = (Boolean)this.fast.getValue() ? mc.player.isOnGround() : Managers.getPositionManager().isOnGround();
         float damage;
         if (this.getBlock() == Blocks.WATER && this.state != null) {
            damage = MineUtil.getDamage(this.state, stack, onGround);
         } else {
            damage = MineUtil.getDamage(stack, this.pos, onGround);
         }

         damage *= Managers.getTpsManager().getTps() / 20.0F;
         this.damages[i] = MathUtil.clamp(this.damages[i] + damage, 0.0F, (Float)this.damage.getValue());
         if (this.damages[i] > this.maxDamage) {
            this.maxDamage = this.damages[i];
         }
      }

   }

   protected void tryBreak(int pickSlot) {
      if (this.instantTimer.passed(250L) || this.mode.getValue() != MineMode.INSTANT) {
         AutoCrystal AUTO_CRYSTAL = (AutoCrystal)Managers.getModuleManager().get(AutoCrystal.class);
         float[] rotations = BlockUtil.getBlockPosRotations(this.pos);
         if (this.getBlock() != Blocks.WATER) {
            if (AUTO_CRYSTAL.getTiming().getValue() == Timing.STRICT) {
               AUTO_CRYSTAL.setLock(true);
            }

            SpeedMineEvent pre = new SpeedMineEvent(this.pos, Stage.PRE);
            PollosHook.getEventBus().dispatch(pre);
            int lastSlot = mc.player.getInventory().selectedSlot;
            float[] oldRots = new float[]{mc.player.getYaw(), mc.player.getPitch()};
            if ((Boolean)this.rotation.getValue()) {
               PacketUtil.rotate(rotations, Managers.getPositionManager().isOnGround());
               PollosHook.getEventBus().dispatch(new RenderRotationsEvent(rotations));
            }

            PacketUtil.send(new OnGroundOnly(true));
            ItemStack oldItem = mc.player.getStackInHand(Hand.MAIN_HAND);
            AutoCrystal.SWITCH_LOCK = true;
            if (this.swap.getValue() != SwapMode.HOLD) {
               this.swap(pickSlot);
            }

            ItemStack newItem = mc.player.getStackInHand(Hand.MAIN_HAND);
            this.debugLog(String.valueOf(Formatting.AQUA) + "Mining current position");
            this.instantTimer.reset();
            PacketUtil.swing();
            PacketUtil.send(new PlayerActionC2SPacket(Action.STOP_DESTROY_BLOCK, this.pos, this.direction, PacketUtil.incrementSequence()));
            PacketUtil.swing();
            PacketUtil.swing();
            PacketUtil.send(this.getAbortPacket());
            PacketUtil.swing();
            PacketUtil.swing();
            if (this.mode.getValue() == MineMode.PACKET) {
               PacketUtil.swing();
               PacketUtil.send(new PlayerActionC2SPacket(Action.START_DESTROY_BLOCK, this.pos, this.direction, PacketUtil.incrementSequence()));
               PacketUtil.swing();
               if ((Boolean)this.fast.getValue()) {
                  PacketUtil.swing();
                  PacketUtil.send(new PlayerActionC2SPacket(Action.STOP_DESTROY_BLOCK, this.pos, this.direction, PacketUtil.incrementSequence()));
                  PacketUtil.swing();
               }
            }

            if (this.swap.getValue() != SwapMode.HOLD) {
               if (this.swap.getValue() != SwapMode.ALTERNATIVE) {
                  this.swap(lastSlot);
               } else if (!(Boolean)this.forceDesync.getValue()) {
                  this.swap(pickSlot);
               } else {
                  ItemStack fakeStack = new ItemStack(Items.END_CRYSTAL, 64);
                  int slot = InventoryUtil.hotbarToInventory(pickSlot);
                  int oldSlot = InventoryUtil.hotbarToInventory(lastSlot);
                  Slot currentSlot = (Slot)mc.player.currentScreenHandler.slots.get(oldSlot);
                  Slot swapSlot = (Slot)mc.player.currentScreenHandler.slots.get(slot);
                  DefaultedList<Slot> defaultedList = mc.player.currentScreenHandler.slots;
                  int i = defaultedList.size();
                  List<ItemStack> list = Lists.newArrayListWithCapacity(i);
                  Iterator var17 = defaultedList.iterator();

                  while(var17.hasNext()) {
                     Slot slot1 = (Slot)var17.next();
                     list.add(slot1.getStack().copy());
                  }

                  mc.player.currentScreenHandler.onSlotClick(slot, mc.player.getInventory().selectedSlot, SlotActionType.SWAP, mc.player);
                  Int2ObjectMap<ItemStack> int2ObjectMap = new Int2ObjectOpenHashMap();

                  for(int j = 0; j < i; ++j) {
                     ItemStack itemStack = (ItemStack)list.get(j);
                     ItemStack itemStack2 = ((Slot)defaultedList.get(j)).getStack();
                     if (!ItemStack.areEqual(itemStack, itemStack2)) {
                        int2ObjectMap.put(j, itemStack2.copy());
                     }
                  }

                  PacketUtil.send(new ClickSlotC2SPacket(0, mc.player.currentScreenHandler.getRevision(), slot, mc.player.getInventory().selectedSlot, SlotActionType.SWAP, fakeStack, int2ObjectMap));
                  currentSlot.insertStack(oldItem);
                  swapSlot.insertStack(newItem);
               }
            }

            AutoCrystal.SWITCH_LOCK = false;
            if ((Boolean)this.rotation.getValue()) {
               PacketUtil.rotate(oldRots, true);
            }

            if (this.mode.getValue() == MineMode.PACKET) {
               this.softReset();
            }

            SpeedMineEvent post = new SpeedMineEvent(this.pos, Stage.POST);
            PollosHook.getEventBus().dispatch(post);
         }

      }
   }

   private void swap(int slot) {
      if (this.swap.getValue() == SwapMode.ALTERNATIVE) {
         InventoryUtil.altSwap(slot);
      } else {
         InventoryUtil.switchToSlot(slot);
      }

   }

   protected PlayerActionC2SPacket getAbortPacket() {
      PlayerActionC2SPacket packet = new PlayerActionC2SPacket(Action.ABORT_DESTROY_BLOCK, this.pos, this.direction);
      return this.mode.getValue() == MineMode.INSTANT ? packet : new PlayerActionC2SPacket(Action.ABORT_DESTROY_BLOCK, mc.player.getBlockPos().up().up().east().east().south().south(), Direction.UP, PacketUtil.incrementSequence());
   }

   protected Color getProgressColor() {
      if ((Boolean)this.damageColor.getValue()) {
         float damage = Math.min(this.maxDamage, (Float)this.damage.getValue());
         float red = 255.0F - 255.0F * damage;
         float green = 255.0F * damage;
         return new Color((int)red, (int)green, 0);
      } else {
         return this.isRender() ? Color.GREEN : Color.RED;
      }
   }

   protected void debugLog(String s) {
      if ((Boolean)this.debug.getValue()) {
         ClientLogger.getLogger().log("<FastBreak> " + s, false);
      }
   }

   public ListEnum getSelection() {
      return (ListEnum)this.selection.getValue();
   }

   public MineMode getMode() {
      return (MineMode)this.mode.getValue();
   }

   public RenderMode getRenderMode() {
      return (RenderMode)this.renderMode.getValue();
   }

   public boolean isStrict() {
      return (Boolean)this.strict.getValue();
   }

   
   public BlockListValue getBlocks() {
      return this.blocks;
   }

   
   public StopWatch getTimer() {
      return this.timer;
   }

   
   public float getMaxDamage() {
      return this.maxDamage;
   }

   
   public boolean isRender() {
      return this.render;
   }

   
   public BlockPos getPos() {
      return this.pos;
   }
}
