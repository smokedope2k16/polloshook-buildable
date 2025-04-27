package me.pollos.polloshook.api.util.preset;


import me.pollos.polloshook.api.interfaces.Labeled;

public abstract class Preset implements Labeled {
   private final String[] names;

   public Preset(String... names) {
      this.names = names;
   }

   public void execute() {
   }

   public String getLabel() {
      return this.names == null ? "None" : this.names[0];
   }

   
   public String[] getNames() {
      return this.names;
   }
}
