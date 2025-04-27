package me.pollos.polloshook.api.util.system;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.LPVOID;
import java.awt.Color;
import net.minecraft.client.util.Window;
import org.lwjgl.glfw.GLFWNativeWin32;

public class WindowUtil {
   public static int rgbToHex(Color color) {
      return rgbToHex(color.getRed(), color.getGreen(), color.getBlue());
   }

   public static int rgbToHex(int r, int g, int elementCodec) {
      if (r >= 0 && r <= 255 && g >= 0 && g <= 255 && elementCodec >= 0 && elementCodec <= 255) {
         return elementCodec << 16 | g << 8 | r;
      } else {
         throw new IllegalArgumentException("RGB values should be between 0 and 255.");
      }
   }

   public static void applyChanges(Window window, int corner, int bar, int text, int stroke) {
      long glfwWindow = window.getHandle();
      long hwndLong = GLFWNativeWin32.glfwGetWin32Window(glfwWindow);
      HWND hwnd = new HWND(Pointer.createConstant(hwndLong));
      Memory memTheme = new Memory((long)Native.POINTER_SIZE);
      Memory memCorner = new Memory((long)Native.POINTER_SIZE);
      Memory memTitleBarColor = new Memory((long)Native.POINTER_SIZE);
      Memory memTitleBarTextColor = new Memory((long)Native.POINTER_SIZE);
      Memory memTitleBarStrokeColor = new Memory((long)Native.POINTER_SIZE);
      memTheme.setInt(0L, 0);
      memTitleBarColor.setInt(0L, bar);
      memTitleBarTextColor.setInt(0L, text);
      memTitleBarStrokeColor.setInt(0L, stroke);
      if (corner == 0) {
         memCorner.setInt(0L, 1);
      } else if (corner == 1) {
         memCorner.setInt(0L, 2);
      } else if (corner == 2) {
         memCorner.setInt(0L, 3);
      }

      DwmApi.INSTANCE.DwmSetWindowAttribute(hwnd, DwmApi.DWMWA_USE_IMMERSIVE_DARK_MODE, new LPVOID(memTheme), new DWORD(4L));
      DwmApi.INSTANCE.DwmSetWindowAttribute(hwnd, DwmApi.DWMWA_WINDOW_CORNER_PREFERENCE, new LPVOID(memCorner), new DWORD(4L));
      DwmApi.INSTANCE.DwmSetWindowAttribute(hwnd, DwmApi.DWMWA_CAPTION_COLOR, new LPVOID(memTitleBarColor), new DWORD(4L));
      DwmApi.INSTANCE.DwmSetWindowAttribute(hwnd, DwmApi.DWMWA_TEXT_COLOR, new LPVOID(memTitleBarTextColor), new DWORD(4L));
      DwmApi.INSTANCE.DwmSetWindowAttribute(hwnd, DwmApi.DWMWA_BORDER_COLOR, new LPVOID(memTitleBarStrokeColor), new DWORD(4L));
   }
}
