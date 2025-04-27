package me.pollos.polloshook.impl.module.misc.spammer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import me.pollos.polloshook.PollosHook;
import me.pollos.polloshook.api.command.args.FileArgument;
import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.CommandModule;
import me.pollos.polloshook.api.util.logging.ClientLogger;
import me.pollos.polloshook.api.util.math.StopWatch;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.StringValue;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.api.value.value.constant.EnumValue;
import me.pollos.polloshook.api.value.value.parents.EnumParent;
import me.pollos.polloshook.api.value.value.parents.impl.Parent;
import me.pollos.polloshook.impl.module.misc.spammer.mode.SpammerMode;

public class Spammer extends CommandModule {
   protected final EnumValue<SpammerMode> mode;
   protected final NumberValue<Float> delay;
   protected final Value<Boolean> randomize;
   protected final Value<Boolean> greenText;
   protected final Value<Boolean> macros;
   protected final StringValue splitter;
   protected final Value<Boolean> antiKick;
   protected final NumberValue<Integer> length;
   protected final Value<Boolean> sendSlashCommands;
   protected final Value<Boolean> loop;
   protected File currentFile;
   protected final List<String> strings;
   protected final StopWatch timer;

   public Spammer() {
      super(new String[]{"Spammer", "chatspammer", "chatspam", "spam"}, Category.MISC, new String[]{"SpammerFile", "spamfile", "setfile"}, new Spammer.SpammerFileArgument());
      this.mode = new EnumValue(SpammerMode.FILE, new String[]{"Mode"});
      this.delay = (new NumberValue(5.0F, 0.0F, 30.0F, 0.1F, new String[]{"Delay"})).withTag("second");
      this.randomize = (new Value(false, new String[]{"Random", "shuffle"})).setParent((Parent)this.fileParent());
      this.greenText = (new Value(false, new String[]{"GreenText", "green"})).setParent((Parent)this.fileParent());
      this.macros = (new Value(false, new String[]{"SplitMacros", "splitmacro", "instantmacros"})).setParent((Parent)this.fileParent());
      this.splitter = (new StringValue("::", new String[]{"Split", "splitter", "divider"})).setParent(this.macros);
      this.antiKick = (new Value(false, new String[]{"AntiKick", "nokick"})).setParent((Parent)this.fileParent());
      this.length = (new NumberValue(10, 1, 20, new String[]{"Length", "l"})).setParent(this.antiKick);
      this.sendSlashCommands = (new Value(false, new String[]{"SendChatCommands", "allowcommands"})).setParent((Parent)this.fileParent());
      this.loop = (new Value(true, new String[]{"Loop", "looped"})).setParent((Parent)this.fileParent());
      this.strings = new ArrayList();
      this.timer = new StopWatch();
      this.offerValues(new Value[]{this.mode, this.delay, this.randomize, this.greenText, this.macros, this.splitter, this.antiKick, this.length, this.sendSlashCommands, this.loop});
      this.offerListeners(new Listener[]{new ListenerTick(this)});
   }

   public void onWorldLoad() {
      this.setEnabled(false);
   }

   protected String getTag() {
      return this.mode.getStylizedName();
   }

   public String onCommand(String[] args) {
      String args1 = args[1];
      File[] files;
      int i;
      if (args1.equalsIgnoreCase("list")) {
         files = PollosHook.SPAMMERS.listFiles();
         if (files != null && files.length != 0) {
            StringBuilder fileNames = new StringBuilder();

            for(i = 0; i < files.length; ++i) {
               if (i > 0) {
                  fileNames.append(", ");
               }

               fileNames.append(files[i].getName());
            }

            return "Spammers (%d): %s".formatted(new Object[]{files.length, fileNames});
         } else {
            return "No files found";
         }
      } else if (!PollosHook.SPAMMERS.exists()) {
         return "Spammer directory doesn't exist (%s)".formatted(new Object[]{PollosHook.SPAMMERS.mkdir() ? "created new one" : "failed to create new one"});
      } else {
         files = (File[])Objects.requireNonNull(PollosHook.SPAMMERS.listFiles());
         int var4 = files.length;

         for(i = 0; i < var4; ++i) {
            File file = files[i];
            String fixedArgs1 = args1.endsWith(".txt") ? "" : ".txt";
            if (file.getName().equalsIgnoreCase(args1 + fixedArgs1)) {
               this.setCurrentFile(file);
               if (!this.isEnabled()) {
                  this.setEnabled(true);
               }

               return "Set file to %s".formatted(new Object[]{args1});
            }
         }

         StringJoiner stringJoiner = new StringJoiner(", ");
         File[] var9 = (File[])Objects.requireNonNull(PollosHook.SPAMMERS.listFiles());
         i = var9.length;

         for(int var11 = 0; var11 < i; ++var11) {
            File file = var9[var11];
            if (file.getName().contains(".txt")) {
               stringJoiner.add(file.getName());
            }
         }

         return "No file found, try: %s".formatted(new Object[]{stringJoiner});
      }
   }

   private void setCurrentFile(File file) {
      this.currentFile = file;

      try {
         BufferedReader reader = new BufferedReader(new FileReader(file));
         this.strings.clear();

         String line;
         while((line = reader.readLine()) != null) {
            if (!line.replace("\\s", "").isEmpty()) {
               this.strings.add(line);
            }
         }

         reader.close();
      } catch (IOException var4) {
         ClientLogger.getLogger().error("Error while value file");
      }

   }

   private EnumParent fileParent() {
      return new EnumParent(this.mode, SpammerMode.FILE, false);
   }

   private static class SpammerFileArgument extends FileArgument {
      public SpammerFileArgument() {
         super("[file]", PollosHook.SPAMMERS);
      }

      public String predict(String currentArg) {
         return currentArg.toLowerCase().startsWith("l") ? "list" : super.predict(currentArg);
      }
   }
}
