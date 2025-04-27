package me.pollos.polloshook.api.util.math;

public interface Passable<T> {
   boolean passed(double var1);

   boolean passed(long var1);

   boolean sleep(double var1);

   boolean sleep(long var1);

   T reset();
}
