package me.pollos.polloshook.impl.gui.click.frame;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.render.Render2DMethods;
import me.pollos.polloshook.impl.gui.click.component.Component;
import me.pollos.polloshook.impl.gui.click.component.ModuleComponent;
import me.pollos.polloshook.impl.gui.click.component.values.ValueComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;

public class CategoryFrame extends Frame {


    public CategoryFrame(Screen parentScreen, String label, float posX, float posY, float width, float height) {
        super(parentScreen, label, posX, posY, width, height);
        this.setExtended(true);
    }

    @Override
    public void moved(float posX, float posY) {
        super.moved(posX, posY);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        Render2DMethods.drawRect(context, this.getPosX(), this.getPosY(), this.getPosX() + this.getWidth(), this.getPosY() + this.getHeight(), this.getColor().getRGB());
        Managers.getTextManager().drawString(context, this.getLabel(), (double)((int)(this.getPosX() + 3.0F)), (double)((int)(this.getPosY() + this.getHeight() / 2.0F - (float)(Managers.getTextManager().getHeight() >> 1))), -1);

        if (this.isExtended()) {
            Render2DMethods.drawRect(context, this.getPosX(), this.getPosY() + this.getHeight(), this.getPosX() + this.getWidth(), this.getPosY() + this.getHeight() + 1.0F + this.getCurrentHeight(), 1996488704);

            context.getMatrices().push();
            float scrollMaxHeight = (float)MinecraftClient.getInstance().getWindow().getScaledHeight() - (this.getPosY() + this.getHeight());
            Render2DMethods.scissor((float)((int)this.getPosX()), (float)((int)(this.getPosY() + this.getHeight() + 1.0F)), (float)((int)(this.getPosX() + this.getWidth())), (float)((int)(this.getPosY() + this.getHeight() + Math.min(this.getCurrentHeight(), scrollMaxHeight) + 1.0F)));

            this.getComponents().forEach((component) -> {
                component.render(context, mouseX, mouseY, delta);
            });

            RenderSystem.disableScissor();
            context.getMatrices().pop();
            this.updatePositions();
        }
    }

    @Override
    public void keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.isExtended()) {
             this.getComponents().forEach((component) -> {
                component.keyPressed(keyCode, scanCode, modifiers);
             });
        }
        super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        float scrollMaxHeight = (float)MinecraftClient.getInstance().getWindow().getScaledHeight() - (this.getPosY() + this.getHeight());
        if (this.isExtended() && Render2DMethods.mouseWithinBounds(mouseX, mouseY, (double)this.getPosX(), (double)(this.getPosY() + this.getHeight()), (double)this.getWidth(), (double)(Math.min(this.getCurrentHeight() + 1.0f, scrollMaxHeight + 1.0f)))) {
             boolean handledByComponent = false;
            for (Component component : this.getComponents()) {
                 if (component.mouseClicked(mouseX, mouseY, button)) {
                     handledByComponent = true;
                     break;
                 }
             }
             if (handledByComponent) {
                 return true;
             }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
         if (this.isExtended()) {
              this.getComponents().forEach((component) -> {
                 component.mouseReleased(mouseX, mouseY, mouseButton);
              });
         }
        return super.mouseReleased(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        float scrollMaxHeight = (float)MinecraftClient.getInstance().getWindow().getScaledHeight() - (this.getPosY() + this.getHeight());

        if (this.isExtended() && Render2DMethods.mouseWithinBounds(mouseX, mouseY, (double)this.getPosX(), (double)(this.getPosY() + this.getHeight()), (double)this.getWidth(), (double)(Math.min(this.getCurrentHeight() + 1.0f, scrollMaxHeight + 1.0f)))) {
            if (this.getScrollY() > scrollMaxHeight - this.getCurrentHeight()) {
                 float scrollSpeed = Math.max(1.0f, Math.min(this.getCurrentHeight() / 20f, scrollMaxHeight / 10f));

                int currentScrollY = this.getScrollY();
                int newScrollY = currentScrollY + (int)(verticalAmount * scrollSpeed);

                int maxScrollDown = (int) -(this.getCurrentHeight() - scrollMaxHeight);
                newScrollY = Math.max(newScrollY, maxScrollDown);
                newScrollY = Math.min(newScrollY, 0);

                this.setScrollY(newScrollY);

                return true;
            }
        }

        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }


    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (this.isExtended()) {
            for (Component component : this.getComponents()) {
                if (component.charTyped(chr, modifiers)) {
                    return true;
                }
            }
        }
        return super.charTyped(chr, modifiers);
    }

    private int getVisibleValueComponentCount(ModuleComponent moduleComponent) {
        int count = 0;
        for (Component valueComponent : moduleComponent.getComponents()) {
            if (valueComponent instanceof ValueComponent value && value.getValue().getParent().isVisible()) {
                count++;
            }
        }
        return count;
    }

    public void updatePositions() {
        float offsetY = this.getHeight() + 1.0F;
        float currentFramePosX = this.getPosX();
        float currentFramePosY = this.getPosY() + (float)this.getScrollY();

        for (Component component : this.getComponents()) {
            component.setOffsetY(offsetY);
            component.moved(currentFramePosX, currentFramePosY + offsetY);

            if (component instanceof ModuleComponent moduleComponent) {
                if (moduleComponent.isExtended()) {
                    float currentModuleOffsetY = offsetY + moduleComponent.getHeight();

                    for (Component valueComponent : moduleComponent.getComponents()) {
                        if (valueComponent instanceof ValueComponent value && value.getValue().getParent().isVisible()) {
                            valueComponent.setOffsetY(currentModuleOffsetY - offsetY);
                            valueComponent.moved(currentFramePosX, currentFramePosY + currentModuleOffsetY);
                            currentModuleOffsetY += valueComponent.getHeight();
                        }
                    }
                    offsetY = currentModuleOffsetY;
                    offsetY += 3.0F;
                } else {
                    offsetY += component.getHeight();
                }
            } else {
                offsetY += component.getHeight();
            }
        }
    }

    private float getCurrentHeight() {
        float totalContentHeight = 0.0F;

        for (Component component : this.getComponents()) {
            totalContentHeight += component.getHeight();

            if (component instanceof ModuleComponent moduleComponent && moduleComponent.isExtended()) {
                for (Component component1 : moduleComponent.getComponents()) {
                    if (component1 instanceof ValueComponent value && value.getValue().getParent().isVisible()) {
                        totalContentHeight += component1.getHeight();
                    }
                }
                totalContentHeight += 3.0F;
            }
        }
        return totalContentHeight;
    }
}