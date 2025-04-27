package me.pollos.polloshook.impl.module.misc.chatappend;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import me.pollos.polloshook.api.command.core.Argument;
import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.CommandModule;
import me.pollos.polloshook.api.util.thread.FileUtil;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.api.value.value.constant.EnumValue;
import me.pollos.polloshook.impl.config.base.AbstractConfig;
import me.pollos.polloshook.impl.module.misc.chatappend.mode.ChatAppendMode;

public class ChatAppend extends CommandModule {
   protected final Value<Boolean> greenText = new Value(false, new String[]{"GreenText", ">"});
   protected final Value<Boolean> spaced;
   protected final EnumValue<ChatAppendMode> mode;
   protected static List<String> FILTERS = new ArrayList();
   protected static final ArrayList<String> CUSTOM_FILTERS = new ArrayList();
   private boolean flag;

   public ChatAppend() {
      super(new String[]{"ChatAppend", "append"}, Category.MISC, new String[]{"AddFilter", "putcommandfilter", "filters", "filter"}, new Argument("[filter]"));
      this.spaced = (new Value(true, new String[]{"Space", "spaced"})).setParent(this.greenText);
      this.mode = new EnumValue(ChatAppendMode.POLLOSHOOK, new String[]{"Ending"});
      this.flag = true;
      this.offerValues(new Value[]{this.greenText, this.spaced, this.mode});
      this.offerListeners(new Listener[]{new ListenerSend(this)}); 
      AbstractConfig var10001 = new AbstractConfig("command_filters.txt") {
         public void load() {
            List<String> list = Arrays.asList("/", ".", ",", "$", "#", "+", "@", "!", "*", "-");
            ChatAppend.FILTERS = new ArrayList(list);
            File file = this.getFile();
            FileUtil.handleFileCreation(file);

            try {
               BufferedReader reader = FileUtil.createBufferedReader(file);

               try {
                  ChatAppend.CUSTOM_FILTERS.clear();

                  String line;
                  while((line = reader.readLine()) != null) {
                     String[] filters = line.split(":");
                     for (String filter : filters) {
                        ChatAppend.FILTERS.add(filter.trim());
                        ChatAppend.CUSTOM_FILTERS.add(filter.trim());
                     }
                  }
               } catch (Throwable var11) {
                  if (reader != null) {
                     try {
                        reader.close();
                     } catch (Throwable var10) {
                        var11.addSuppressed(var10);
                     }
                  }
                  throw var11;
               }

               if (reader != null) {
                  reader.close();
               }
            } catch (IOException var12) {
               var12.printStackTrace();
            }
         }

         public void save() {
            File file = this.getFile();
            FileUtil.handleFileCreation(file);
            if (!ChatAppend.CUSTOM_FILTERS.isEmpty()) {
               BufferedWriter f = FileUtil.createWriter(file);
               Iterator<String> var3 = ChatAppend.CUSTOM_FILTERS.iterator();

               while(var3.hasNext()) {
                  String s = (String)var3.next();
                  if (ChatAppend.this.flag) {
                     FileUtil.writeOnLine(f, s);
                     ChatAppend.this.flag = false;
                  } else {
                     FileUtil.writeOnLine(f, s);
                  }
               }
               FileUtil.closeWriter(f);
            } else {
                try {
                    BufferedWriter f = FileUtil.createWriter(file);
                    f.write(""); 
                    FileUtil.closeWriter(f);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
         }
      };
      var10001.load();
   }

   public String onCommand(String[] args) {
      if (args.length < 2) {
          return "Usage: .append <filter> or .append LIST or .append DELETE <filter>";
      }

      String command = args[1].toUpperCase(); 

      if (command.equals("LIST")) {
         if (FILTERS.isEmpty()) {
             return "No filters currently added.";
         }

         return "Filters: [" + FILTERS.stream().collect(Collectors.joining(" : ")) + "]";
      } else if (command.equals("DELETE")) {
         if (args.length < 3) {
             return "Usage: .append DELETE <filter>";
         }
         String filterToDelete = args[2];

         List<String> defaultFilters = Arrays.asList("/", ".", ",", "$", "#", "+", "@", "!", "*", "-");
         if (defaultFilters.contains(filterToDelete)) {
             return "Cannot delete default filter: " + filterToDelete;
         }

         if (CUSTOM_FILTERS.contains(filterToDelete)) {
            CUSTOM_FILTERS.remove(filterToDelete);
            FILTERS.remove(filterToDelete);
            return "Deleted custom filter: " + filterToDelete;
         } else {
            return "Filter not found in custom filters list: " + filterToDelete;
         }
      } else {
         String newFilter = args[1];
         if (FILTERS.contains(newFilter)) {
            return "Filter already exists: " + newFilter;
         } else {
            FILTERS.add(newFilter);
            CUSTOM_FILTERS.add(newFilter);
            return "Added filter: " + newFilter;
         }
      }
   }

   public static boolean shouldFilter(String string) {
       return FILTERS.stream().anyMatch(filter -> string.startsWith(filter));
   }

   public static String getContainedFilter(String string) {
       return FILTERS.stream()
                     .filter(filter -> string.startsWith(filter))
                     .findFirst()
                     .orElse("");
   }
}
