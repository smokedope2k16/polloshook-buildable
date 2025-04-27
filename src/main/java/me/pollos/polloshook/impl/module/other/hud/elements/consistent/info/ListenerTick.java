package me.pollos.polloshook.impl.module.other.hud.elements.consistent.info;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.util.thread.PollosHookThread;
import me.pollos.polloshook.api.util.thread.interfaces.SafeRunnable;
import me.pollos.polloshook.impl.events.update.TickEvent;

public class ListenerTick extends ModuleListener<Info, TickEvent> {
   private static final String API_URL = "https://api.coincap.io/v2/assets/bitcoin";
   private static final long TIMEOUT = 30000L;

   public ListenerTick(Info module) {
      super(module, TickEvent.class);
   }

   public void call(TickEvent event) {
      if ((Boolean)((Info)this.module).btc.getValue()) {
         if (((Info)this.module).timer.passed(30000L)) {
            ((Info)this.module).timer.reset();
            SafeRunnable runnable = () -> {
               HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10L)).build();
               HttpRequest request = HttpRequest.newBuilder().uri(URI.create("https://api.coincap.io/v2/assets/bitcoin")).GET().build();
               HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
               String responseBody = (String)response.body();
               JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
               JsonObject dataObject = jsonObject.getAsJsonObject("data");
               if (dataObject != null && dataObject.has("priceUsd")) {
                  ((Info)this.module).price = dataObject.get("priceUsd").getAsDouble();
               }

            };
            PollosHookThread.submit(runnable);
         }

      }
   }
}
