package me.pollos.polloshook.impl.module.misc.packetlogger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import me.pollos.polloshook.PollosHook;
import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.minecraft.network.PacketRegistry;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.util.logging.ClientLogger;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.api.value.value.constant.EnumValue;
import me.pollos.polloshook.impl.config.base.AbstractConfig;
import me.pollos.polloshook.impl.events.network.PacketEvent;
import me.pollos.polloshook.impl.module.misc.packetlogger.util.Logging;
import me.pollos.polloshook.impl.module.misc.packetlogger.util.PacketType;

public class PacketLogger extends ToggleableModule {
   protected final EnumValue<Logging> logging;
   protected final EnumValue<PacketType> type;
   protected final Value<Boolean> logAll;
   protected final Value<Boolean> cancel;
   protected final Value<Boolean> blockUpdateS2CPacket;
   protected final Value<Boolean> keepAliveS2CPacket;
   protected final Value<Boolean> commandSuggestionsS2CPacket;
   protected final Value<Boolean> playerActionResponsePacket;
   protected final Value<Boolean> updateSelectedSlotPacket;
   protected final Value<Boolean> clickSlotC2SPacket;
   protected final Value<Boolean> handSwingC2SPacket;
   protected final Value<Boolean> keepAliveC2SPacket;
   protected final Value<Boolean> playerActionC2SPacket;
   protected final Value<Boolean> playerMoveC2SPacket;
   protected final Value<Boolean> requestCommandCompletionsC2SPacket;
   protected final List<PacketLogger.PlayerMoveValue> playerMoveValues;
   protected final List<PacketLogger.PlayerActionValue> playerActionValues;
   protected final File PACKET_LOG;
   private BufferedWriter writer;
   final PacketLogger.PlayerActionValue dropItem;
   final PacketLogger.PlayerActionValue dropAllItems;
   final PacketLogger.PlayerActionValue releaseUseItem;
   final PacketLogger.PlayerActionValue stopDestroyBlock;
   final PacketLogger.PlayerActionValue abortDestroyBlock;
   final PacketLogger.PlayerActionValue startDestroyBlock;
   final PacketLogger.PlayerActionValue swapItemWithOffhand;
   final PacketLogger.PlayerMoveValue onGroundOnly;
   final PacketLogger.PlayerMoveValue lookAndOnGround;
   final PacketLogger.PlayerMoveValue positionAndOnGround;
   final PacketLogger.PlayerMoveValue full;

   public PacketLogger() {
      super(new String[]{"PacketLogger", "packetlog", "packetprinter", "logger", "loggatron"}, Category.MISC);
      this.logging = new EnumValue(Logging.FILE, new String[]{"Mode", "logging"});
      this.type = new EnumValue(PacketType.CLIENT, new String[]{"LogType", "type"});
      this.logAll = new Value(false, new String[]{"LogEveryPacket", "everypacket"});
      this.cancel = new Value(false, new String[]{"LogCanceledValue", "cancel"});
      this.blockUpdateS2CPacket = new Value(false, new String[]{"BlockUpdateS2CPacket"});
      this.keepAliveS2CPacket = new Value(false, new String[]{"KeepAliveS2CPacket"});
      this.commandSuggestionsS2CPacket = new Value(false, new String[]{"CommandSuggestionsS2CPacket"});
      this.playerActionResponsePacket = new Value(false, new String[]{"PlayerActionResponseS2CPacket"});
      this.updateSelectedSlotPacket = new Value(false, new String[]{"UpdateSelectedSlotC2SPacket"});
      this.clickSlotC2SPacket = new Value(false, new String[]{"ClickSlotC2SPacket"});
      this.handSwingC2SPacket = new Value(false, new String[]{"HandSwingC2SPacket"});
      this.keepAliveC2SPacket = new Value(false, new String[]{"KeepAliveC2SPacket"});
      this.playerActionC2SPacket = new Value(false, new String[]{"PlayerActionC2SPacket"});
      this.playerMoveC2SPacket = new Value(false, new String[]{"PlayerMoveC2SPacket"});
      this.requestCommandCompletionsC2SPacket = new Value(false, new String[]{"RequestCommandCompletionsC2SPacket"});
      this.playerMoveValues = new ArrayList();
      this.playerActionValues = new ArrayList();
      this.PACKET_LOG = new File(PollosHook.PACKETS, "packet_log.txt");
      this.dropItem = new PacketLogger.PlayerActionValue(this, new String[]{"Drop Item", "dropitem"});
      this.dropAllItems = new PacketLogger.PlayerActionValue(this, new String[]{"Drop All Items", "dropallitems"});
      this.releaseUseItem = new PacketLogger.PlayerActionValue(this, new String[]{"Release Use Item", "releaseuseitem"});
      this.stopDestroyBlock = new PacketLogger.PlayerActionValue(this, new String[]{"Stop Destroy Block", "stopdestroyblock"});
      this.abortDestroyBlock = new PacketLogger.PlayerActionValue(this, new String[]{"Abort Destroy Block", "abortdestroyblock"});
      this.startDestroyBlock = new PacketLogger.PlayerActionValue(this, new String[]{"Start Destroy Block", "startdestroyblock"});
      this.swapItemWithOffhand = new PacketLogger.PlayerActionValue(this, new String[]{"Swap Item With Offhand", "swapitemwithoffhand"});
      this.onGroundOnly = new PacketLogger.PlayerMoveValue(this, new String[]{"OnGroundOnly"});
      this.lookAndOnGround = new PacketLogger.PlayerMoveValue(this, new String[]{"LookAndOnGround"});
      this.positionAndOnGround = new PacketLogger.PlayerMoveValue(this, new String[]{"PositionAndOnGround"});
      this.full = new PacketLogger.PlayerMoveValue(this, new String[]{"Full"});
      this.offerValues(new Value[]{this.logging, this.type, this.cancel, this.logAll, this.blockUpdateS2CPacket, this.keepAliveS2CPacket, this.commandSuggestionsS2CPacket, this.playerActionResponsePacket, this.updateSelectedSlotPacket, this.clickSlotC2SPacket, this.keepAliveC2SPacket, this.handSwingC2SPacket, this.requestCommandCompletionsC2SPacket});
      this.offerListeners(new Listener[]{new ListenerSend(this), new ListenerReceive(this)});
      this.register();
      this.logging.addObserver((event) -> {
         if (!event.isCanceled()) {
            this.clear();
         }

      });
      AbstractConfig var10001 = new AbstractConfig("logged_packets.txt") {
         public void save() {
            PacketLogger.this.clear();
         }

         public void load() {
            try {
               boolean var1;
               if (!PacketLogger.this.PACKET_LOG.exists()) {
                  var1 = PacketLogger.this.PACKET_LOG.createNewFile();
               } else {
                  var1 = PacketLogger.this.PACKET_LOG.delete();
               }
            } catch (IOException var2) {
               ClientLogger.getLogger().error("Couldn't make packet log file");
            }

         }
      };
   }

