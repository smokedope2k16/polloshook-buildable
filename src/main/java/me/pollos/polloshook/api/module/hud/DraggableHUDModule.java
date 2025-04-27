package me.pollos.polloshook.api.module.hud;

import java.awt.Color;
import java.util.concurrent.TimeUnit;
import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.event.events.Event;
import me.pollos.polloshook.api.util.color.ColorUtil;
import me.pollos.polloshook.api.util.thread.PollosHookThread;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.impl.events.render.Render2DEvent;
import me.pollos.polloshook.impl.gui.editor.core.PollosHUD;
import me.pollos.polloshook.impl.module.other.hud.HUD;
import me.pollos.polloshook.impl.module.other.hud.elements.draggable.arraylist.Arraylist;
import net.minecraft.client.gui.DrawContext;

public abstract class DraggableHUDModule extends HUDModule {
    private float textX = 0.0F;
    private float textY = 0.0F;
    private float textWidth = 0.0F;
    private float textHeight = 0.0F;
    private float boostedX;
    private float boostedY;
    private boolean hovered;
    private DraggableHUDModule.HudPosition position;
    private boolean noClampFlag;
    private boolean initFlag;
    private boolean dragging;
    public static int INIT_WIDTH;
    public static int INIT_HEIGHT;
    private final Value<Boolean> setPos;

    public DraggableHUDModule(String[] aliases) {
        super(aliases);
        this.position = DraggableHUDModule.HudPosition.CUSTOM;
        this.noClampFlag = true;
        this.initFlag = true;
        this.setPos = new Value<>(true, new String[]{"SetPos", "defaultpos"});

        this.offerValues(new Value[]{this.setPos});

        Listener<Render2DEvent> listener2DRender = new Listener<Render2DEvent>(Render2DEvent.class) {
            public void call(Render2DEvent event) {
                DrawContext context = event.getContext();

                if (DraggableHUDModule.INIT_HEIGHT == mc.getWindow().getFramebufferHeight() && DraggableHUDModule.INIT_WIDTH == mc.getWindow().getFramebufferWidth() && DraggableHUDModule.this.initFlag) {
                    DraggableHUDModule.this.noClampFlag = DraggableHUDModule.this.initFlag = false;
                }

                if ((Boolean)DraggableHUDModule.this.setPos.getValue()) {
                    DraggableHUDModule.this.position = DraggableHUDModule.HudPosition.CUSTOM;
                    DraggableHUDModule.this.setDefaultPosition(context);
                } else {
                    DraggableHUDModule.this.position = DraggableHUDModule.HudPosition.getClosestPosition(
                        context,
                        (Float)HUD.get().getSnapRadius().getValue() * 10.0F,
                        DraggableHUDModule.this.textX,
                        DraggableHUDModule.this.textY,
                        DraggableHUDModule.this.textWidth,
                        DraggableHUDModule.this.textHeight
                    );

                    if (!DraggableHUDModule.this.dragging && !(DraggableHUDModule.this instanceof Arraylist)) { 
                        DraggableHUDModule.this.snapToHudPosition(context);
                    }

                    boolean hudEditorScreen = mc.currentScreen instanceof PollosHUD;
                    DraggableHUDModule.this.clampPos(event.getContext());

                    if (hudEditorScreen) {
                        Color white = ColorUtil.changeAlpha(Color.WHITE, 125);

                        if (DraggableHUDModule.this.dragging) {
                            int centerX = context.getScaledWindowWidth() / 2;
                            int centerY = context.getScaledWindowHeight() / 2;
                            context.fill(centerX, 0, centerX + 1, context.getScaledWindowHeight(), white.getRGB());
                            context.fill(0, centerY, context.getScaledWindowWidth(), centerY + 1, white.getRGB());
                        }

                        Color color = DraggableHUDModule.this.hovered ? new Color(4342338) : new Color(7105644);
                        Color finalColor = ColorUtil.changeAlpha(color, 125);

                        int xStart = DraggableHUDModule.this.getFixedX((int)(DraggableHUDModule.this.getTextX() - 1.0F));
                        int xEnd = DraggableHUDModule.this.getFixedX((int)(DraggableHUDModule.this.getTextX() + DraggableHUDModule.this.getTextWidth() + 1.0F));
                        int yStart = (int)(DraggableHUDModule.this.getTextY() - 1.0F);
                        int yEnd = (int)(DraggableHUDModule.this.getTextY() + DraggableHUDModule.this.getTextHeight() + 1.0F);
                        context.fill(xStart, yStart, xEnd, yEnd, finalColor.getRGB());
                    }
                }
            }
        };

        Listener<DraggableHUDModule.DragComponentEvent> listenerDrag = new Listener<DraggableHUDModule.DragComponentEvent>(DraggableHUDModule.DragComponentEvent.class) {
            public void call(DraggableHUDModule.DragComponentEvent event) {
                DraggableHUDModule comp = event.module;
                if (comp == DraggableHUDModule.this && !(Boolean)comp.setPos.getValue()) {
                    DraggableHUDModule.this.setTextX((float)event.posX);
                    DraggableHUDModule.this.setTextY((float)event.posY);
                    DraggableHUDModule.this.clampPos(event.context);
                }
            }
        };

        this.offerListeners(new Listener[]{listener2DRender, listenerDrag});
    }

