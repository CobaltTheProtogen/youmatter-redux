package com.kobaltromero.matterz;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;

public class MZClient {
    public static void registerScreens(RegisterMenuScreensEvent event) {
        /* event.register(ModContent.SCANNER_MENU.get(), ScannerScreen::new);
        event.register(ModContent.ENCODER_MENU.get(), EncoderScreen::new);
        event.register(ModContent.REPLICATOR_MENU.get(), ReplicatorScreen::new);
        event.register(ModContent.PRODUCER_MENU.get(), ProducerScreen::new); */
    }

    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        // registerFluidType(event, ModContent.STABILIZER_TYPE.get(), "stabilizer");
        registerFluidType(event, ModContent.UMATTER_TYPE.get(), "umatter");
    }

    private static void registerFluidType(RegisterClientExtensionsEvent event, FluidType fluidType, String fluidName) {
        event.registerFluidType(new IClientFluidTypeExtensions() {
            private final ResourceLocation STILL_TEXTURE = ResourceLocation.fromNamespaceAndPath(MatterZ.ID, "block/fluid/" + fluidName + "/still");
            private final ResourceLocation FLOWING_TEXTURE = ResourceLocation.fromNamespaceAndPath(MatterZ.ID, "block/fluid/" + fluidName + "/flow");

            @Override
            public @NotNull ResourceLocation getStillTexture() {
                return STILL_TEXTURE;
            }

            @Override
            public @NotNull ResourceLocation getFlowingTexture() {
                return FLOWING_TEXTURE;
            }
        }, fluidType);
    }
}
