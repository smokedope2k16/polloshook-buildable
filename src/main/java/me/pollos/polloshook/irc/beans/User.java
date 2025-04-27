package me.pollos.polloshook.irc.beans;

public class User {
   private String _prefix;
   private String _nick;
   private String _lowerNick;

   public User(String prefix, String nick) {
      this._prefix = prefix;
      this._nick = nick;
      this._lowerNick = nick.toLowerCase();
   }

   public String getPrefix() {
      return this._prefix;
   }

   public boolean isOp() {
      return this._prefix.indexOf(64) >= 0;
   }

   public boolean hasVoice() {
      return this._prefix.indexOf(43) >= 0;
   }

   public String getNick() {
      return this._nick;
   }

   public String toString() {
      String var10000 = this.getPrefix();
      return var10000 + this.getNick();
   }

   public boolean equals(String nick) {
      return nick.toLowerCase().equals(this._lowerNick);
   }

   public boolean equals(Object o) {
      if (o instanceof User) {
         User other = (User)o;
         return other._lowerNick.equals(this._lowerNick);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this._lowerNick.hashCode();
   }

   public int compareTo(Object o) {
      if (o instanceof User) {
         User other = (User)o;
         return other._lowerNick.compareTo(this._lowerNick);
      } else {
         return -1;
      }
   }
}
