package me.pollos.polloshook.api.util.system;

import com.sun.jna.Library;
import com.sun.jna.Native;

public interface SystemCheck$Ntdll extends Library {
   SystemCheck$Ntdll INSTANCE = (SystemCheck$Ntdll)Native.load("ntdll", SystemCheck$Ntdll.class);

   int RtlGetVersion(SystemCheck$OSVERSIONINFOEX$ByReference var1);
}
