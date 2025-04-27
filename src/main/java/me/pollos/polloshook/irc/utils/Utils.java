package me.pollos.polloshook.irc.utils;

public class Utils {
   public static int[] longToIp(long address) {
      int[] ip = new int[4];

      for(int i = 3; i >= 0; --i) {
         ip[i] = (int)(address % 256L);
         address /= 256L;
      }

      return ip;
   }

   public static long ipToLong(byte[] address) {
      if (address.length != 4) {
         throw new IllegalArgumentException("byte array must be of length 4");
      } else {
         long ipNum = 0L;
         long multiplier = 1L;

         for(int i = 3; i >= 0; --i) {
            int byteVal = (address[i] + 256) % 256;
            ipNum += (long)byteVal * multiplier;
            multiplier *= 256L;
         }

         return ipNum;
      }
   }
}
