package me.pollos.polloshook.impl.module.other.rpc;



public enum RPCImage {
   CAT("kitty_cat"),
   VENUZ("venuz"),
   MORNING("morning"),
   SHERBERT("classic"),
   MASONIC("masonic"),
   ROTATINGS("rotatings"),
   GUMDROP("tudoucat"),
   SLURNSY("benson"),
   BENDZONE("bendzone"),
   BANDHU("aetra"),
   HOCKEY("hockey"),
   RUSSIAN("russia"),
   LENAY("cute"),
   POLLOS("pollos"),
   SHUFFLED("ignored");

   private final String key;

   public RPCImage next() {
      RPCImage[] values = values();
      int nextOrdinal = (this.ordinal() + 1) % values.length;
      if (values[nextOrdinal] == SHUFFLED) {
         nextOrdinal = (nextOrdinal + 1) % values.length;
      }

      return values[nextOrdinal];
   }

   
   public String getKey() {
      return this.key;
   }

   
   private RPCImage(final String key) {
      this.key = key;
   }

   // $FF: synthetic method
   private static RPCImage[] $values() {
      return new RPCImage[]{CAT, VENUZ, MORNING, SHERBERT, MASONIC, ROTATINGS, GUMDROP, SLURNSY, BENDZONE, BANDHU, HOCKEY, RUSSIAN, LENAY, POLLOS, SHUFFLED};
   }
}
