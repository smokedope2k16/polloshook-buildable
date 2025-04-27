package me.pollos.polloshook.impl.command.player;

import me.pollos.polloshook.PollosHook;
import me.pollos.polloshook.api.command.core.Argument;
import me.pollos.polloshook.api.command.core.Command;
import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.inventory.InventoryUtil;
import me.pollos.polloshook.api.minecraft.inventory.ItemUtil;
import me.pollos.polloshook.api.minecraft.network.PacketUtil;
import me.pollos.polloshook.api.util.math.MathUtil;
import me.pollos.polloshook.api.util.math.StopWatch;
import me.pollos.polloshook.asm.ducks.world.IClientPlayerInteractionManager;
import me.pollos.polloshook.impl.events.update.TickEvent;
import me.pollos.polloshook.impl.module.combat.autoarmour.AutoArmour;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;

public class FireworkCommand extends Command {
   private int clicks = 0;
   private int maxClicks = 0;
   private final StopWatch timer = new StopWatch();
   private final Listener<TickEvent> tickEventListener = new Listener<TickEvent>(TickEvent.class) {
      public void call(TickEvent event) {
         if (FireworkCommand.this.clicks >= FireworkCommand.this.maxClicks) {
            FireworkCommand.this.unregister();
         } else {
            int slot = ItemUtil.getHotbarItemSlot(Items.FIREWORK_ROCKET);
            boolean fast = false;
            if (slot == -1) {
               fast = true;

               for(int i = 35; i >= 9; --i) {
                  ItemStack stack = InventoryUtil.getStack(i);
                  if (stack.getItem() == Items.FIREWORK_ROCKET) {
                     slot = i;
                  }
               }

               if (slot == -1) {
                  FireworkCommand.this.unregister();
                  return;
               }
            }

            FireworkCommand.this.useFirework(fast, slot, false);
         }
      }
   };

   public FireworkCommand() {
      super(new String[]{"Firework", "rocket", "usefirework"}, new Argument("[amount]"));
   }

   public String execute(String[] args) {
      if (mc.player.isOnGround()) {
         this.unregister();
         return String.valueOf(Formatting.RED) + "Player must be in the air to use this";
      } else if (mc.player.getAbilities().flying) {
         this.unregister();
         return String.valueOf(Formatting.RED) + "Player must not be flying";
      } else if (mc.player.isTouchingWater()) {
         this.unregister();
         return String.valueOf(Formatting.RED) + "Player must not be in water";
      } else if (mc.player.getStatusEffect(StatusEffects.LEVITATION) != null) {
         this.unregister();
         return String.valueOf(Formatting.RED) + "Player must not have the levitation effect";
      } else {
         int elytraSlot = ItemUtil.findInInventory(Items.ELYTRA);
         if (mc.player.getEquippedStack(EquipmentSlot.CHEST).getItem() != Items.ELYTRA && elytraSlot != -1) {
            ((AutoArmour)Managers.getModuleManager().get(AutoArmour.class)).useElytra();
         }

         if (mc.player.getEquippedStack(EquipmentSlot.CHEST).getItem() == Items.ELYTRA && !mc.player.isFallFlying()) {
            mc.player.checkFallFlying();
            PacketUtil.send(new ClientCommandC2SPacket(mc.player, Mode.START_FALL_FLYING));
         }

         if (!mc.player.isFallFlying()) {
            return String.valueOf(Formatting.RED) + "Player must be fall flying";
         } else {
            int slot = ItemUtil.findHotbarItem(Items.FIREWORK_ROCKET);
            boolean fast = false;
            if (slot == -1) {
               fast = true;

               for(int i = 35; i >= 9; --i) {
                  ItemStack stack = InventoryUtil.getStack(i);
                  if (stack.getItem() == Items.FIREWORK_ROCKET) {
                     slot = i;
                  }
               }

               if (slot == -1) {
                  this.unregister();
                  return String.valueOf(Formatting.RED) + "No firework found";
               }
            }

            if (args.length <= 1) {
               this.useFirework(fast, slot, true);
               return "Used keyCodec firework";
            } else {
               String amountS = args[1];
               this.maxClicks = MathUtil.intFromString(amountS);
               PollosHook.getEventBus().register(this.tickEventListener);
               return "Attempting to use %s%s%s firework%s".formatted(new Object[]{Formatting.GREEN, this.maxClicks, Formatting.GRAY, this.maxClicks == 1 ? "" : "s"});
            }
         }
      }
   }

   private void unregister() {
      this.maxClicks = 0;
      this.clicks = 0;
      this.timer.reset();
      PollosHook.getEventBus().unregister(this.tickEventListener);
   }

   private void useFirework(boolean fast, int slot, boolean isOnly1) {
      int lastSlot = mc.player.getInventory().selectedSlot;
      if (this.timer.passed(250L) || isOnly1) {
         if (fast) {
            mc.interactionManager.clickSlot(0, slot, lastSlot, SlotActionType.SWAP, mc.player);
         } else {
            InventoryUtil.switchToSlot(slot);
         }

         ((IClientPlayerInteractionManager)mc.interactionManager).sendPacketWithSequence(mc.world, (sequence) -> {
            return new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, sequence, mc.player.getYaw(), mc.player.getPitch());
         });
         mc.player.swingHand(Hand.MAIN_HAND);
         if (fast) {
            mc.interactionManager.clickSlot(0, slot, lastSlot, SlotActionType.SWAP, mc.player);
         } else {
            InventoryUtil.switchToSlot(lastSlot);
         }

         this.timer.reset();
         ++this.clicks;
      }

   }
}