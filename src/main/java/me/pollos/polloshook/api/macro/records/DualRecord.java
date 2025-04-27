package me.pollos.polloshook.api.macro.records;

import me.pollos.polloshook.api.macro.SimpleMacro;

public record DualRecord(SimpleMacro first, SimpleMacro second) {
   public DualRecord(SimpleMacro first, SimpleMacro second) {
      this.first = first;
      this.second = second;
   }

   public SimpleMacro first() {
      return this.first;
   }

   public SimpleMacro second() {
      return this.second;
   }
}
