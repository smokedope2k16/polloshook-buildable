package me.pollos.polloshook.impl.module.other.irc.util;

public record IrcPing(long time, String name, double x, double y, double z, String dimension, String ip) {
   public IrcPing(long time, String name, double x, double y, double z, String dimension, String ip) {
      this.time = time;
      this.name = name;
      this.x = x;
      this.y = y;
      this.z = z;
      this.dimension = dimension;
      this.ip = ip;
   }

   public long time() {
      return this.time;
   }

   public String name() {
      return this.name;
   }

   public double x() {
      return this.x;
   }

   public double y() {
      return this.y;
   }

   public double z() {
      return this.z;
   }

   public String dimension() {
      return this.dimension;
   }

   public String ip() {
      return this.ip;
   }
}
