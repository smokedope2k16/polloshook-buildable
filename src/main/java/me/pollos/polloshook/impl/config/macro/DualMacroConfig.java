package me.pollos.polloshook.impl.config.macro;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Iterator;
import me.pollos.polloshook.api.macro.DualMacro;
import me.pollos.polloshook.api.macro.SimpleMacro;
import me.pollos.polloshook.api.macro.records.DualRecord;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.util.thread.FileUtil;
import me.pollos.polloshook.impl.config.base.AbstractConfig;
import net.minecraft.client.util.InputUtil;

public class DualMacroConfig extends AbstractConfig {
   public DualMacroConfig(String label) {
      super(label);
      Managers.getConfigManager().getRegistry().add(this);
   }

   public void load() {
      Managers.getMacroManager().getDualMacros().clear();
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
               } catch (NumberFormatException var10) {
                  key = InputUtil.fromTranslationKey(split[1]).getCode();
               }

               SimpleMacro first = Managers.getMacroManager().getSimple(split[2]);
               SimpleMacro second = Managers.getMacroManager().getSimple(split[3]);
               second.setPaused(true);
               first.setPaused(true);
               DualRecord dualRecord = new DualRecord(first, second);

               try {
                  Managers.getMacroManager().getDualMacros().add(new DualMacro(split[0], key, dualRecord));
               } catch (Exception var9) {
                  var9.printStackTrace();
               }
            }

            reader.close();
         } catch (Exception var11) {
            var11.printStackTrace();
         }

      }
   }

   public void save() {
      FileUtil.handleFileCreation(this.getFile());

      BufferedWriter bufferedWriter;
      try {
         bufferedWriter = FileUtil.createWriter(this.getFile());
         Iterator var2 = Managers.getMacroManager().getDualMacros().iterator();

         while(var2.hasNext()) {
            DualMacro macro = (DualMacro)var2.next();
            String var10001 = macro.getLabel();
            bufferedWriter.write(var10001 + ":" + String.valueOf(InputUtil.fromKeyCode(macro.getKey(), 0)) + ":" + macro.getFirst().getLabel() + ":" + macro.getSecond().getLabel());
            bufferedWriter.newLine();
         }
      } catch (Exception var5) {
         var5.printStackTrace();
         return;
      }

      try {
         bufferedWriter.close();
      } catch (IOException var4) {
         var4.printStackTrace();
      }

   }
}
