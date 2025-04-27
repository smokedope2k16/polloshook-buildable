package me.pollos.polloshook.impl.config.modules;

public record SimpleConfig(String label, boolean enabled, boolean drawn, String bind, boolean hold) {
   public SimpleConfig(String label, boolean enabled, boolean drawn, String bind, boolean hold) {
      this.label = label;
      this.enabled = enabled;
      this.drawn = drawn;
      this.bind = bind;
      this.hold = hold;
   }

   public String label() {
      return this.label;
   }

   public boolean enabled() {
      return this.enabled;
   }

   public boolean drawn() {
      return this.drawn;
   }

   public String bind() {
      return this.bind;
   }

   public boolean hold() {
      return this.hold;
   }
}