   protected void onEnable() {
      this.initializeWriter();
   }

   protected void onDisable() {
      this.clear();
   }

   private void register() {
      this.getValues().add(this.playerActionC2SPacket);
      this.playerActionValues.add(this.dropItem);
      this.playerActionValues.add(this.dropAllItems);
      this.playerActionValues.add(this.releaseUseItem);
      this.playerActionValues.add(this.stopDestroyBlock);
      this.playerActionValues.add(this.abortDestroyBlock);
      this.playerActionValues.add(this.startDestroyBlock);
      this.playerActionValues.add(this.swapItemWithOffhand);
      this.playerActionValues.forEach((playerActionValue) -> {
         this.getValues().add(playerActionValue);
      });
      this.getValues().add(this.playerMoveC2SPacket);
      this.playerMoveValues.add(this.onGroundOnly);
      this.playerMoveValues.add(this.lookAndOnGround);
      this.playerMoveValues.add(this.positionAndOnGround);
      this.playerMoveValues.add(this.full);
      this.playerMoveValues.forEach((playerMoveValue) -> {
         this.getValues().add(playerMoveValue);
      });
   }

   protected void debug(String str, PacketEvent<?> event) {
      if ((Boolean)this.logAll.getValue()) {
         this.log(str);
         this.write(str);
      } else {
         String cancelStatus = (Boolean)this.cancel.getValue() ? "\n[Canceled: " + event.isCanceled() + "]" : "";
         this.log(PacketRegistry.getName(event.getPacket()) + ":\n" + str + cancelStatus);
         this.write(PacketRegistry.getName(event.getPacket()) + ":\n" + str + cancelStatus);
      }
   }

   protected void log(String message) {
      if (this.logging.getValue() != Logging.FILE) {
         ClientLogger.getLogger().log(message, false);
      }
   }

   protected void clear() {
      try {
         boolean bl = this.PACKET_LOG.delete();
         boolean bl2 = this.PACKET_LOG.createNewFile();
         this.writer.flush();
         this.writer.close();
         this.writer = null;
      } catch (Exception var3) {
      }

   }

   protected void initializeWriter() {
      if (this.writer == null) {
         try {
            this.writer = new BufferedWriter(new FileWriter(this.PACKET_LOG));
         } catch (Exception var2) {
            ClientLogger.getLogger().error("Failed to create writer");
         }
      }

   }

   protected void write(String message) {
      if (this.logging.getValue() != Logging.CHAT) {
         try {
            String s = message + "\n";
            this.writer.write(s);
            this.writer.flush();
         } catch (IOException var3) {
            ClientLogger.getLogger().error("Error while writing packet log text");
         }

      }
   }

   public class PlayerActionValue extends Value<Boolean> {
      public PlayerActionValue(final PacketLogger this$0, String... names) {
         super(false, names);
      }
   }

   public class PlayerMoveValue extends Value<Boolean> {
      public PlayerMoveValue(final PacketLogger this$0, String... names) {
         super(false, names);
      }
   }
}
