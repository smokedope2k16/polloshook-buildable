package me.pollos.polloshook.api.util.system;

import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.WORD;
import java.util.Arrays;
import java.util.List;

public class SystemCheck$OSVERSIONINFOEX extends Structure {
   public DWORD dwOSVersionInfoSize = new DWORD();
   public DWORD dwMajorVersion = new DWORD();
   public DWORD dwMinorVersion = new DWORD();
   public DWORD dwBuildNumber = new DWORD();
   public DWORD dwPlatformId = new DWORD();
   public byte[] szCSDVersion = new byte[128];
   public WORD wServicePackMajor = new WORD();
   public WORD wServicePackMinor = new WORD();
   public WORD wSuiteMask = new WORD();
   public byte wProductType;
   public byte wReserved;

   protected List<String> getFieldOrder() {
      return Arrays.asList("dwOSVersionInfoSize", "dwMajorVersion", "dwMinorVersion", "dwBuildNumber", "dwPlatformId", "szCSDVersion", "wServicePackMajor", "wServicePackMinor", "wSuiteMask", "wProductType", "wReserved");
   }
}
