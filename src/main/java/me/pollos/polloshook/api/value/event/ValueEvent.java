package me.pollos.polloshook.api.value.event;


import me.pollos.polloshook.api.event.events.Event;
import me.pollos.polloshook.api.value.value.Value;

public class ValueEvent<T> extends Event {
   private final Value<T> setting;
   private T value;

   
   public Value<T> getSetting() {
      return this.setting;
   }

   
   public T getValue() {
      return this.value;
   }

   
   public void setValue(T value) {
      this.value = value;
   }

   
   public ValueEvent(Value<T> setting, T value) {
      this.setting = setting;
      this.value = value;
   }
}
