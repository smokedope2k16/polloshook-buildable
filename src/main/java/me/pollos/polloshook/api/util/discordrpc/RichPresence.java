package me.pollos.polloshook.api.util.discordrpc;

import com.google.gson.JsonObject;

public class RichPresence {
   private String details;
   private String state;
   private RichPresence.Assets assets;
   private RichPresence.Timestamps timestamps;

   public void setDetails(String details) {
      this.details = details;
   }

   public void setState(String state) {
      this.state = state;
   }

   public void setLargeImage(String key, String text) {
      if (this.assets == null) {
         this.assets = new RichPresence.Assets();
      }

      this.assets.large_image = key;
      this.assets.large_text = text;
   }

   public void setSmallImage(String key, String text) {
      if (this.assets == null) {
         this.assets = new RichPresence.Assets();
      }

      this.assets.small_image = key;
      this.assets.small_text = text;
   }

   public void setStart(long time) {
      if (this.timestamps == null) {
         this.timestamps = new RichPresence.Timestamps();
      }

      this.timestamps.start = time;
   }

   public void setEnd(long time) {
      if (this.timestamps == null) {
         this.timestamps = new RichPresence.Timestamps();
      }

      this.timestamps.end = time;
   }

   public JsonObject toJson() {
      JsonObject o = new JsonObject();
      if (this.details != null) {
         o.addProperty("details", this.details);
      }

      if (this.state != null) {
         o.addProperty("state", this.state);
      }

      JsonObject keyCodec;
      if (this.assets != null) {
         keyCodec = new JsonObject();
         if (this.assets.large_image != null) {
            keyCodec.addProperty("large_image", this.assets.large_image);
         }

         if (this.assets.large_text != null) {
            keyCodec.addProperty("large_text", this.assets.large_text);
         }

         if (this.assets.small_image != null) {
            keyCodec.addProperty("small_image", this.assets.small_image);
         }

         if (this.assets.small_text != null) {
            keyCodec.addProperty("small_text", this.assets.small_text);
         }

         o.add("assets", keyCodec);
      }

      if (this.timestamps != null) {
         keyCodec = new JsonObject();
         if (this.timestamps.start != null) {
            keyCodec.addProperty("start", this.timestamps.start);
         }

         if (this.timestamps.end != null) {
            keyCodec.addProperty("end", this.timestamps.end);
         }

         o.add("timestamps", keyCodec);
      }

      return o;
   }

   public static class Assets {
      public String large_image;
      public String large_text;
      public String small_image;
      public String small_text;
   }

   public static class Timestamps {
      public Long start;
      public Long end;
   }
}
