package me.pollos.polloshook.api.util.thread;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;


public final class FileUtil {
   public static void handleFileCreation(File file) {
      try {
         if (!file.exists()) {
            file.createNewFile();
         }

      } catch (Throwable var2) {}
   }

   public static BufferedWriter createWriter(File file) {
      try {
         return new BufferedWriter(new FileWriter(file));
      } catch (Throwable var2) {}
      return null;
   }

   public static BufferedReader createBufferedReader(File file) {
      try {
         return new BufferedReader(new FileReader(file));
      } catch (Throwable var2) {}
      return null;
   }

   public static FileReader createReader(File file) {
      try {
         return new FileReader(file);
      } catch (Throwable var2) {
         return null;
      }
   }

   public static void writeOnLine(BufferedWriter writer, String str) {
      try {
         writer.write(str);
      } catch (Throwable var3) {}
   }

   public static void writeThenNewLine(BufferedWriter writer, String str) {
      try {
         writer.write(str);
         writer.newLine();
      } catch (Throwable var3) {}
   }

   public static void closeWriter(BufferedWriter writer) {
      try {
         writer.close();
      } catch (Throwable var2) {}
   }

   public static void closeReader(BufferedReader reader) {
      try {
         reader.close();
      } catch (Throwable var2) {}
   }

   
   private FileUtil() {
      throw new UnsupportedOperationException("This is keyCodec utility class and cannot be instantiated");
   }
}
