package me.pollos.polloshook.api.util.thread.execption;

public class UnsupportedLoaderVersionException extends RuntimeException {
   public UnsupportedLoaderVersionException() {
      super("Unsupported Loader Version (re-install fabric with loader version as %s)".formatted(new Object[]{"0.16.9"}));
   }
}
