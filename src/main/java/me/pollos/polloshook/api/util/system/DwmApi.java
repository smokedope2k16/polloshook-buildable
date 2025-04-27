package me.pollos.polloshook.api.util.system;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.LPVOID;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

public interface DwmApi extends StdCallLibrary {
   DwmApi INSTANCE = (DwmApi)Native.load("dwmapi", DwmApi.class, W32APIOptions.DEFAULT_OPTIONS);
   DWORD DWMWA_USE_IMMERSIVE_DARK_MODE = new DWORD(20L);
   DWORD DWMWA_WINDOW_CORNER_PREFERENCE = new DWORD(33L);
   DWORD DWMWA_CAPTION_COLOR = new DWORD(35L);
   DWORD DWMWA_TEXT_COLOR = new DWORD(36L);
   DWORD DWMWA_BORDER_COLOR = new DWORD(34L);

   HRESULT DwmSetWindowAttribute(HWND var1, DWORD var2, LPVOID var3, DWORD var4);
}
