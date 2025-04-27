package me.pollos.polloshook.impl.module.render.betterchat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.IntStream;
import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.util.binds.keyboard.impl.KeyboardUtil;
import me.pollos.polloshook.api.util.logging.ClientLogger;
import me.pollos.polloshook.api.util.text.TextUtil;
import me.pollos.polloshook.asm.ducks.gui.chat.IChatHud;
import me.pollos.polloshook.impl.events.chat.ChatMouseClickEvent;
import me.pollos.polloshook.impl.module.render.betterchat.mode.Language;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.hud.ChatHudLine.Visible;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.apache.commons.lang3.StringEscapeUtils;

public class ListenerChatMouseClick extends ModuleListener<BetterChat, ChatMouseClickEvent> {
   public ListenerChatMouseClick(BetterChat module) {
      super(module, ChatMouseClickEvent.class);
   }

   public void call(ChatMouseClickEvent event) {
      ChatHudLine l = this.getMessageAtXZ(event.getX(), event.getZ());
      if (l != null) {
         Text contentText = l.content();
         String contentNoColor = TextUtil.removeColor(contentText.getString());
         if ((Boolean)((BetterChat)this.module).copy.getValue() && KeyboardUtil.isCTRL()) {
            mc.keyboard.setClipboard(contentNoColor);
            ClientLogger.getLogger().log("Copied (%s) to clipboard".formatted(new Object[]{contentNoColor}));
         } else if ((Boolean)((BetterChat)this.module).translate.getValue() && KeyboardUtil.isShift()) {
            if (contentNoColor.startsWith("<") && contentNoColor.contains(">")) {
               int i = contentNoColor.indexOf(">") + 1;
               String name = contentNoColor.substring(0, i);
               String str = contentNoColor.substring(i).trim();
               String translateNoName = this.translate(str, ((Language)((BetterChat)this.module).targetLanguage.getValue()).getCode());
               ClientLogger.getLogger().log("%s%s %s".formatted(new Object[]{Formatting.WHITE, name, translateNoName}));
            } else {
               String translatedMessage = this.translate(contentNoColor, ((Language)((BetterChat)this.module).targetLanguage.getValue()).getCode());
               ClientLogger.getLogger().log("%s%s".formatted(new Object[]{Formatting.WHITE, translatedMessage}));
            }
         }

      }
   }

   private ChatHudLine getMessageAtXZ(double x, double y) {
      IChatHud iChatHud = (IChatHud)mc.inGameHud.getChatHud();
      int lineSelected = iChatHud.messageIndex(x, y);
      if (lineSelected == -1) {
         return null;
      } else {
         List<Integer> start = IntStream.range(0, iChatHud.getVisibleMessages().size()).boxed().toList();
         int end = (Integer)start.stream().filter((index) -> {
            return index <= lineSelected;
         }).reduce((keyCodec, elementCodec) -> {
            return elementCodec;
         }).orElse(-1);
         int indexOfMessage = start.indexOf(end);
         return (ChatHudLine)iChatHud.getAllMessages().get(indexOfMessage);
      }
   }

   private String translate(String text, String targetLang) {
      try {
         String urlStr = "https://translate.google.com/translate_a/single?client=gtx&sl=auto&tl=" + targetLang + "&dt=t&q=" + URLEncoder.encode(text, StandardCharsets.UTF_8);
         String result = getString(urlStr);
         int firstQuote = result.indexOf("\"");
         int secondQuote = result.indexOf("\"", firstQuote + 1);
         return StringEscapeUtils.unescapeJava(result.substring(firstQuote + 1, secondQuote));
      } catch (Exception var7) {
         return String.valueOf(Formatting.RED) + "Translation failed";
      }
   }

   private static String getString(String urlStr) throws IOException {
      URL url = new URL(urlStr);
      HttpURLConnection connection = (HttpURLConnection)url.openConnection();
      connection.setRequestProperty("User-Agent", "Mozilla/5.0");
      BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
      StringBuilder response = new StringBuilder();

      String line;
      while((line = br.readLine()) != null) {
         response.append(line);
      }

      br.close();
      return response.toString();
   }
}
