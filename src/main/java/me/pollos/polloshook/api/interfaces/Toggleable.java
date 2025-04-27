package me.pollos.polloshook.api.interfaces;

public interface Toggleable {
   boolean isEnabled();

   void setEnabled(boolean var1);

   void toggle();
}
