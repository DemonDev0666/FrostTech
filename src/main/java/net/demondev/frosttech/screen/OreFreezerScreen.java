package net.demondev.frosttech.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.demondev.frosttech.FrostTech;
import net.demondev.frosttech.screen.renderer.EnergyInfoArea;
import net.demondev.frosttech.util.MouseUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.Optional;

public class OreFreezerScreen extends AbstractContainerScreen<OreFreezerMenu> {
    // Define the texture path for the GUI
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(FrostTech.MOD_ID, "textures/gui/ore_freezer_gui.png");

    // Energy info area for rendering energy storage in the GUI
    private EnergyInfoArea energyInfoArea;

    // Constructor for the screen, passing the menu, player inventory, and title
    public OreFreezerScreen(OreFreezerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    // Initialize the GUI and assign the energy info area
    @Override
    protected void init() {
        super.init();
        this.inventoryLabelY = 10000; // Hide inventory label
        this.titleLabelY = 10000;     // Hide title label
        assignEnergyInfoArea();       // Assign the energy bar
    }

    // Assign the energy info area (positioned relative to the GUI window)
    private void assignEnergyInfoArea() {
        int x = (width - imageWidth) / 2; // X position for the GUI
        int y = (height - imageHeight) / 2; // Y position for the GUI
        int energyBarWidth = 9;
        int energyBarHeight = 64;

        // Create the energy info area at the correct position
        energyInfoArea = new EnergyInfoArea(x + 150, y + 11, menu.blockEntity.getEnergyStorage(), energyBarWidth, energyBarHeight);
    }

    // Render the background and energy info area
    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        // Set shader and color before rendering the background
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);

        // Calculate the top-left corner of the GUI window
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        // Render the background texture
        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        // Render the progress arrow and energy bar
        renderProgressArrow(guiGraphics, x, y);
        energyInfoArea.draw(guiGraphics);
    }

    // Render the crafting progress arrow
    private void renderProgressArrow(GuiGraphics guiGraphics, int x, int y) {
        if (menu.isCrafting()) {
            guiGraphics.blit(TEXTURE, x + 85, y + 30, 176, 0, 8, menu.getScaledProgress());
        }
    }

    // Main render method, rendering the background, GUI elements, and tooltips
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics); // Render the default background
        super.render(guiGraphics, mouseX, mouseY, delta); // Render the main GUI

        // Render the tooltip (for items or slots)
        renderTooltip(guiGraphics, mouseX, mouseY);

        // Render the energy area tooltip (custom energy storage info)
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        renderEnergyAreaToolTips(guiGraphics, mouseX, mouseY, x, y);
    }

    // Render the tooltip for the energy area
    private void renderEnergyAreaToolTips(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, int x, int y) {
        // Check if the mouse is hovering over the energy bar
        if (isMouseAboveArea(pMouseX, pMouseY, x, y, 150, 11, 9, 63)) {
            // Render the tooltip at the current mouse position
            pGuiGraphics.renderTooltip(this.font, energyInfoArea.getTooltips(), Optional.empty(), pMouseX, pMouseY);
        }
    }

    // Utility method to check if the mouse is above a specific area (e.g., the energy bar)
    private boolean isMouseAboveArea(int pMouseX, int pMouseY, int x, int y, int offsetX, int offsetY, int width, int height) {
        return MouseUtil.isMouseOver(pMouseX, pMouseY, x + offsetX, y + offsetY, width, height);
    }
}
