package me.pollos.polloshook.api.minecraft;

import java.util.Optional;
import java.util.UUID;
import me.pollos.polloshook.api.interfaces.Minecraftable;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.asm.ducks.IMinecraftClient;
import net.minecraft.client.session.Session;
import net.minecraft.client.session.Session.AccountType;

public class SessionUtil implements Minecraftable {
   public static void setSession(String name) {
      name = name.length() > 16 ? name.substring(0, 16) : name;
      UUID uuid = UUID.nameUUIDFromBytes(name.getBytes());
      String token = mc.getSession().getAccessToken();
      Session session = new Session(name, uuid, token, Optional.empty(), Optional.empty(), AccountType.LEGACY);
      ((IMinecraftClient)mc).setSession(session);
      Managers.getIrcManager().changeUsername(name);
   }
}
