package com.kobaltromero.youmatter_redux.producer;

import java.util.Arrays;
import java.util.List;

import com.kobaltromero.youmatter_redux.network.PacketChangeSettingsProducerServer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.IFluidTank;
import net.neoforged.neoforge.network.PacketDistributor;
import com.kobaltromero.youmatter_redux.ModContent;
import com.kobaltromero.youmatter_redux.YouMatter;
import org.jetbrains.annotations.NotNull;

public class ProducerScreen extends AbstractContainerScreen<ProducerMenu> {

    private static final int WIDTH = 176;
    private static final int HEIGHT = 168;

    private final ProducerBlockEntity producer;

    private static final ResourceLocation GUI = ResourceLocation.fromNamespaceAndPath(YouMatter.MODID, "textures/gui/producer.png");

    public ProducerScreen(ProducerMenu container, Inventory inv, Component name) {
        super(container, inv, name);
        this.producer = container.producer;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        int relX = (this.width - WIDTH) / 2;
        int relY = (this.height - HEIGHT) / 2;
        guiGraphics.blit(GUI, relX, relY, 0, 0, WIDTH, HEIGHT);

        drawFluidTank(guiGraphics, 89, producer.getUTank());
        drawFluidTank(guiGraphics,31, producer.getSTank());
    }

    private void drawActiveIcon(GuiGraphics guiGraphics, boolean isActive) {
        if(isActive) {
            guiGraphics.blit(GUI, 154, 13, 176, 24, 8, 9);

        } else {
            guiGraphics.blit(GUI, 154, 13, 176, 15, 8, 9);
        }
    }


    @Override
    protected void renderLabels(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {
        drawEnergyBolt(guiGraphics, producer.getEnergy());
        drawActiveIcon(guiGraphics, producer.isActivated());


        guiGraphics.drawString(font, I18n.get(ModContent.PRODUCER_BLOCK.get().getDescriptionId()), 8, 6, 0x404040, false);
    }

    private void drawEnergyBolt(GuiGraphics guiGraphics, int energy) {
        if(energy == 0) {
            guiGraphics.blit(GUI, 150, 58, 176, 114, 15, 20);
        } else {
            double percentage = energy * 100.0F / 1000000;  // i know this is dumb
            float percentagef = (float) percentage / 100; // but it works.
            guiGraphics.blit(GUI, 150, 58, 176, 93, 15, Math.round(20 * percentagef)); // it's not really intended that the bolt fills from the top but it looks cool tbh.

        }
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        //Render the dark background
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTicks);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        //Render any tooltips
        this.renderTooltip(guiGraphics, mouseX, mouseY);

        int xAxis = (mouseX - (width - imageWidth) / 2);
        int yAxis = (mouseY - (height - imageHeight) / 2);

        if(xAxis >= 31 && xAxis <= 44 && yAxis >= 20 && yAxis <= 75) {
            drawTooltip(guiGraphics, mouseX, mouseY, Arrays.asList(Component.literal(I18n.get("youmatter.gui.stabilizer.title")), Component.literal(I18n.get("youmatter.gui.stabilizer.description", producer.getSTank().getFluidAmount()))));
        }
        if(xAxis >= 89 && xAxis <= 102 && yAxis >= 20 && yAxis <= 75) {
            drawTooltip(guiGraphics, mouseX, mouseY, Arrays.asList(Component.literal(I18n.get("youmatter.gui.umatter.title")), Component.literal(I18n.get("youmatter.gui.umatter.description", producer.getUTank().getFluidAmount()))));
        }
        if(xAxis >= 150 && xAxis <= 164 && yAxis >= 57 && yAxis <= 77) {
            drawTooltip(guiGraphics, mouseX, mouseY, Arrays.asList(Component.literal(I18n.get("youmatter.gui.energy.title")), Component.literal(I18n.get("youmatter.gui.energy.description", producer.getEnergy()))));
        }
        if(xAxis >= 148 && xAxis <= 167 && yAxis >= 7 && yAxis <= 27) {
            drawTooltip(guiGraphics, mouseX, mouseY, Arrays.asList(Component.literal(producer.isActivated() ? I18n.get("youmatter.gui.active") : I18n.get("youmatter.gui.paused")), Component.literal(I18n.get("youmatter.gui.clicktochange"))));
        }
    }


    private void drawTooltip(GuiGraphics guiGraphics, int x, int y, List<Component> tooltips) {
        guiGraphics.renderComponentTooltip(font, tooltips, x, y);
    }


    //both drawFluid and drawFluidTank is courtesy of DarkGuardsMan and was modified to suit my needs. Go check him out: https://github.com/BuiltBrokenModding/Atomic-Science | MIT License |  Copyright (c) 2018 Built Broken Modding License: https://opensource.org/licenses/MIT
    private void drawFluid(GuiGraphics guiGraphics, int x, int y, int drawSize, FluidStack fluidStack)
    {
        if (fluidStack != null && !fluidStack.isEmpty())
        {
            drawSize -= 1;
            ResourceLocation fluidIcon;
            Fluid fluid = fluidStack.getFluid();

            ResourceLocation waterSprite = IClientFluidTypeExtensions.of(Fluids.WATER).getStillTexture(new FluidStack(Fluids.WATER, 1000));

            if (fluid instanceof FlowingFluid) {
                IClientFluidTypeExtensions.of(fluid).getStillTexture();
                fluidIcon = IClientFluidTypeExtensions.of(fluid).getStillTexture();
            } else {
                fluidIcon = waterSprite;
            }

            final int textureSize = 16;
            int start = 0;
            int renderY;
            while (drawSize != 0) {
                if (drawSize > textureSize) {
                    renderY = textureSize;
                    drawSize -= textureSize;
                } else {
                    renderY = drawSize;
                    drawSize = 0;
                }

                //TODO?
                guiGraphics.blit(x + 1, y + -3 + 58 - renderY - start, 1000, 14, textureSize - (textureSize - renderY), Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(fluidIcon));


                start = start + textureSize;
            }
        }
    }

    private void drawFluidTank(GuiGraphics guiGraphics, int x, IFluidTank tank) {

        //Get data
        final float scale = tank.getFluidAmount() / (float) tank.getCapacity();
        final FluidStack fluidStack = tank.getFluid();


        //Draw fluid
        int meterHeight = 55;
        this.drawFluid(guiGraphics, this.leftPos + x - 1, this.topPos + 22, (int) ((meterHeight - 1) * scale), fluidStack);

        //Draw lines
        int meterWidth = 14;
        guiGraphics.blit(GUI, this.leftPos + x, this.topPos + 22, 176, 35, meterWidth, meterHeight);

    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if(mouseButton == 0) {
            double xAxis = (mouseX - (double) (width - imageWidth) / 2);
            double yAxis = (mouseY - (double) (height - imageHeight) / 2);
            if(xAxis >= 148 && xAxis <= 167 && yAxis >= 7 && yAxis <= 27) {
                //Playing Click sound
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                producer.setActivated(!producer.isActivated());
                //Sending packet to server
                PacketDistributor.sendToServer(new PacketChangeSettingsProducerServer(producer.isActivated()));
            }
        }
        return true;
    }
}
