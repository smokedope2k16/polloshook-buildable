package me.pollos.polloshook.impl.module.render.newchunks.util;

public class ChunkData {
    private int x;
    private int z;

    public ChunkData(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public int getX() {
        return this.x;
    }

    public int getZ() {
        return this.z;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ChunkData)) {
            return false;
        }
        ChunkData other = (ChunkData) o;
        if (!other.canEqual(this)) {
            return false;
        }
        return this.x == other.x && this.z == other.z;
    }

    protected boolean canEqual(Object other) {
        return other instanceof ChunkData;
    }

    public int hashCode() {
        int result = 1;
        result = 59 * result + this.x;
        result = 59 * result + this.z;
        return result;
    }

    public String toString() {
        return "ChunkData(x=" + this.x + ", z=" + this.z + ")";
    }
}