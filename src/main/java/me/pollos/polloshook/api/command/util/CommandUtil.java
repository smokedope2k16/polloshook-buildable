package me.pollos.polloshook.api.command.util;

import java.util.Collection;
import java.util.Iterator;
import me.pollos.polloshook.api.command.core.Command;
import me.pollos.polloshook.api.interfaces.Labeled;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.module.Module;
import me.pollos.polloshook.api.util.text.TextUtil;

public final class CommandUtil {

   private CommandUtil() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }

   public static Command getCommand(String arg) {
      return getCommand(arg, true);
   }

   public static Command getCommand(String arg, boolean moduleCheck) {
      if (moduleCheck && getModule(arg) != null) {
         return null;
      }
      return getLabeledStartingWith(arg, Managers.getCommandManager().collectCommands());
   }

   public static Module getModule(String arg) {
      return getLabeledStartingWith(arg, Managers.getModuleManager().getAllModules());
   }

   public static <T extends Labeled> T getLabeledStartingWith(String prefix, Collection<T> collection) {
      for (T labeled : collection) {
         if (TextUtil.startsWith(labeled.getLabel(), prefix)) {
            return labeled;
         }
      }
      return null;
   }

   public static String concatenate(String[] args, int startIndex) {
      return concatenate(args, startIndex, args.length);
   }

   public static String concatenate(String[] args, int startIndex, int end) {
      if (startIndex < 0 || startIndex >= args.length) {
         throw new ArrayIndexOutOfBoundsException(startIndex);
      }
      if (end > args.length) {
         throw new ArrayIndexOutOfBoundsException(end);
      }

      StringBuilder builder = new StringBuilder(args[startIndex]);
      for (int i = startIndex + 1; i < end; i++) {
         builder.append(" ").append(args[i]);
      }
      return builder.toString();
   }
}