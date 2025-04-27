package me.pollos.polloshook.api.value.value.list.toggleable.block;

import me.pollos.polloshook.api.interfaces.Toggleable;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKey;

public class ToggleableBlock implements Toggleable {
    private final Block block;
    private boolean enabled;

    public ToggleableBlock(Block block, boolean enabled) {
        this.block = block;
        this.enabled = enabled;
    }

    public void toggle() {
        this.enabled = !this.enabled;
    }

    public String getBlockName() {
        return this.block.getName().getString();
    }

    public RegistryKey<Block> getBlockRegistryEntry() {
        return this.block.getRegistryEntry().registryKey();
    }

    public Block getBlock() {
        return this.block;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ToggleableBlock)) {
            return false;
        }
        ToggleableBlock other = (ToggleableBlock) o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.enabled != other.enabled) {
            return false;
        }
        Block thisBlock = this.block;
        Block otherBlock = other.block;
        return thisBlock == null ? otherBlock == null : thisBlock.equals(otherBlock);
    }

    protected boolean canEqual(Object other) {
        return other instanceof ToggleableBlock;
    }

    public int hashCode() {
        int result = 1;
        result = 59 * result + (this.enabled ? 79 : 97);
        Block block = this.block;
        result = 59 * result + (block == null ? 43 : block.hashCode());
        return result;
    }

    public String toString() {
        return "ToggleableBlock(block=" + this.block + ", enabled=" + this.enabled + ")";
    }
}