package com.kobaltromero.matterz;
import com.kobaltromero.matterz.components.ThumbDriveContents;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.*;
import com.kobaltromero.matterz.fluid.UMatterFluidBlock;
import com.kobaltromero.matterz.fluid.UMatterFluidType;

public class ModContent {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MatterZ.ID);
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(BuiltInRegistries.MENU, MatterZ.ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, MatterZ.ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MatterZ.ID);
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(BuiltInRegistries.FLUID, MatterZ.ID);
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, MatterZ.ID);
    public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, MatterZ.ID);

    static DeferredItem<Item> DUMMY_ITEM = ITEMS.registerItem("dummy_item", props -> new Item(props.stacksTo(1)));

   /*  public static final DeferredBlock<ScannerBlock> SCANNER_BLOCK = BLOCKS.registerBlock("scanner", props -> new ScannerBlock(props.strength(5.0F).sound(SoundType.METAL).requiresCorrectToolForDrops().lightLevel(state -> state.getValue(ModBlockStateProperties.ACTIVE) ? 7 : 0), 1_000_000, SCANNER, TierRegistry.BASIC));
    public static final DeferredHolder<MenuType<?>, MenuType<ScannerMenu>> SCANNER_MENU = MENU_TYPES.register("scanner", () -> IMenuTypeExtension.create((windowId, inv, data) -> new ScannerMenu(windowId, inv.player.level(), data.readBlockPos(), inv, inv.player)));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ScannerBlockEntity>> SCANNER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("scanner", () -> BlockEntityType.Builder.of(ScannerBlockEntity::new, SCANNER_BLOCK.get()).build(null));
    public static final DeferredItem<ScannerBlockItem> SCANNER_BLOCK_ITEM = ITEMS.registerItem("scanner", props -> new ScannerBlockItem(SCANNER_BLOCK.get(), props, MachineType.SCANNER, Tier.BASIC));

    public static final DeferredBlock<EncoderBlock> ENCODER_BLOCK = BLOCKS.registerBlock("encoder", props -> new EncoderBlock(props.strength(5.0F).sound(SoundType.METAL).requiresCorrectToolForDrops().lightLevel(state -> state.getValue(ModBlockStateProperties.ACTIVE) ? 7 : 0), 1_000_000, MachineType.ENCODER, BASIC));
    public static final DeferredHolder<MenuType<?>, MenuType<EncoderMenu>> ENCODER_MENU = MENU_TYPES.register("encoder", () -> IMenuTypeExtension.create((windowId, inv, data) -> new EncoderMenu(windowId, inv.player.level(), data.readBlockPos(), inv, inv.player)));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EncoderBlockEntity>> ENCODER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("encoder", () -> BlockEntityType.Builder.of(EncoderBlockEntity::new, ENCODER_BLOCK.get()).build(null));
    public static final DeferredItem<EncoderBlockItem> ENCODER_BLOCK_ITEM = ITEMS.registerItem("encoder", props -> new EncoderBlockItem(ENCODER_BLOCK.get(), props, MachineType.ENCODER, Tier.BASIC)); */

   /* public static final DeferredBlock<MachineBlock> BASIC_PRODUCER_BLOCK = BLOCKS.registerBlock("producer_basic", props -> new MachineBlock(props.strength(5.0F).sound(SoundType.METAL).requiresCorrectToolForDrops().lightLevel(state -> state.getValue(ModBlockStateProperties.ACTIVE) ? 7 : 0), 1_000_000, 16_000, PRODUCER_TYPE.get(), BASIC.get()));
    public static final DeferredBlock<MachineBlock> ADVANCED_PRODUCER_BLOCK = BLOCKS.registerBlock("producer_advanced", props -> new MachineBlock(props.strength(5.0F).sound(SoundType.METAL).requiresCorrectToolForDrops().lightLevel(state -> state.getValue(ModBlockStateProperties.ACTIVE) ? 7 : 0), 1_000_000, 16_000, PRODUCER_TYPE.get(), ADVANCED.get()));
    public static final DeferredBlock<MachineBlock> ELITE_PRODUCER_BLOCK = BLOCKS.registerBlock("producer_elite", props -> new MachineBlock(props.strength(5.0F).sound(SoundType.METAL).requiresCorrectToolForDrops().lightLevel(state -> state.getValue(ModBlockStateProperties.ACTIVE) ? 7 : 0), 1_000_000, 16_000, PRODUCER_TYPE.get(), ELITE.get()));
    public static final DeferredBlock<MachineBlock> ULTIMATE_PRODUCER_BLOCK = BLOCKS.registerBlock("producer_ultimate", props -> new MachineBlock(props.strength(5.0F).sound(SoundType.METAL).requiresCorrectToolForDrops().lightLevel(state -> state.getValue(ModBlockStateProperties.ACTIVE) ? 7 : 0), 1_000_000,16_000, PRODUCER_TYPE.get(), ULTIMATE.get()));
    public static final DeferredBlock<MachineBlock> CREATIVE_PRODUCER_BLOCK = BLOCKS.registerBlock("producer_creative", props -> new MachineBlock(props.strength(5.0F).sound(SoundType.METAL).requiresCorrectToolForDrops().lightLevel(state -> state.getValue(ModBlockStateProperties.ACTIVE) ? 7 : 0), 1_000_000, 16_000, PRODUCER_TYPE.get(), null));
    public static final DeferredBlock<MachineBlock> REPLICATOR_BLOCK = BLOCKS.registerBlock("replicator", props -> new MachineBlock(props.strength(5.0F).sound(SoundType.METAL).requiresCorrectToolForDrops().lightLevel(state -> state.getValue(ModBlockStateProperties.ACTIVE) ? 7 : 0), 1_000_000, 16_000, REPLICATOR_TYPE.get(), BASIC.get())); */

   /* public static final DeferredItem<MachineBlockItem> BASIC_PRODUCER_BLOCK_ITEM = ITEMS.registerItem("producer_basic", props -> new MachineBlockItem(BASIC_PRODUCER_BLOCK.get(), props, MachineType.PRODUCER, Tier.BASIC));
    public static final DeferredItem<MachineBlockItem> ADVANCED_PRODUCER_BLOCK_ITEM = ITEMS.registerItem("producer_advanced", props -> new MachineBlockItem(ADVANCED_PRODUCER_BLOCK.get(), props, MachineType.PRODUCER, Tier.ADVANCED));
    public static final DeferredItem<MachineBlockItem> ELITE_PRODUCER_BLOCK_ITEM = ITEMS.registerItem("producer_elite", props -> new MachineBlockItem(ELITE_PRODUCER_BLOCK.get(), props, MachineType.PRODUCER, Tier.ELITE));
    public static final DeferredItem<MachineBlockItem> ULTIMATE_PRODUCER_BLOCK_ITEM = ITEMS.registerItem("producer_ultimate", props -> new MachineBlockItem(ULTIMATE_PRODUCER_BLOCK.get(), props, MachineType.PRODUCER, Tier.ULTIMATE));
    public static final DeferredItem<MachineBlockItem> CREATIVE_PRODUCER_BLOCK_ITEM = ITEMS.registerItem("producer_creative", props -> new MachineBlockItem(CREATIVE_PRODUCER_BLOCK.get(), props, MachineType.PRODUCER, Tier.CREATIVE)); */

   /*  public static final DeferredItem<MachineBlockItem> REPLICATOR_BLOCK_ITEM = ITEMS.registerItem("replicator", props -> new MachineBlockItem(REPLICATOR_BLOCK.get(), props, MachineType.REPLICATOR, Tier.BASIC));

    public static final DeferredHolder<MenuType<?>, MenuType<ProducerMenu>> PRODUCER_MENU = MENU_TYPES.register("producer", () -> IMenuTypeExtension.create((windowId, inv, data) -> new ProducerMenu(windowId, inv.player.level(), data.readBlockPos(), inv, inv.player)));
    public static final DeferredHolder<MenuType<?>, MenuType<ReplicatorMenu>> REPLICATOR_MENU = MENU_TYPES.register("replicator", () -> IMenuTypeExtension.create((windowId, inv, data) -> new ReplicatorMenu(windowId, inv.player.level(), data.readBlockPos(), inv, inv.player)));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AbstractMachine>> MACHINE = BLOCK_ENTITY_TYPES.register("machine", () -> BlockEntityType.Builder.of(AbstractMachine::new, BASIC_PRODUCER_BLOCK.get(), ADVANCED_PRODUCER_BLOCK.get(), ELITE_PRODUCER_BLOCK.get(), ULTIMATE_PRODUCER_BLOCK.get(), CREATIVE_PRODUCER_BLOCK.get(), REPLICATOR_BLOCK.get()).build(null)); */
    

    public static final DeferredHolder<FluidType, FluidType> UMATTER_TYPE = FLUID_TYPES.register("umatter", UMatterFluidType::new);
    public static final DeferredHolder<Fluid, FlowingFluid> UMATTER = FLUIDS.register("umatter", () -> new BaseFlowingFluid.Source(ModContent.UMATTER_PROPERTIES));
    public static final DeferredHolder<Fluid, FlowingFluid> UMATTER_FLOWING = FLUIDS.register("umatter_flowing", () -> new BaseFlowingFluid.Flowing(ModContent.UMATTER_PROPERTIES));
    public static final DeferredHolder<Block, UMatterFluidBlock> UMATTER_FLUID_BLOCK = BLOCKS.register("umatter_fluid_block", () -> new UMatterFluidBlock(UMATTER.get(), BlockBehaviour.Properties.of().noCollission().strength(1.0F).noLootTable().replaceable()));
    public static final DeferredHolder<Item, BucketItem> UMATTER_BUCKET = ITEMS.register("umatter_bucket", () -> new BucketItem(UMATTER.get(), new Item.Properties().stacksTo(1)));
    public static final BaseFlowingFluid.Properties UMATTER_PROPERTIES = new BaseFlowingFluid.Properties(UMATTER_TYPE, UMATTER, UMATTER_FLOWING).bucket(UMATTER_BUCKET).block(UMATTER_FLUID_BLOCK);

    /* public static final DeferredItem<ThumbDrive4Item> THUMBDRIVE_4_ITEM = ITEMS.registerItem("thumb_drive_4", props -> new ThumbDrive4Item(props.stacksTo(1).component(ModContent.THUMBDRIVE_CONTAINER, ThumbDriveContents.EMPTY), Tier.BASIC));
    public static final DeferredItem<ThumbDrive8Item> THUMBDRIVE_8_ITEM = ITEMS.registerItem("thumb_drive_8", props -> new ThumbDrive8Item(props.stacksTo(1).component(ModContent.THUMBDRIVE_CONTAINER, ThumbDriveContents.EMPTY), Tier.ADVANCED));
    public static final DeferredItem<ThumbDrive16Item> THUMBDRIVE_16_ITEM = ITEMS.registerItem("thumb_drive_16", props -> new ThumbDrive16Item(props.stacksTo(1).component(ModContent.THUMBDRIVE_CONTAINER, ThumbDriveContents.EMPTY), Tier.ELITE));
    public static final DeferredItem<ThumbDrive32Item> THUMBDRIVE_32_ITEM = ITEMS.registerItem("thumb_drive_32", props -> new ThumbDrive32Item(props.stacksTo(1).component(ModContent.THUMBDRIVE_CONTAINER, ThumbDriveContents.EMPTY), Tier.ULTIMATE));
    public static final DeferredItem<BlackHoleItem> BLACK_HOLE_ITEM = ITEMS.registerItem("black_hole", BlackHoleItem::new);

    public static final DeferredHolder<Item, IronPlateItem> IRON_PLATE_ITEM = ITEMS.register("iron_plate", IronPlateItem::new);
    public static final DeferredHolder<Item, ComputeModuleItem> COMPUTE_MODULE_ITEM = ITEMS.register("compute_module", ComputeModuleItem::new);
    public static final DeferredHolder<Item, TransistorItem> TRANSISTOR_ITEM = ITEMS.register("transistor", TransistorItem::new);
    public static final DeferredHolder<Item, TransistorRawItem> TRANSISTOR_RAW_ITEM = ITEMS.register("transistor_raw", TransistorRawItem::new); */

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ThumbDriveContents>> THUMBDRIVE_CONTAINER = DATA_COMPONENTS.register(
            "thumbdrive_container", () -> DataComponentType.<ThumbDriveContents>builder()
                    .persistent(ThumbDriveContents.CODEC)
                    .networkSynchronized(ThumbDriveContents.STREAM_CODEC).build());

    public static void init(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
        MENU_TYPES.register(modEventBus);
        BLOCK_ENTITY_TYPES.register(modEventBus);
        ITEMS.register(modEventBus);
        FLUIDS.register(modEventBus);
        FLUID_TYPES.register(modEventBus);
        DATA_COMPONENTS.register(modEventBus);
    }
}
