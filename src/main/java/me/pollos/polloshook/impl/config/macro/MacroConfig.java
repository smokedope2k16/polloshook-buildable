package me.pollos.polloshook.impl.config.macro;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Iterator;
import me.pollos.polloshook.api.macro.SimpleMacro;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.util.obj.MessageSender;
import me.pollos.polloshook.api.util.thread.FileUtil;
import me.pollos.polloshook.impl.config.base.AbstractConfig;
import net.minecraft.client.util.InputUtil;

public class MacroConfig extends AbstractConfig {
   public MacroConfig(String string) {
      super(string);
      Managers.getConfigManager().getRegistry().add(this);
   }

   public void save() {
      FileUtil.handleFileCreation(this.getFile());

      BufferedWriter bufferedWriter;
      try {
         bufferedWriter = FileUtil.createWriter(this.getFile());
         Iterator var2 = Managers.getMacroManager().getSimpleMacros().iterator();

         while(var2.hasNext()) {
            SimpleMacro macro = (SimpleMacro)var2.next();
            String var10000 = macro.getLabel();
            String toWrite = var10000 + ":" + String.valueOf(InputUtil.fromKeyCode(macro.getKey(), 0)) + ":" + macro.getChatMacro().getMessage();
            FileUtil.writeThenNewLine(bufferedWriter, toWrite);
         }
      } catch (Exception var6) {
         var6.printStackTrace();
         return;
      }

      try {
         bufferedWriter.close();
      } catch (IOException var5) {
         var5.printStackTrace();
      }

   }

   public void load() {
      Managers.getMacroManager().getSimpleMacros().clear();
      FileUtil.handleFileCreation(this.getFile());
      if (this.getFile().exists()) {
         try {
            BufferedReader reader = FileUtil.createBufferedReader(this.getFile());

            String text;
            while((text = reader.readLine()) != null) {
               String[] split = text.split(":");

               int key;
               try {
                  key = Integer.parseInt(split[1]);
               } catch (NumberFormatException var7) {
                  key = InputUtil.fromTranslationKey(split[1]).getCode();
               }

               try {
                  Managers.getMacroManager().getSimpleMacros().add(new SimpleMacro(split[0], key, new MessageSender(split[2])));
               } catch (Exception var6) {
                  var6.printStackTrace();
               }
            }

            reader.close();
         } catch (Exception var8) {
            var8.printStackTrace();
         }

      }
   }
}
