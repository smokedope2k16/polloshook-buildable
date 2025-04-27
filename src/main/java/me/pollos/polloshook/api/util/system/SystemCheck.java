package me.pollos.polloshook.api.util.system;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SystemCheck {
   
   private static final Logger log = LoggerFactory.getLogger(SystemCheck.class);

   public static SystemStatus checkSystem() {
      return SystemStatus.NOT_SUITABLE;
   }
}
