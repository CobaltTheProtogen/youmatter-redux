package com.kobaltromero.youmatter_redux;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.kobaltromero.youmatter_redux.network.*;

import java.util.List;

@Mod(YouMatter.MODID)
public class YouMatter {
    public static final String MODID = "youmatter";
    public static final Logger logger = LogManager.getLogger();
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB = CREATIVE_MODE_TABS.register("tab", () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
            .icon(() -> new ItemStack(ModContent.UMATTER_BUCKET.get()))
            .title(Component.translatable("itemGroup.YouMatter"))
            .displayItems((displayParameters, output) -> {
                output.acceptAll(List.of(
                        new ItemStack(ModContent.SCANNER_BLOCK.get()),
                        new ItemStack(ModContent.ENCODER_BLOCK.get()),
                        new ItemStack(ModContent.PRODUCER_BLOCK.get()),
                        new ItemStack(ModContent.REPLICATOR_BLOCK.get()),
                        new ItemStack(ModContent.MACHINE_CASING_ITEM.get()),
                        new ItemStack(ModContent.BLACK_HOLE_ITEM.get()),
                        new ItemStack(ModContent.COMPUTE_MODULE_ITEM.get()),
                        new ItemStack(ModContent.TRANSISTOR_RAW_ITEM.get()),
                        new ItemStack(ModContent.TRANSISTOR_ITEM.get()),
                        new ItemStack(ModContent.THUMBDRIVE_8_ITEM.get()),
                        new ItemStack(ModContent.UMATTER_BUCKET.get()),
                        new ItemStack(ModContent.STABILIZER_BUCKET.get())));
            }).build());

    public YouMatter(IEventBus modEventBus, ModContainer container) {
        container.registerConfig(ModConfig.Type.COMMON, YMConfig.CONFIG_SPEC);
        ModContent.init(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        modEventBus.addListener(YMClient::registerScreens);
        modEventBus.addListener(YMClient::registerClientExtensions);
        modEventBus.addListener(this::registerCapabilities);
        modEventBus.addListener(this::registerPayloads);
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModContent.SCANNER_BLOCK_ENTITY.get(), (o, direction) -> o.getItemHandler());
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModContent.SCANNER_BLOCK_ENTITY.get(), (o, direction) -> o.getEnergyHandler());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModContent.ENCODER_BLOCK_ENTITY.get(), (o, direction) -> o.getItemHandler());
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModContent.ENCODER_BLOCK_ENTITY.get(), (o, direction) -> o.getEnergyHandler());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModContent.REPLICATOR_BLOCK_ENTITY.get(), (o, direction) -> o.getItemHandler());
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModContent.REPLICATOR_BLOCK_ENTITY.get(), (o, direction) -> o.getEnergyHandler());
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModContent.REPLICATOR_BLOCK_ENTITY.get(), (o, direction) -> o.getFluidHandler());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModContent.PRODUCER_BLOCK_ENTITY.get(), (o, direction) -> o.getItemHandler());
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModContent.PRODUCER_BLOCK_ENTITY.get(), (o, direction) -> o.getEnergyHandler());
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModContent.PRODUCER_BLOCK_ENTITY.get(), (o, direction) -> o.getFluidHandler());
    }

    @SubscribeEvent
    public void registerPayloads(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(
                PacketChangeSettingsProducerServer.TYPE,
                PacketChangeSettingsProducerServer.STREAM_CODEC,
                PacketHandler.ProducerSettings::handle
        );
        registrar.playToServer(
                PacketShowNext.TYPE,
                PacketShowNext.STREAM_CODEC,
                PacketHandler.ShowNext::handle
        );
        registrar.playToServer(
                PacketShowPrevious.TYPE,
                PacketShowPrevious.STREAM_CODEC,
                PacketHandler.ShowPrevious::handle
        );
        registrar.playToServer(
                PacketChangeSettingsReplicatorServer.TYPE,
                PacketChangeSettingsReplicatorServer.STREAM_CODEC,
                PacketHandler.ReplicatorSettings::handle
        );
    }
}
