package me.pollos.polloshook.asm.ducks.gui.chat;

import java.util.List;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.hud.ChatHudLine.Visible;
import net.minecraft.text.Text;

public interface IChatHud {
   void clientMessage(Text var1, int var2);

   int messageIndex(double var1, double var3);

   double getX(double var1);

   double getY(double var1);

   List<Visible> getVisibleMessages();

   List<ChatHudLine> getAllMessages();

   List<Text> getTexts();
}
