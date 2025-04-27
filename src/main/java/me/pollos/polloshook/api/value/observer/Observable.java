package me.pollos.polloshook.api.value.observer;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Observable<T> {
   private final List<Observer<? super T>> observers = new LinkedList();

   public T onChange(T value) {
      Iterator var2 = this.observers.iterator();

      while(var2.hasNext()) {
         Observer<? super T> observer = (Observer)var2.next();
         observer.onChange(value);
      }

      return value;
   }

   public void addObserver(Observer<? super T> observer) {
      if (observer != null && !this.observers.contains(observer)) {
         this.observers.add(observer);
      }

   }

   public void removeObserver(Observer<? super T> observer) {
      this.observers.remove(observer);
   }
}
