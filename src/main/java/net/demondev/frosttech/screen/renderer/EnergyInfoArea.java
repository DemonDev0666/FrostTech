package net.demondev.frosttech.screen.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.List;

public class EnergyInfoArea {
    private final IEnergyStorage energy;
    private final Rect2i area;

    public EnergyInfoArea(int xMin, int yMin, IEnergyStorage energy, int width, int height) {
        this.energy = energy;
        this.area = new Rect2i(xMin, yMin, 9, 64);
    }


    public List<Component> getTooltips() {
        return List.of(Component.literal(energy.getEnergyStored() + "/" + energy.getMaxEnergyStored() + " FE"));
    }


    public void draw(GuiGraphics guiGraphics) {
        int height = area.getHeight();
        int stored = (int) (height * (energy.getEnergyStored() / (float) energy.getMaxEnergyStored()));


        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);


        guiGraphics.fillGradient(
                area.getX(), area.getY() + (height - stored),
                area.getX() + area.getWidth(), area.getY() + area.getHeight(),
                0xffa3eaff, 0xff2ca8e8
        );
    }
}
