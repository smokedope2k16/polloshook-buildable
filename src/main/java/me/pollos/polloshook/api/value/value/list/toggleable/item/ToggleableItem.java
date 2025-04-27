package me.pollos.polloshook.api.value.value.list.toggleable.item;

import me.pollos.polloshook.api.interfaces.Toggleable;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKey;

public class ToggleableItem implements Toggleable {
    private final Item item;
    private boolean enabled;

    public ToggleableItem(Item item, boolean enabled) {
        this.item = item;
        this.enabled = enabled;
    }

    public void toggle() {
        this.enabled = !this.enabled;
    }

    public String getItemName() {
        return this.item.getName().getString();
    }

    public RegistryKey<Item> getItemRegistryEntry() {
        return this.item.getRegistryEntry().registryKey();
    }

    public Item getItem() {
        return this.item;
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
        if (!(o instanceof ToggleableItem)) {
            return false;
        }
        ToggleableItem other = (ToggleableItem) o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.enabled != other.enabled) {
            return false;
        }
        Item thisItem = this.item;
        Item otherItem = other.item;
        return thisItem == null ? otherItem == null : thisItem.equals(otherItem);
    }

    protected boolean canEqual(Object other) {
        return other instanceof ToggleableItem;
    }

    public int hashCode() {
        int result = 1;
        result = 59 * result + (this.enabled ? 79 : 97);
        Item item = this.item;
        result = 59 * result + (item == null ? 43 : item.hashCode());
        return result;
    }

    public String toString() {
        return "ToggleableItem(item=" + this.item + ", enabled=" + this.enabled + ")";
    }
}