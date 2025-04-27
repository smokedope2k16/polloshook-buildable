package me.pollos.polloshook.api.module.hud;

import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.module.hud.interfaces.Element2D;
import me.pollos.polloshook.impl.events.render.Render2DEvent;
import net.minecraft.client.gui.screen.ChatScreen;

public abstract class HUDModule extends ToggleableModule implements Element2D {
    public static Element2D GLOBAL_ELEMENT = (context) -> {
    };
    protected boolean isChatOpened;

    public HUDModule(String[] aliases) {
        super(aliases, Category.ELEMENTS);
        Listener<Render2DEvent> render2DEventListener = new Listener<Render2DEvent>(Render2DEvent.class) {
            public void call(Render2DEvent event) {
                if (!mc.options.hudHidden && !mc.getDebugHud().shouldShowDebugHud()) {
                    HUDModule.this.isChatOpened = mc.currentScreen instanceof ChatScreen;
                    if (!mc.options.hudHidden) {
                        HUDModule.this.draw(event.getContext());
                    }
                }
            }
        };
        this.offerListeners(new Listener[]{render2DEventListener});
    }

    public String toString() {
        String var10000 = super.toString();
        return "HUDModule(super=" + var10000 + ", isChatOpened=" + this.isChatOpened + ")";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof HUDModule)) {
            return false;
        } else {
            HUDModule other = (HUDModule)o;
            if (!other.canEqual(this)) {
                return false;
            } else if (!super.equals(o)) {
                return false;
            } else {
                return this.isChatOpened == other.isChatOpened;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof HUDModule;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = result * 59 + (this.isChatOpened ? 79 : 97);
        return result;
    }
}