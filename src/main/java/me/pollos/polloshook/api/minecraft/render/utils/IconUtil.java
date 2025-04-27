package me.pollos.polloshook.api.minecraft.render.utils;

import com.google.common.collect.Lists;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import me.pollos.polloshook.api.util.logging.ClientLogger;
import net.minecraft.resource.InputSupplier;
import org.apache.commons.compress.utils.IOUtils;

public class IconUtil {
   public static List<InputSupplier<InputStream>> getAllPngResources() {
      List<String> icons = Lists.newArrayList();
      icons.add("icon_16x16.png");
      icons.add("icon_32x32.png");
      icons.add("icon_48x48.png");
      icons.add("icon_128x128.png");
      icons.add("icon_256x256.png");
      return icons.stream().map(IconUtil::getResource).toList();
   }

   public static InputSupplier<InputStream> getResource(String path) {
      String fullPath = "/assets/icons/" + path;
      byte[] data = null;
      
      try (InputStream inputstream = IconUtil.class.getResourceAsStream(fullPath)) {
          if (inputstream != null) {
              data = IOUtils.toByteArray(inputstream);
          }
      } catch (IOException var8) {
          ClientLogger.getLogger().info("Couldn't set icon: " + var8.getMessage());
      }
      
      if (data == null) {
          throw new RuntimeException("Unexpected resource path " + path);
      } else {
          final byte[] finalData = data;
          return () -> new ByteArrayInputStream(finalData);
      }
  }
}
