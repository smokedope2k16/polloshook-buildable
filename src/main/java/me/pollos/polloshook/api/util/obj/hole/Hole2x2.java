package me.pollos.polloshook.api.util.obj.hole;

import net.minecraft.util.math.BlockPos;

public class Hole2x2 extends Hole2x1 {
    protected final BlockPos thirdPos;
    protected final BlockPos fourthPos;

    public Hole2x2(BlockPos pos, BlockPos secondPos, BlockPos thirdPos, BlockPos fourthPos, SafetyEnum safety) {
        super(pos, secondPos, safety);
        this.thirdPos = thirdPos;
        this.fourthPos = fourthPos;
    }

    public BlockPos getThirdPos() {
        return this.thirdPos;
    }

    public BlockPos getFourthPos() {
        return this.fourthPos;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof Hole2x2)) {
            return false;
        } else {
            Hole2x2 other = (Hole2x2)o;
            if (!other.canEqual(this)) {
                return false;
            } else if (!super.equals(o)) {
                return false;
            } else {
                BlockPos thisThirdPos = this.getThirdPos();
                BlockPos otherThirdPos = other.getThirdPos();
                if (thisThirdPos == null) {
                    if (otherThirdPos != null) {
                        return false;
                    }
                } else if (!thisThirdPos.equals(otherThirdPos)) {
                    return false;
                }

                BlockPos thisFourthPos = this.getFourthPos();
                BlockPos otherFourthPos = other.getFourthPos();
                if (thisFourthPos == null) {
                    if (otherFourthPos != null) {
                        return false;
                    }
                } else if (!thisFourthPos.equals(otherFourthPos)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof Hole2x2;
    }

    public int hashCode() {
        int result = super.hashCode();
        BlockPos thirdPos = this.getThirdPos();
        result = result * 59 + (thirdPos == null ? 43 : thirdPos.hashCode());
        BlockPos fourthPos = this.getFourthPos();
        result = result * 59 + (fourthPos == null ? 43 : fourthPos.hashCode());
        return result;
    }

    public String toString() {
        return "Hole2x2(super=" + super.toString() + 
               ", thirdPos=" + this.getThirdPos() + 
               ", fourthPos=" + this.getFourthPos() + ")";
    }
}