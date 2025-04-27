package me.pollos.polloshook.api.command.core;

public class Argument {
   private String label;

   public Argument(String label) {
      this.label = label;
   }

   public String predict(String currentArg) {
      return currentArg;
   }

   public String getLabel() {
      return this.label;
   }

   public void setLabel(String label) {
      this.label = label;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      Argument argument = (Argument) o;

      return this.label != null ? this.label.equals(argument.label) : argument.label == null;
   }

   @Override
   public int hashCode() {
      return this.label != null ? this.label.hashCode() : 0;
   }

   @Override
   public String toString() {
      return "Argument(label=" + this.label + ")";
   }
}