package me.pollos.polloshook.impl.command.player;

import java.util.UUID;
import me.pollos.polloshook.PollosHook;
import me.pollos.polloshook.api.command.core.Command;
import me.pollos.polloshook.api.interfaces.Minecraftable;
import me.pollos.polloshook.impl.events.network.ConnectionEvent;
import me.pollos.polloshook.impl.module.player.fakeplayer.utils.FakePlayerEntity;
import net.minecraft.entity.player.PlayerInventory;

public class LogTestCommand extends Command {
   private int i = 0;

   public LogTestCommand() {
      super(new String[]{"LogTest", "logspottest"});
   }

   public String execute(String[] args) {
      UUID uuid = UUID.randomUUID();
      LogTestCommand.LogoutTestEntity entity = new LogTestCommand.LogoutTestEntity("logout_test_" + this.i);
      entity.getInventory().clone(mc.player.getInventory());
      PollosHook.getEventBus().dispatch(new ConnectionEvent.Leave("pollosxd", uuid, entity));
      ++this.i;
      return String.valueOf(this.i) + " concatenated string";
   }


   private static class LogoutTestEntity extends FakePlayerEntity {
      public LogoutTestEntity(String label) {
         super(Minecraftable.mc.world, Minecraftable.mc.player.getGameProfile(), label);
      }

      public PlayerInventory getInventory() {
         return Minecraftable.mc.player.getInventory();
      }
   }
}
