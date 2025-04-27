package me.pollos.polloshook.impl.module.render.betterchat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.asm.mixins.network.IGameMessageS2CPacket;
import me.pollos.polloshook.impl.events.network.PacketEvent;
import me.pollos.polloshook.impl.module.other.manager.util.ClientBrackets;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

public class ListenerChat extends ModuleListener<BetterChat, PacketEvent.Receive<?>> {
   public ListenerChat(BetterChat module) {
      super(module, PacketEvent.Receive.class);
   }

   public void call(PacketEvent.Receive<?> event) {
      if ((Boolean)((BetterChat)this.module).timeStamps.getValue()) {
         Packet var3 = event.getPacket();
         if (var3 instanceof GameMessageS2CPacket) {
            GameMessageS2CPacket packet = (GameMessageS2CPacket)var3;
            Text message = packet.content();
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("H:mm");
            String timeStamp = simpleDateFormat.format(calendar.getTime());
            Text left = Text.literal(((ClientBrackets)((BetterChat)this.module).bracket.getValue()).getBrackets()[0]).withColor(((BetterChat)this.module).bracketColor.getColor().getRGB());
            Text time = Text.literal(timeStamp).withColor(((BetterChat)this.module).timeColor.getColor().getRGB());
            Text right = Text.literal(((ClientBrackets)((BetterChat)this.module).bracket.getValue()).getBrackets()[1]).withColor(((BetterChat)this.module).bracketColor.getColor().getRGB());
            Text timestamp = Text.empty().append(left.copy().append(time.copy().append(right.copy())));
            ((IGameMessageS2CPacket)event.getPacket()).setContent(timestamp.copy().append(" ").setStyle(Style.EMPTY).append(message));
         }
      }

   }
}