    protected void onEnable() {
        boolean is0 = this.textY == 0.0F && this.textX == 0.0F && this.textWidth == 0.0F && this.textHeight == 0.0F;
        if (is0) {
            this.setDefaultPosition(new DrawContext(mc, mc.getBufferBuilders().getEntityVertexConsumers()));
        }
    }

    public void onWorldLoad() {
        PollosHookThread.SCHEDULED_EXECUTOR.schedule(() -> {
            return this.noClampFlag = false;
        }, 1L, TimeUnit.MINUTES);
    }

    public abstract void setDefaultPosition(DrawContext var1);

    public boolean isRightToLeftRendering() {
        return false;
    }

    public int getFixedX(int x) {
        return this.isRightToLeftRendering() ? (int)((float)x - this.getTextWidth()) : x;
    }

    private void clampPos(DrawContext context) {
        if (!this.noClampFlag) {
            int maxX = context.getScaledWindowWidth() - 2;
            int maxY = context.getScaledWindowHeight() - 2;
            if (this.isRightToLeftRendering()) {
                if (this.textX < this.textWidth + 2.0F) {
                    this.boostedX = this.textWidth + 2.0F - this.textX;
                    this.textX = this.textWidth + 2.0F;
                } else if (this.textX > (float)(maxX + 2)) {
                    this.boostedX = this.textX - (float)(maxX + 2);
                    this.textX = (float)(maxX + 2);
                }
            } else if (this.textX < 2.0F) {
                this.boostedX = 2.0F - this.textX;
                this.textX = 2.0F;
            } else if (this.textX > (float)maxX - this.textWidth) {
                this.boostedX = this.textX - ((float)maxX - this.textWidth);
                this.textX = (float)maxX - this.textWidth;
            }

            if (this.textY < 1.0F) {
                this.boostedY = 1.0F - this.textY;
                this.textY = 1.0F;
            } else if (this.textY > (float)maxY - this.textHeight + 1.0F) {
                this.boostedY = this.textY - ((float)maxY - this.textHeight + 1.0F);
                this.textY = (float)maxY - this.textHeight + 1.0F;
            }
        }
    }

