package me.pollos.polloshook.api.value.value.targeting;

public class TargetPreset {
    private boolean targetPlayers;
    private boolean targetMonsters;
    private boolean targetFriendlies;
    private boolean ignoreInvis;
    private boolean ignoreNaked;
    private final Enum<?> target;
    public static TargetPreset DEFAULT;

    public TargetPreset(boolean targetPlayers, boolean targetMonsters, 
                      boolean targetFriendlies, boolean ignoreInvis, 
                      boolean ignoreNaked, Enum<?> target) {
        this.targetPlayers = targetPlayers;
        this.targetMonsters = targetMonsters;
        this.targetFriendlies = targetFriendlies;
        this.ignoreInvis = ignoreInvis;
        this.ignoreNaked = ignoreNaked;
        this.target = target;
    }

    public boolean isTargetPlayers() {
        return this.targetPlayers;
    }

    public boolean isTargetMonsters() {
        return this.targetMonsters;
    }

    public boolean isTargetFriendlies() {
        return this.targetFriendlies;
    }

    public boolean isIgnoreInvis() {
        return this.ignoreInvis;
    }

    public boolean isIgnoreNaked() {
        return this.ignoreNaked;
    }

    public Enum<?> getTarget() {
        return this.target;
    }

    public void setTargetPlayers(boolean targetPlayers) {
        this.targetPlayers = targetPlayers;
    }

    public void setTargetMonsters(boolean targetMonsters) {
        this.targetMonsters = targetMonsters;
    }

    public void setTargetFriendlies(boolean targetFriendlies) {
        this.targetFriendlies = targetFriendlies;
    }

    public void setIgnoreInvis(boolean ignoreInvis) {
        this.ignoreInvis = ignoreInvis;
    }

    public void setIgnoreNaked(boolean ignoreNaked) {
        this.ignoreNaked = ignoreNaked;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof TargetPreset)) {
            return false;
        }
        TargetPreset other = (TargetPreset) o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.targetPlayers != other.targetPlayers) {
            return false;
        }
        if (this.targetMonsters != other.targetMonsters) {
            return false;
        }
        if (this.targetFriendlies != other.targetFriendlies) {
            return false;
        }
        if (this.ignoreInvis != other.ignoreInvis) {
            return false;
        }
        if (this.ignoreNaked != other.ignoreNaked) {
            return false;
        }
        Enum<?> thisTarget = this.target;
        Enum<?> otherTarget = other.target;
        return thisTarget == null ? otherTarget == null : thisTarget.equals(otherTarget);
    }

    protected boolean canEqual(Object other) {
        return other instanceof TargetPreset;
    }

    public int hashCode() {
        int result = 1;
        result = 59 * result + (this.targetPlayers ? 79 : 97);
        result = 59 * result + (this.targetMonsters ? 79 : 97);
        result = 59 * result + (this.targetFriendlies ? 79 : 97);
        result = 59 * result + (this.ignoreInvis ? 79 : 97);
        result = 59 * result + (this.ignoreNaked ? 79 : 97);
        Enum<?> target = this.target;
        result = 59 * result + (target == null ? 43 : target.hashCode());
        return result;
    }

    public String toString() {
        return "TargetPreset(targetPlayers=" + this.targetPlayers + 
               ", targetMonsters=" + this.targetMonsters + 
               ", targetFriendlies=" + this.targetFriendlies + 
               ", ignoreInvis=" + this.ignoreInvis + 
               ", ignoreNaked=" + this.ignoreNaked + 
               ", target=" + this.target + ")";
    }

    static {
        DEFAULT = new TargetPreset(true, false, false, false, false, TargetMode.DISTANCE);
    }
}