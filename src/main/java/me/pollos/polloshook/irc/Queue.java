package me.pollos.polloshook.irc;

import java.util.Vector;

public class Queue {
   private Vector _queue = new Vector();

   public void add(Object o) {
      synchronized(this._queue) {
         this._queue.addElement(o);
         this._queue.notify();
      }
   }

   public void addFront(Object o) {
      synchronized(this._queue) {
         this._queue.insertElementAt(o, 0);
         this._queue.notify();
      }
   }

   public Object next() {
      Object o = null;
      synchronized(this._queue) {
         if (this._queue.size() == 0) {
            try {
               this._queue.wait();
            } catch (InterruptedException var6) {
               return null;
            }
         }

         try {
            o = this._queue.firstElement();
            this._queue.removeElementAt(0);
         } catch (ArrayIndexOutOfBoundsException var5) {
            throw new InternalError("Race hazard in Queue object.");
         }

         return o;
      }
   }

   public boolean hasNext() {
      return this.size() != 0;
   }

   public void clear() {
      synchronized(this._queue) {
         this._queue.removeAllElements();
      }
   }

   public int size() {
      return this._queue.size();
   }
}
