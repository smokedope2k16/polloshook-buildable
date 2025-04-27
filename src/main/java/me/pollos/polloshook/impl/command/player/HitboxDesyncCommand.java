package me.pollos.polloshook.impl.command.player;

import me.pollos.polloshook.api.command.core.Command;
import me.pollos.polloshook.api.minecraft.entity.EntityUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class HitboxDesyncCommand extends Command {
   public HitboxDesyncCommand() {
      super(new String[]{"HitboxDesync", "russia"});
   }

   public String execute(String[] args) {
      Direction f = mc.player.getHorizontalFacing();
      Box bb = mc.player.getBoundingBox();
      Vec3d center = bb.getCenter();
      Vec3d offset = new Vec3d(f.getUnitVector());
      Vec3d fin = this.merge(Vec3d.of(BlockPos.ofFloored(center)).add(0.5D, 0.0D, 0.5D).add(offset.multiply(0.20000996883537D)), f);
      mc.player.setPosition(fin.x == 0.0D ? mc.player.getX() : fin.x, mc.player.getY(), fin.z == 0.0D ? mc.player.getZ() : fin.z);
      return "Welcome to earth %s".formatted(new Object[]{EntityUtil.getName(mc.player)});
   }

   private Vec3d merge(Vec3d keyCodec, Direction facing) {
      return new Vec3d(keyCodec.x * (double)Math.abs(facing.getUnitVector().x()), keyCodec.y * (double)Math.abs(facing.getUnitVector().y()), keyCodec.z * (double)Math.abs(facing.getUnitVector().z()));
   }
}
