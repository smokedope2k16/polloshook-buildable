package me.pollos.polloshook.api.util.math;

import net.minecraft.util.math.Vec3d;

public class TimeVec3d extends Vec3d {
    private final long time;

    public TimeVec3d(Vec3d vec) {
        super(vec.x, vec.y, vec.z);
        this.time = System.currentTimeMillis();
    }

    public TimeVec3d(double xIn, double yIn, double zIn, long time) {
        super(xIn, yIn, zIn);
        this.time = time;
    }

    public long getTime() {
        return this.time;
    }
}