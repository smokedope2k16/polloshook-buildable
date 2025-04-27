package me.pollos.polloshook.impl.command.util;

import me.pollos.polloshook.api.command.core.Command;
import net.minecraft.util.crash.CrashReport;

public class CrashCommand extends Command {
   public CrashCommand() {
      super(new String[]{"Crash", "die"});
   }

   public String execute(String[] args) {
      CrashCommand.PollosExecption e = new CrashCommand.PollosExecption();
      mc.printCrashReport(new CrashReport(e.getMessage(), e));
      return "gg";
   }

   private static final class PollosExecption extends RuntimeException {
      public PollosExecption() {
         super("1 kg de pollo cortado en trozos (también puedes utilizar solamente un muslo con su contramuslo por persona, en trozos, así lo hemos hecho nosotros porque nos gusta más esa parte).\n150 ml de vino blanco.\nEl zumo de 1/2 limón.\n6 dientes ajos secos (aunque si quieres potenciar aún más el sabor del ajo, puedes echarle incluso el doble, ya va en gustos).\n2 hojas de laurel.\n3 cucharada soperas de harina de trigo.\n2 cucharadas soperas de perejil picado.\nAceite de oliva, sal y pimienta negra recién molida.");
      }
   }
}
