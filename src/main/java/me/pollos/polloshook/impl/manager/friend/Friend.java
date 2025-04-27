package me.pollos.polloshook.impl.manager.friend;


import me.pollos.polloshook.api.interfaces.Labeled;

public class Friend implements Labeled {
   private final String label;
   private String alias;

   
   public String getLabel() {
      return this.label;
   }

   
   public String getAlias() {
      return this.alias;
   }

   
   public void setAlias(String alias) {
      this.alias = alias;
   }

   
   public Friend(String label, String alias) {
      this.label = label;
      this.alias = alias;
   }
}
