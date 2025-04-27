package me.pollos.polloshook.api.util.obj.hole;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import me.pollos.polloshook.api.interfaces.Minecraftable;
import me.pollos.polloshook.api.minecraft.rotations.FacingUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class Hole implements Minecraftable {
    private final BlockPos pos;
    private final SafetyEnum safety;

    public Hole(BlockPos pos, SafetyEnum safety) {
        this.pos = pos;
        this.safety = safety;
    }

    public List<BlockPos> getBlocks() {
        if (this.pos == null) {
            return Collections.emptyList();
        } else {
            List<BlockPos> positions = new ArrayList();
            Direction[] directions = FacingUtil.DOWN;
            
            for (Direction dir : directions) {
                positions.add(this.pos.offset(dir));
            }

            positions.removeIf(pos -> mc.world.getBlockState(pos).isAir());
            return positions;
        }
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public SafetyEnum getSafety() {
        return this.safety;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof Hole)) {
            return false;
        } else {
            Hole other = (Hole)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                BlockPos thisPos = this.getPos();
                BlockPos otherPos = other.getPos();
                if (thisPos == null) {
                    if (otherPos != null) {
                        return false;
                    }
                } else if (!thisPos.equals(otherPos)) {
                    return false;
                }

                SafetyEnum thisSafety = this.getSafety();
                SafetyEnum otherSafety = other.getSafety();
                if (thisSafety == null) {
                    if (otherSafety != null) {
                        return false;
                    }
                } else if (!thisSafety.equals(otherSafety)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof Hole;
    }

    public int hashCode() {
        int result = 1;
        BlockPos pos = this.getPos();
        result = result * 59 + (pos == null ? 43 : pos.hashCode());
        SafetyEnum safety = this.getSafety();
        result = result * 59 + (safety == null ? 43 : safety.hashCode());
        return result;
    }

    public String toString() {
        return "Hole(pos=" + this.getPos() + ", safety=" + this.getSafety() + ")";
    }
}