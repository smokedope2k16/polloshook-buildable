package me.pollos.polloshook.impl.module.misc.chatappend.mode;



public enum ChatAppendMode {
   POLLOSHOOK(" | \ud835\udcc5\ud835\udc5c\ud835\udcc1\ud835\udcc1\ud835\udc5c\ud835\udcc8\ud835\udcbd\ud835\udc5c\ud835\udc5c\ud835\udcc0"),
   POLLOSHACK(" ⏐ \ud835\ude7f\ud835\ude7e\ud835\ude7b\ud835\ude7b\ud835\ude7e\ud835\ude82\ud835\ude77\ud835\ude70\ud835\ude72\ud835\ude7a"),
   SEXMASTER(" ⏐ ＳｅꇓＭΛｓƬεʀ．ＣＣ"),
   CHACHOOXWARE(" ᴄʜᴀᴄʜᴏᴏхᴡᴀʀᴇ"),
   SR9MM(" ⏐ ʙʀᴀɴᴋ1"),
   YAKIGOD(" ⏐ ʏᴀᴋɪɢᴏᴅ.ᴄᴄ"),
   FUTURECLIENT(" | Futureclient v2.71.1-beta"),
   OFF("   Hello Nigga");

   private final String string;

   
   private ChatAppendMode(final String string) {
      this.string = string;
   }

   
   public String getString() {
      return this.string;
   }

   // $FF: synthetic method
   private static ChatAppendMode[] $values() {
      return new ChatAppendMode[]{POLLOSHOOK, POLLOSHACK, SEXMASTER, CHACHOOXWARE, SR9MM, YAKIGOD, FUTURECLIENT, OFF};
   }
}
