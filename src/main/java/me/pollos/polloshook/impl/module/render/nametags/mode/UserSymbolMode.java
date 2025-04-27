package me.pollos.polloshook.impl.module.render.nametags.mode;



public enum UserSymbolMode {
   CROSS("♰"),
   MOON("☪"),
   NAZI("✠"),
   MONEY("\ud83d\udcb2"),
   STAR("⭐"),
   GORILLA("\ud83e\udd8d"),
   CHICKEN("\ud83d\udc14"),
   FAX("℻"),
   WEIRD_STAR("⚝"),
   YING_YANG("☯"),
   JEWISH("✡"),
   TRI("⃤");

   private final String symbol;

   
   private UserSymbolMode(final String symbol) {
      this.symbol = symbol;
   }

   
   public String getSymbol() {
      return this.symbol;
   }

   // $FF: synthetic method
   private static UserSymbolMode[] $values() {
      return new UserSymbolMode[]{CROSS, MOON, NAZI, MONEY, STAR, GORILLA, CHICKEN, FAX, WEIRD_STAR, YING_YANG, JEWISH, TRI};
   }
}
