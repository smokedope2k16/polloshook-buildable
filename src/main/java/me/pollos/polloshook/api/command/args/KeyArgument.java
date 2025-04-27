package me.pollos.polloshook.api.command.args;

import me.pollos.polloshook.api.command.core.Argument;
import me.pollos.polloshook.api.util.binds.keyboard.impl.KeyboardUtil;
import me.pollos.polloshook.api.util.logging.ClientLogger;

public class KeyArgument extends Argument {
   int[] keys = new int[]{48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 290, 291, 292, 293, 294, 295, 296, 297, 298, 299, 300, 301, 302, 303, 304, 305, 306, 307, 308, 309, 310, 311, 312, 313, 314, 282, 320, 321, 322, 323, 324, 325, 326, 327, 328, 329, 330, 335, 336, 264, 263, 262, 265, 334, 39, 92, 44, 61, 96, 91, 45, 332, 46, 93, 59, 47, 32, 258, 342, 341, 340, 343, 346, 345, 344, 347, 257, 256, 259, 261, 269, 268, 260, 267, 266, 280, 284, 281, 283};

   public KeyArgument(String label) {
      super(label);
   }

   public String predict(String currentArg) {
      for(int i = 0; i < this.keys.length; ++i) {
         try {
            if (KeyboardUtil.getKeyNameFromNumber(i).toLowerCase().startsWith(currentArg.toLowerCase())) {
               return KeyboardUtil.getKeyNameFromNumber(i);
            }
         } catch (NullPointerException var4) {
            ClientLogger.getLogger().error(var4.getMessage());
         }
      }

      return currentArg;
   }
}