    public boolean snapToHudPosition(DrawContext context) {
        int screenWidth = context.getScaledWindowWidth();
        int screenHeight = context.getScaledWindowHeight();
        switch(this.getPosition().ordinal()) {
        case 0:
            this.setTextX(0.0F);
            this.setTextY(0.0F);
            break;
        case 1:
            this.setTextX(((float)screenWidth - this.getTextWidth()) / 2.0F);
            this.setTextY(0.0F);
            break;
        case 2:
            this.setTextX((float)screenWidth - this.getTextWidth());
            this.setTextY(0.0F);
            break;
        case 3:
            this.setTextX(0.0F);
            this.setTextY(((float)screenHeight - this.getTextHeight()) / 2.0F);
            break;
        case 4:
            this.setTextX(((float)screenWidth - this.getTextWidth()) / 2.0F);
            this.setTextY(((float)screenHeight - this.getTextHeight()) / 2.0F);
            break;
        case 5:
            this.setTextX((float)screenWidth - this.getTextWidth());
            this.setTextY(((float)screenHeight - this.getTextHeight()) / 2.0F);
            break;
        case 6:
            this.setTextX(0.0F);
            this.setTextY((float)screenHeight - this.getTextHeight());
            break;
        case 7:
        case 8:
            this.setTextX(((float)screenWidth - this.getTextWidth()) / 2.0F);
            this.setTextY((float)screenHeight - this.getTextHeight());
            break;
        case 9:
            this.setTextX((float)screenWidth - this.getTextWidth());
            this.setTextY((float)screenHeight - this.getTextHeight());
            break;
        case 10:
            this.setTextY(0.0F);
            break;
        case 11:
            this.setTextX(0.0F);
            break;
        case 12:
            this.setTextX((float)screenWidth - this.getTextWidth());
            break;
        case 13:
            this.setTextX(((float)screenWidth - this.getTextWidth()) / 2.0F);
            break;
        case 14:
            this.setTextY((float)screenHeight - this.getTextHeight());
            break;
        default:
            return false;
        }

        return true;
    }

    public void setTextX(float textX) {
        this.textX = textX;
    }

    public void setTextY(float textY) {
        this.textY = textY;
    }

    public void setTextWidth(float textWidth) {
        this.textWidth = textWidth;
    }

    public void setTextHeight(float textHeight) {
        this.textHeight = textHeight;
    }

    public void setBoostedX(float boostedX) {
        this.boostedX = boostedX;
    }

    public void setBoostedY(float boostedY) {
        this.boostedY = boostedY;
    }

    public void setHovered(boolean hovered) {
        this.hovered = hovered;
    }

    public void setPosition(DraggableHUDModule.HudPosition position) {
        this.position = position;
    }

    public void setNoClampFlag(boolean noClampFlag) {
        this.noClampFlag = noClampFlag;
    }

    public void setInitFlag(boolean initFlag) {
        this.initFlag = initFlag;
    }

    public void setDragging(boolean dragging) {
        this.dragging = dragging;
    }

    public float getTextX() {
        return this.textX;
    }

    public float getTextY() {
        return this.textY;
    }

    public float getTextWidth() {
        return this.textWidth;
    }

    public float getTextHeight() {
        return this.textHeight;
    }

    public float getBoostedX() {
        return this.boostedX;
    }

    public float getBoostedY() {
        return this.boostedY;
    }

    public boolean isHovered() {
        return this.hovered;
    }

    public DraggableHUDModule.HudPosition getPosition() {
        return this.position;
    }

    public boolean isNoClampFlag() {
        return this.noClampFlag;
    }

    public boolean isInitFlag() {
        return this.initFlag;
    }

    public boolean isDragging() {
        return this.dragging;
    }

    public Value<Boolean> getSetPos() {
        return this.setPos;
    }

