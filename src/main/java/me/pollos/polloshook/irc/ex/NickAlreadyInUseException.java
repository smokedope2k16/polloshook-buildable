package me.pollos.polloshook.irc.ex;

public class NickAlreadyInUseException extends IrcException {
   public NickAlreadyInUseException(String e) {
      super(e);
   }
}
