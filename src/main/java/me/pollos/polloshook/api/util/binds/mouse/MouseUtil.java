package me.pollos.polloshook.api.util.binds.mouse;

public class MouseUtil {
   public static MouseClickAction getActionByInt(int i) {
      switch(i) {
      case 0:
         return MouseClickAction.RELEASE;
      case 1:
         return MouseClickAction.PRESS;
      default:
         return MouseClickAction.UNKNOWN;
      }
   }

   public static MouseButton getMouseButtonByInt(int i) {
      switch(i) {
      case 0:
         return MouseButton.LEFT;
      case 1:
         return MouseButton.RIGHT;
      case 2:
         return MouseButton.MIDDLE;
      case 3:
         return MouseButton.THUMB4;
      case 4:
         return MouseButton.THUMB5;
      default:
         return MouseButton.UNKNOWN;
      }
   }

   public static int reversed(MouseButton mb) {
      switch(mb) {
      case RIGHT:
         return 1;
      case MIDDLE:
         return 2;
      case THUMB4:
         return 3;
      case THUMB5:
         return 4;
      default:
         return -1;
      }
   }
}
