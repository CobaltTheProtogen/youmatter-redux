package com.kobaltromero.youmatter_redux.blocks.basic.producer;

import java.util.Arrays;
import java.util.List;

import com.kobaltromero.youmatter_redux.block_entities.MachineBlockEntity;
import com.kobaltromero.youmatter_redux.network.PacketChangeSettingsProducerServer;
import com.kobaltromero.youmatter_redux.network.PacketFillBucket;
import com.kobaltromero.youmatter_redux.util.GeneralUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.IFluidTank;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.network.PacketDistributor;
import com.kobaltromero.youmatter_redux.ModContent;
import com.kobaltromero.youmatter_redux.YouMatter;
import org.jetbrains.annotations.NotNull;

public class ProducerScreen extends AbstractContainerScreen<ProducerMenu> {

    private static final int WIDTH = 176;
    private static final int HEIGHT = 168;

    private final MachineBlockEntity machine;

    private static final ResourceLocation GUI = ResourceLocation.fromNamespaceAndPath(YouMatter.MODID, "textures/gui/producer.png");

    public ProducerScreen(ProducerMenu container, Inventory inv, Component name) {
        super(container, inv, name);
        this.machine = container.machine;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        int relX = (this.width - WIDTH) / 2;
        int relY = (this.height - HEIGHT) / 2;
        guiGraphics.blit(GUI, relX, relY, 0, 0, WIDTH, HEIGHT);

        renderFluidSquare(guiGraphics, relX + 80, relY + 33, machine.getTank().getFluid());
    }

    private void drawActiveIcon(GuiGraphics guiGraphics, boolean isActive) {
        if (isActive) {
            guiGraphics.blit(GUI, 84, 25, 176, 9, 8, 8);
        } else {
            guiGraphics.blit(GUI, 84, 25, 176, 0, 8, 8);
        }
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {
        drawEnergyBolt(guiGraphics, machine.getEnergy());
        drawActiveIcon(guiGraphics, machine.isActivated());
    }

    private void drawEnergyBolt(GuiGraphics guiGraphics, int energy) {
        if (energy == 0) {
            guiGraphics.blit(GUI, 150, 58, 176, 114, 15, 20);
        } else {
            double percentage = energy * 100.0F / 1_000_000;  // i know this is dumb
            float percentagef = (float) percentage / 100; // but it works.
            guiGraphics.blit(GUI, 150, 58, 176, 93, 15, Math.round(20 * percentagef)); // it's not really intended that the bolt fills from the top but it looks cool tbh.
        }
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        // Render the dark background
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTicks);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        // Render any tooltips
        this.renderTooltip(guiGraphics, mouseX, mouseY);

        int xAxis = (mouseX - (width - imageWidth) / 2);
        int yAxis = (mouseY - (height - imageHeight) / 2);

        if (xAxis >= 150 && xAxis <= 164 && yAxis >= 57 && yAxis <= 77) {
            int energy = machine.getEnergy();
            String energyText = GeneralUtils.formatLargeNumber(energy);

            drawTooltip(guiGraphics, mouseX, mouseY, Arrays.asList(
                    Component.literal(I18n.get("youmatter.gui.energy.title")),
                    Component.literal(I18n.get("youmatter.gui.energy.description", energyText))
            ));
        }

        if (xAxis >= 148 && xAxis <= 167 && yAxis >= 7 && yAxis <= 27) {
            drawTooltip(guiGraphics, mouseX, mouseY, Arrays.asList(Component.literal(machine.isActivated() ? I18n.get("youmatter.gui.active") : I18n.get("youmatter.gui.paused")), Component.literal(I18n.get("youmatter.gui.clicktochange"))));
        }
    }

    private void renderFluidSquare(GuiGraphics guiGraphics, int x, int y, FluidStack fluidStack) {
        if (fluidStack != null && !fluidStack.isEmpty()) {
            ResourceLocation fluidIcon;
            Fluid fluid = fluidStack.getFluid();

            ResourceLocation waterSprite = IClientFluidTypeExtensions.of(Fluids.WATER).getStillTexture(new FluidStack(Fluids.WATER, 1000));

            if (fluid instanceof FlowingFluid) {
                fluidIcon = IClientFluidTypeExtensions.of(fluid).getStillTexture();
            } else {
                fluidIcon = waterSprite;
            }

            // Constrain the rendering to 16x16 pixels
            guiGraphics.blit(x, y, 0, 16, 16, Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(fluidIcon));
        }
    }


    private void drawTooltip(GuiGraphics guiGraphics, int x, int y, List<Component> tooltips) {
        guiGraphics.renderComponentTooltip(font, tooltips, x, y);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        // Check if the fluid square is clicked with the right mouse button
        if (mouseButton == 1) {
            double xAxis = (mouseX - (double) (width - imageWidth) / 2);
            double yAxis = (mouseY - (double) (height - imageHeight) / 2);
            if (xAxis >= 80 && xAxis <= 96 && yAxis >= 33 && yAxis <= 49) {
                PacketDistributor.sendToServer(new PacketFillBucket(machine.getBlockPos()));
            }
            return true;
        }
        if (mouseButton == 0) {
            double xAxis = (mouseX - (double) (width - imageWidth) / 2);
            double yAxis = (mouseY - (double) (height - imageHeight) / 2);
            if (xAxis >= 148 && xAxis <= 167 && yAxis >= 7 && yAxis <= 27) {
                //Playing Click sound
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                machine.setActivated(!machine.isActivated());
                //Sending packet to server
                PacketDistributor.sendToServer(new PacketChangeSettingsProducerServer(machine.isActivated()));
            }
        }
        return true;
    }
}
