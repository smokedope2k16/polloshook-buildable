package me.pollos.polloshook.api.interfaces;

import me.pollos.polloshook.api.util.logging.ClientLogger;

public interface Initializable {
   default Initializable start(String startMessage) {
      ClientLogger.getLogger().info(startMessage);
      return this;
   }

   default Initializable finish(String finishMessage) {
      ClientLogger.getLogger().info(finishMessage);
      return this;
   }

   default void info(String str) {
      ClientLogger.getLogger().info(str);
   }

   void init();
}