    public String toString() {
        String var10000 = super.toString();
        return "DraggableHUDModule(super=" + var10000 + ", textX=" + this.getTextX() + ", textY=" + this.getTextY() + ", textWidth=" + this.getTextWidth() + ", textHeight=" + this.getTextHeight() + ", boostedX=" + this.getBoostedX() + ", boostedY=" + this.getBoostedY() + ", hovered=" + this.isHovered() + ", position=" + String.valueOf(this.getPosition()) + ", noClampFlag=" + this.isNoClampFlag() + ", initFlag=" + this.isInitFlag() + ", dragging=" + this.isDragging() + ", setPos=" + String.valueOf(this.getSetPos()) + ")";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof DraggableHUDModule)) {
            return false;
        } else {
            DraggableHUDModule other = (DraggableHUDModule)o;
            if (!other.canEqual(this)) {
                return false;
            } else if (!super.equals(o)) {
                return false;
            } else if (Float.compare(this.getTextX(), other.getTextX()) != 0) {
                return false;
            } else if (Float.compare(this.getTextY(), other.getTextY()) != 0) {
                return false;
            } else if (Float.compare(this.getTextWidth(), other.getTextWidth()) != 0) {
                return false;
            } else if (Float.compare(this.getTextHeight(), other.getTextHeight()) != 0) {
                return false;
            } else if (Float.compare(this.getBoostedX(), other.getBoostedX()) != 0) {
                return false;
            } else if (Float.compare(this.getBoostedY(), other.getBoostedY()) != 0) {
                return false;
            } else if (this.isHovered() != other.isHovered()) {
                return false;
            } else if (this.isNoClampFlag() != other.isNoClampFlag()) {
                return false;
            } else if (this.isInitFlag() != other.isInitFlag()) {
                return false;
            } else if (this.isDragging() != other.isDragging()) {
                return false;
            } else {
                Object this$position = this.getPosition();
                Object other$position = other.getPosition();
                if (this$position == null) {
                    if (other$position != null) {
                        return false;
                    }
                } else if (!this$position.equals(other$position)) {
                    return false;
                }

                Object this$setPos = this.getSetPos();
                Object other$setPos = other.getSetPos();
                if (this$setPos == null) {
                    if (other$setPos != null) {
                        return false;
                    }
                } else if (!this$setPos.equals(other$setPos)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof DraggableHUDModule;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = result * 59 + Float.floatToIntBits(this.getTextX());
        result = result * 59 + Float.floatToIntBits(this.getTextY());
        result = result * 59 + Float.floatToIntBits(this.getTextWidth());
        result = result * 59 + Float.floatToIntBits(this.getTextHeight());
        result = result * 59 + Float.floatToIntBits(this.getBoostedX());
        result = result * 59 + Float.floatToIntBits(this.getBoostedY());
        result = result * 59 + (this.isHovered() ? 79 : 97);
        result = result * 59 + (this.isNoClampFlag() ? 79 : 97);
        result = result * 59 + (this.isInitFlag() ? 79 : 97);
        result = result * 59 + (this.isDragging() ? 79 : 97);
        Object $position = this.getPosition();
        result = result * 59 + ($position == null ? 43 : $position.hashCode());
        Object $setPos = this.getSetPos();
        result = result * 59 + ($setPos == null ? 43 : $setPos.hashCode());
        return result;
    }

    public static enum HudPosition {
        TOP_LEFT(DraggableHUDModule.Position.LEFT, DraggableHUDModule.Position.TOP),
        TOP_CENTER(DraggableHUDModule.Position.MIDDLE, DraggableHUDModule.Position.TOP),
        TOP_RIGHT(DraggableHUDModule.Position.RIGHT, DraggableHUDModule.Position.TOP),
        MIDDLE_LEFT(DraggableHUDModule.Position.LEFT, DraggableHUDModule.Position.MIDDLE),
        MIDDLE_CENTER(DraggableHUDModule.Position.MIDDLE, DraggableHUDModule.Position.MIDDLE),
        MIDDLE_RIGHT(DraggableHUDModule.Position.RIGHT, DraggableHUDModule.Position.MIDDLE),
        BOTTOM_LEFT(DraggableHUDModule.Position.LEFT, DraggableHUDModule.Position.BOTTOM),
        BOTTOM_CENTER_L(DraggableHUDModule.Position.RIGHT, DraggableHUDModule.Position.BOTTOM),
        BOTTOM_CENTER_R(DraggableHUDModule.Position.LEFT, DraggableHUDModule.Position.BOTTOM),
        BOTTOM_RIGHT(DraggableHUDModule.Position.RIGHT, DraggableHUDModule.Position.BOTTOM),
        TOP(DraggableHUDModule.Position.CUSTOM, DraggableHUDModule.Position.TOP),
        LEFT(DraggableHUDModule.Position.LEFT, DraggableHUDModule.Position.CUSTOM),
        RIGHT(DraggableHUDModule.Position.RIGHT, DraggableHUDModule.Position.CUSTOM),
        CENTER(DraggableHUDModule.Position.MIDDLE, DraggableHUDModule.Position.CUSTOM),
        BOTTOM(DraggableHUDModule.Position.CUSTOM, DraggableHUDModule.Position.BOTTOM),
        CUSTOM(DraggableHUDModule.Position.CUSTOM, DraggableHUDModule.Position.CUSTOM);

        private final DraggableHUDModule.Position first;
        private final DraggableHUDModule.Position second;

        public static DraggableHUDModule.HudPosition getClosestPosition(DrawContext context, float threshold, float x, float y, float width, float height) {
            int scaledWidth = context.getScaledWindowWidth();
            int scaledHeight = context.getScaledWindowHeight();
            boolean isNearTop = y <= threshold;
            boolean isNearBottom = y + height >= (float)scaledHeight - threshold;
            boolean isNearLeft = x <= threshold;
            boolean isNearRight = x + width >= (float)scaledWidth - threshold;
            boolean isNearCenterX = Math.abs(x + width / 2.0F - (float)scaledWidth / 2.0F) <= threshold;
            boolean isNearCenterY = Math.abs(y + height / 2.0F - (float)scaledHeight / 2.0F) <= threshold;
            if (isNearTop && isNearLeft) {
                return TOP_LEFT;
            } else if (isNearTop && isNearCenterX) {
                return TOP_CENTER;
            } else if (isNearTop && isNearRight) {
                return TOP_RIGHT;
            } else if (isNearCenterY && isNearLeft) {
                return MIDDLE_LEFT;
            } else if (isNearCenterY && isNearCenterX) {
                return MIDDLE_CENTER;
            } else if (isNearCenterY && isNearRight) {
                return MIDDLE_RIGHT;
            } else if (isNearBottom && isNearLeft) {
                return BOTTOM_LEFT;
            } else if (isNearBottom && isNearCenterX) {
                return BOTTOM_CENTER_L;
            } else if (isNearBottom && isNearRight) {
                return BOTTOM_RIGHT;
            } else if (isNearTop) {
                return TOP;
            } else if (isNearLeft) {
                return LEFT;
            } else if (isNearRight) {
                return RIGHT;
            } else if (isNearCenterX) {
                return CENTER;
            } else {
                return isNearBottom ? BOTTOM : CUSTOM;
            }
        }

        public DraggableHUDModule.Position getFirst() {
            return this.first;
        }

        public DraggableHUDModule.Position getSecond() {
            return this.second;
        }

        private HudPosition(final DraggableHUDModule.Position first, final DraggableHUDModule.Position second) {
            this.first = first;
            this.second = second;
        }

        public String toString() {
            String var10000 = this.name();
            return "DraggableHUDModule.HudPosition." + var10000 + "(first=" + String.valueOf(this.getFirst()) + ", second=" + String.valueOf(this.getSecond()) + ")";
        }
    }

    public static final class DragComponentEvent extends Event {
        private final DraggableHUDModule module;
        private final DrawContext context;
        private final int posX;
        private final int posY;

        public DraggableHUDModule getModule() {
            return this.module;
        }

        public DrawContext getContext() {
            return this.context;
        }

        public int getPosX() {
            return this.posX;
        }

        public int getPosY() {
            return this.posY;
        }

        public DragComponentEvent(DraggableHUDModule module, DrawContext context, int posX, int posY) {
            this.module = module;
            this.context = context;
            this.posX = posX;
            this.posY = posY;
        }
    }

    public static enum Position {
        LEFT,
        TOP,
        RIGHT,
        MIDDLE,
        BOTTOM,
        CUSTOM;
    }
}