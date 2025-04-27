package me.pollos.polloshook.api.value.value.parents.impl;



public abstract class Parent {
   protected final boolean opposite;

   public abstract boolean isVisible();

   
   public Parent(boolean opposite) {
      this.opposite = opposite;
   }
}
