package me.pollos.polloshook.impl.command.irc;

import me.pollos.polloshook.api.command.core.Command;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.entity.EntityUtil;
import me.pollos.polloshook.impl.module.other.irc.IrcModule;
import me.pollos.polloshook.impl.module.other.irc.util.IrcPing;
import net.minecraft.util.math.BlockPos;

public class PingCommand extends Command {
   public PingCommand() {
      super(new String[]{"Ping", "pong", "backup"});
   }

   public String execute(String[] args) {
      if (!((IrcModule)Managers.getModuleManager().get(IrcModule.class)).isEnabled()) {
         return "IRC module not enabled..";
      } else if (!Managers.getIrcManager().isConnected()) {
         return "Not connected to irc...";
      } else if (!((IrcModule)Managers.getModuleManager().get(IrcModule.class)).isInsideRoom()) {
         return "Haven't joined keyCodec chat room yet...";
      } else if (mc.getCurrentServerEntry() == null) {
         return "Unable to get server ip (singleplayer?)";
      } else {
         BlockPos pos = mc.player.getBlockPos();
         String dimension = mc.world.getRegistryKey().getValue().getPath();
         String server = mc.getCurrentServerEntry().address;
         IrcPing ping = new IrcPing(System.currentTimeMillis(), EntityUtil.getName(mc.player), (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), dimension, server);
         String pingStr = String.format("%s,%s,%s,%s,%s,%s,%s", IrcModule.PING_PREFIX, EntityUtil.getName(mc.player), pos.getX(), pos.getY(), pos.getZ(), dimension, server);
         Managers.getIrcManager().sendMessage("#keqing4pollos", pingStr);
         ((IrcModule)Managers.getModuleManager().get(IrcModule.class)).getPings().add(ping);
         return "Sending ping...";
      }
   }
}
