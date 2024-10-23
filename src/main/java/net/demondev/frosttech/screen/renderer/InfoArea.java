package net.demondev.frosttech.screen.renderer;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.renderer.Rect2i;

public abstract class InfoArea implements Renderable {

    protected final Rect2i area;

    protected InfoArea(Rect2i area) {
        this.area = area;
    }

    // Updated to use GuiGraphics instead of PoseStack for Minecraft 1.20.1
    public abstract void draw(GuiGraphics guiGraphics);
}
