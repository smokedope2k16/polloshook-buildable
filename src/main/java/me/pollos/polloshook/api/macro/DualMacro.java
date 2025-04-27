package me.pollos.polloshook.api.macro;

import java.util.Objects;
import me.pollos.polloshook.api.interfaces.Labeled;
import me.pollos.polloshook.api.interfaces.Minecraftable;
import me.pollos.polloshook.api.interfaces.Toggleable;
import me.pollos.polloshook.api.macro.records.DualRecord;
import me.pollos.polloshook.api.util.obj.AbstractSendable;

public class DualMacro extends AbstractSendable implements Minecraftable, Toggleable, Labeled {
   private final String label;
   private final int key;
   private final DualRecord record;
   private boolean enabled = false;

   public DualMacro(String label, int key, DualRecord record) {
      this.label = label;
      this.key = key;
      this.record = record;
   }

   public String getLabel() {
      return this.label;
   }

   public int getKey() {
      return this.key;
   }

   public DualRecord getRecord() {
      return this.record;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }

   public SimpleMacro getFirst() {
      return this.record.first();
   }

   public SimpleMacro getSecond() {
      return this.record.second();
   }

   public void send() {
      if (this.enabled) {
         this.getFirst().getChatMacro().send();
      } else {
         this.getSecond().getChatMacro().send();
      }
      this.toggle();
   }

   public void toggle() {
      this.enabled = !this.enabled;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      if (!super.equals(o)) return false;
      DualMacro dualMacro = (DualMacro) o;
      return key == dualMacro.key && enabled == dualMacro.enabled && Objects.equals(label, dualMacro.label) && Objects.equals(record, dualMacro.record);
   }

   @Override
   public int hashCode() {
      return Objects.hash(super.hashCode(), label, key, record, enabled);
   }

   @Override
   public String toString() {
      return "DualMacro(label=" + label + ", key=" + key + ", record=" + record + ", enabled=" + enabled + ")";
   }
}