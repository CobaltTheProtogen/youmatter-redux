package com.kobaltromero.matterz;

import com.kobaltromero.matterz.api.machine.MachineType;
import com.kobaltromero.matterz.api.machine.Tier;
import com.kobaltromero.matterz.api.registry.MZKeys;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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
import com.kobaltromero.matterz.network.*;

import java.util.List;

@Mod(MatterZ.ID)
public class MatterZ {
    public static final String ID = "matterz";
    public static final Logger LOGGER = LogManager.getLogger();
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ID);
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MATTERZ = CREATIVE_MODE_TABS.register("matterz",() -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
            .icon(() -> ModContent.DUMMY_ITEM.get().getDefaultInstance())
            .title(Component.translatable("itemGroup.matterz"))
            .displayItems((displayParameters, output) -> {
                output.acceptAll(List.of(
                        new ItemStack(ModContent.UMATTER_BUCKET)));
            }).build());

    // MachineType Registry
    public static final ResourceKey<Registry<MachineType>> MACHINE_TYPE = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(ID, "machine_type"));

    public static final DeferredRegister<MachineType> MACHINE_TYPES = DeferredRegister.create(MACHINE_TYPE, ID);

    public static final DeferredHolder<MachineType, MachineType> PRODUCER = MACHINE_TYPES.register("producer", MachineType::new);
    public static final DeferredHolder<MachineType, MachineType> ITEM_REPLICATOR = MACHINE_TYPES.register("replicator_item", MachineType::new);
    /* public static final DeferredHolder<MachineType, MachineType> ENTITY_REPLICATOR = MACHINE_TYPES.register("replicator_entity", MachineType::new);
    public static final DeferredHolder<MachineType, MachineType> FLUID_REPLICATOR = MACHINE_TYPES.register("replicator_fluid", MachineType::new);
    public static final DeferredHolder<MachineType, MachineType> RECYCLER = MACHINE_TYPES.register("recycler", MachineType::new);
    public static final DeferredHolder<MachineType, MachineType> MATTER_KRYSTALLIZER = MACHINE_TYPES.register("krystallizer", MachineType::new); */
    public static final DeferredHolder<MachineType, MachineType> SCANNER = MACHINE_TYPES.register("scanner", MachineType::new);
    public static final DeferredHolder<MachineType, MachineType> ENCODER = MACHINE_TYPES.register("encoder", MachineType::new);


    // Tier Registry
    public static final ResourceKey<Registry<Tier>> TIER = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(ID, "tier"));


    public static final DeferredRegister<Tier> TIERS = DeferredRegister.create(TIER, ID);

    public static final DeferredHolder<Tier, Tier> BASIC = TIERS.register("basic", () -> new Tier(0x03F288, 1.0f, 0.0125f));
    public static final DeferredHolder<Tier, Tier> ADVANCED = TIERS.register("advanced", () -> new Tier(0xD22C20, 2.0f, 0.025f));
    public static final DeferredHolder<Tier, Tier> ELITE = TIERS.register("elite", () -> new Tier(0x31E1DF, 4.0f, 0.05f));
    public static final DeferredHolder<Tier, Tier> ULTIMATE = TIERS.register("ultimate", () -> new Tier(0xA300F0, 8.0f, 0.075f));
    public static final DeferredHolder<Tier, Tier> CREATIVE = TIERS.register("creative", () -> new Tier(0x808080, 16_000f, 1f));

    public MatterZ(IEventBus modEventBus, ModContainer container) {
        container.registerConfig(ModConfig.Type.COMMON, YMConfig.CONFIG_SPEC);
        ModContent.init(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        MACHINE_TYPES.register(modEventBus);
        TIERS.register(modEventBus);
        modEventBus.addListener(MZClient::registerScreens);
        modEventBus.addListener(MZClient::registerClientExtensions);
        modEventBus.addListener(this::registerCapabilities);
        modEventBus.addListener(this::registerPayloads);
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
       /* event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModContent.SCANNER_BLOCK_ENTITY.get(), (o, direction) -> o.getItemHandler());
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModContent.SCANNER_BLOCK_ENTITY.get(), (o, direction) -> o.getEnergyHandler());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModContent.ENCODER_BLOCK_ENTITY.get(), (o, direction) -> o.getItemHandler()); */
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
                PacketFillBucket.TYPE,
                PacketFillBucket.STREAM_CODEC,
                PacketHandler.FillBucket::handle
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
