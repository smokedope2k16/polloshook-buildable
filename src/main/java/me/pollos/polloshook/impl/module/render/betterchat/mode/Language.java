package me.pollos.polloshook.impl.module.render.betterchat.mode;



public enum Language {
   ENGLISH("en"),
   SPANISH("es"),
   FRENCH("fr"),
   GERMAN("de"),
   ITALIAN("it"),
   PORTUGUESE("pt"),
   RUSSIAN("ru"),
   CHINESE("zh"),
   JAPANESE("ja"),
   KOREAN("ko"),
   ARABIC("ar");

   private final String code;

   
   public String getCode() {
      return this.code;
   }

   
   private Language(final String code) {
      this.code = code;
   }

   // $FF: synthetic method
   private static Language[] $values() {
      return new Language[]{ENGLISH, SPANISH, FRENCH, GERMAN, ITALIAN, PORTUGUESE, RUSSIAN, CHINESE, JAPANESE, KOREAN, ARABIC};
   }
}
