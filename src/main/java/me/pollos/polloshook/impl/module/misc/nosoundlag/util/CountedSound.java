package me.pollos.polloshook.impl.module.misc.nosoundlag.util;

import net.minecraft.util.Identifier;

public class CountedSound {
    private final Identifier identifier;
    private int count = 0;
    private long lastPlayed = System.currentTimeMillis();

    public CountedSound(Identifier identifier) {
        this.identifier = identifier;
    }

    public boolean isEqualSound(Identifier other) {
        return other != null && this.identifier != null && other.getPath().equals(this.identifier.getPath());
    }

    public void increase() {
        ++this.count;
    }

    public void decrease() {
        if (this.count > 0) {
            --this.count;
        }
    }

    public void merge(CountedSound other) {
        if (other != null && this.identifier.getPath().equals(other.getIdentifier().getPath())) {
            this.count += other.getCount();
        }
    }

    public Identifier getIdentifier() {
        return this.identifier;
    }

    public int getCount() {
        return this.count;
    }

    public long getLastPlayed() {
        return this.lastPlayed;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setLastPlayed(long lastPlayed) {
        this.lastPlayed = lastPlayed;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof CountedSound)) {
            return false;
        }
        CountedSound other = (CountedSound) o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.count != other.count) {
            return false;
        }
        if (this.lastPlayed != other.lastPlayed) {
            return false;
        }
        Identifier thisIdentifier = this.identifier;
        Identifier otherIdentifier = other.identifier;
        if (thisIdentifier == null) {
            if (otherIdentifier != null) {
                return false;
            }
        } else if (!thisIdentifier.equals(otherIdentifier)) {
            return false;
        }
        return true;
    }

    protected boolean canEqual(Object other) {
        return other instanceof CountedSound;
    }

    public int hashCode() {
        int result = 1;
        result = 59 * result + this.count;
        long lastPlayed = this.lastPlayed;
        result = 59 * result + (int)(lastPlayed >>> 32 ^ lastPlayed);
        Identifier identifier = this.identifier;
        result = 59 * result + (identifier == null ? 43 : identifier.hashCode());
        return result;
    }

    public String toString() {
        return "CountedSound(identifier=" + this.identifier + 
               ", count=" + this.count + 
               ", lastPlayed=" + this.lastPlayed + ")";
    }
}