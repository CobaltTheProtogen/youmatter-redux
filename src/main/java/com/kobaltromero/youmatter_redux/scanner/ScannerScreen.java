package com.kobaltromero.youmatter_redux.scanner;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import com.kobaltromero.youmatter_redux.ModContent;
import com.kobaltromero.youmatter_redux.YouMatter;

import java.util.Arrays;
import java.util.List;

public class ScannerScreen extends AbstractContainerScreen<ScannerMenu> {
    private static final int WIDTH = 176;
    private static final int HEIGHT = 168;

    private final ScannerBlockEntity scanner;

    private final ResourceLocation GUI = ResourceLocation.fromNamespaceAndPath(YouMatter.MODID, "textures/gui/scanner.png");

    public ScannerScreen(ScannerMenu container, Inventory inv, Component name) {
        super(container, inv, name);
        this.scanner = container.scanner;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTicks);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(guiGraphics, mouseX, mouseY);

        int xAxis = (mouseX - (width - WIDTH) / 2);
        int yAxis = (mouseY - (height - HEIGHT) / 2);

        if (xAxis >= 141 && xAxis <= 156 && yAxis >= 37 && yAxis <= 57) {
            drawTooltip(guiGraphics, mouseX, mouseY, Arrays.asList(Component.literal(I18n.get("youmatter.gui.energy.title")), Component.literal(I18n.get("youmatter.gui.energy.description", scanner.getEnergy()))));
        }

        if (xAxis >= 79 && xAxis <= 94 && yAxis >= 62 && yAxis <= 82) {
            drawTooltip(guiGraphics, mouseX, mouseY, List.of(Component.literal(I18n.get("youmatter.gui.scans.description", scanner.getScans(), scanner.getScansRequired(scanner.inventory.getStackInSlot(1).getItem())))));
        }

        if (!scanner.getHasEncoder()) {
            if (xAxis >= 16 && xAxis <= 32 && yAxis >= 59 && yAxis <= 75) {
                drawTooltip(guiGraphics, mouseX, mouseY, Arrays.asList(Component.literal(I18n.get("youmatter.warning.scanner1")), Component.literal(I18n.get("youmatter.warning.scanner2")), Component.literal(I18n.get("youmatter.warning.scanner3"))));
            }
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        drawEnergyBolt(guiGraphics, scanner.getEnergy());
        drawProgress(guiGraphics, scanner.getProgress());
        drawScans(guiGraphics, scanner.getScans(), scanner.getScansRequired(scanner.inventory.getStackInSlot(1).getItem()));

        if (!scanner.getHasEncoder()) {
            guiGraphics.blit(GUI, 16, 59, 176, 101, 16, 16);
        }
        guiGraphics.drawString(font, I18n.get(ModContent.SCANNER_BLOCK.get().getDescriptionId()), 8, 6, 0x404040, false);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        int relX = (this.width - WIDTH) / 2;
        int relY = (this.height - HEIGHT) / 2;
        guiGraphics.blit(GUI, relX, relY, 0, 0, WIDTH, HEIGHT);
    }

    private void drawProgress(GuiGraphics guiGraphics, int progress) {
        int circuits;

        if (progress < 50) {
            circuits = progress * 2;
        } else if (progress < 100) {
            circuits = 100;
        } else {
            circuits = 100;
        }

        guiGraphics.blit(GUI, 104, 34, 176, 53, 17, Math.round((circuits / 100.0f) * 24));
        guiGraphics.blit(GUI, 54, 34, 176, 77, 17, Math.round((circuits / 100.0f) * 24));
    }

    private void drawScans(GuiGraphics guiGraphics, int scanCount, int maxScanCount) {
        // Calculate the width of the arrow based on the scan count and the maximum scan count
        int arrowWidth = Math.round((Math.min(scanCount, maxScanCount) / (float) maxScanCount) * 18);
        guiGraphics.blit(GUI, 79, 62, 176, 41, arrowWidth, 12);
    }

    private void drawEnergyBolt(GuiGraphics guiGraphics, int energy) {
        if (energy == 0) {
            guiGraphics.blit(GUI, 141, 35, 176, 21, 15, 20);
        } else {
            double percentage = energy * 100.0F / 1000000;
            float percentagef = (float) percentage / 100;
            guiGraphics.blit(GUI, 141, 35, 176, 0, 15, Math.round(20 * percentagef));
        }
    }

    private void drawTooltip(GuiGraphics guiGraphics, int x, int y, List<Component> tooltips) {
        guiGraphics.renderComponentTooltip(font, tooltips, x, y);
    }
}
