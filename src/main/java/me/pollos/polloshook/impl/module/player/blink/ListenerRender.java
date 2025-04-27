package me.pollos.polloshook.impl.module.player.blink;

import me.pollos.polloshook.api.event.listener.SafeModuleListener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.entity.PlayerUtil;
import me.pollos.polloshook.api.module.hud.interfaces.Element2D;
import me.pollos.polloshook.api.util.math.MathUtil;
import me.pollos.polloshook.impl.events.render.Render2DEvent;
import me.pollos.polloshook.impl.module.player.blink.mode.BlinkMode;
import me.pollos.polloshook.impl.module.player.blink.mode.PulseMode;
import net.minecraft.client.gui.DrawContext;

public class ListenerRender extends SafeModuleListener<Blink, Render2DEvent> {

    public ListenerRender(Blink module) {
        super(module, Render2DEvent.class);
    }

    public void safeCall(Render2DEvent event) {
        if ((Boolean)((Blink)this.module).subhuman.getValue()) {
            if (((Blink)this.module).mode.getValue() != BlinkMode.FAKE_LAG) {
                DrawContext context = event.getContext();
                double time = MathUtil.round((double)((float)((Blink)this.module).timer.getTime() / 10000.0F), 1);
                int packetsSize = ((Blink)this.module).queue.size();
                this.renderText(context, "Holding %s%s packets".formatted(new Object[]{packetsSize, ((Blink)this.module).mode.getValue() == BlinkMode.PULSE && ((Blink)this.module).pulse.getValue() == PulseMode.PACKETS ? "/" + String.valueOf(((Blink)this.module).packets.getValue()) : ""}), 22);
                
                if (((Blink)this.module).mode.getValue() == BlinkMode.PULSE) {
                    switch((PulseMode)((Blink)this.module).pulse.getValue()) {
                        case DISTANCE:
                            if (!PlayerUtil.isNull() && ((Blink)this.module).lastVec3d != null) {
                                this.renderText(context, "%.1fm away from blink point".formatted(new Object[]{StrictMath.sqrt(mc.player.squaredDistanceTo(((Blink)this.module).lastVec3d))}), 32);
                                break;
                            }
                            return;
                        case TIME:
                            this.renderText(context, "Sending packets in %s".formatted(new Object[]{Math.min(((Blink)this.module).timer.getTime(), (long)(Integer)((Blink)this.module).delay.getValue())}), 32);
                            break;
                        case PACKETS:
                            this.renderText(context, "Packet count: %d/%d".formatted(new Object[]{packetsSize, ((Blink)this.module).packets.getValue()}), 32);
                            break;
                    }
                }

                if (((Blink)this.module).pulse.getValue() != PulseMode.TIME || !((Blink)this.module).pulse.getParent().isVisible()) {
                    this.renderText(context, "Blinked for %ss".formatted(new Object[]{time}), 12);
                }
            }
        }
    }

    private void renderText(DrawContext context, String string, int y) {
        int textWidth = (int)Managers.getTextManager().getWidth(string);
        final int center = (context.getScaledWindowWidth() - textWidth) / 2;
        
        Element2D element = new Element2D() {
            @Override
            public void draw(DrawContext context) {
                this.drawText(context, string, center, y);
            }
        };
        
        element.draw(context);
    }
}