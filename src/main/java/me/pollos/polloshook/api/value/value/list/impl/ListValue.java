package me.pollos.polloshook.api.value.value.list.impl;

import java.util.ArrayList;
import java.util.List;
import me.pollos.polloshook.api.value.value.Value;

public abstract class ListValue<T> extends Value<List<T>> {
   public ListValue(List<T> list, String... names) {
      super(list, names);
   }

   public ListValue(String... names) {
      super(new ArrayList(), names);
   }
}
