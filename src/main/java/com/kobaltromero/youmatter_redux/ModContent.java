package com.kobaltromero.youmatter_redux;


import com.kobaltromero.youmatter_redux.components.ThumbDriveContents;
import com.kobaltromero.youmatter_redux.items.parts.*;
import com.kobaltromero.youmatter_redux.items.tiered.BlackHoleItem;
import com.kobaltromero.youmatter_redux.items.tiered.TieredBlockItem;
import com.kobaltromero.youmatter_redux.items.tiered.TieredItem;
import com.kobaltromero.youmatter_redux.items.tiered.machines.*;
import com.kobaltromero.youmatter_redux.items.tiered.thumbdrives.ThumbDrive16Item;
import com.kobaltromero.youmatter_redux.items.tiered.thumbdrives.ThumbDrive32Item;
import com.kobaltromero.youmatter_redux.items.tiered.thumbdrives.ThumbDrive4Item;
import com.kobaltromero.youmatter_redux.items.tiered.thumbdrives.ThumbDrive8Item;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.*;
import com.kobaltromero.youmatter_redux.blocks.producer.ProducerBlock;
import com.kobaltromero.youmatter_redux.blocks.producer.ProducerBlockEntity;
import com.kobaltromero.youmatter_redux.blocks.producer.ProducerMenu;
import com.kobaltromero.youmatter_redux.blocks.encoder.EncoderBlock;
import com.kobaltromero.youmatter_redux.blocks.encoder.EncoderBlockEntity;
import com.kobaltromero.youmatter_redux.blocks.encoder.EncoderMenu;
import com.kobaltromero.youmatter_redux.blocks.StabilizerFluidBlock;
import com.kobaltromero.youmatter_redux.fluid.StabilizerFluidType;
import com.kobaltromero.youmatter_redux.blocks.UMatterFluidBlock;
import com.kobaltromero.youmatter_redux.fluid.UMatterFluidType;
import com.kobaltromero.youmatter_redux.blocks.replicator.ReplicatorBlock;
import com.kobaltromero.youmatter_redux.blocks.replicator.ReplicatorBlockEntity;
import com.kobaltromero.youmatter_redux.blocks.replicator.ReplicatorMenu;
import com.kobaltromero.youmatter_redux.blocks.scanner.ScannerBlock;
import com.kobaltromero.youmatter_redux.blocks.scanner.ScannerBlockEntity;
import com.kobaltromero.youmatter_redux.blocks.scanner.ScannerMenu;

public class ModContent {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(YouMatter.MODID);
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(BuiltInRegistries.MENU, YouMatter.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, YouMatter.MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(YouMatter.MODID);
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(BuiltInRegistries.FLUID, YouMatter.MODID);
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, YouMatter.MODID);
    public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, YouMatter.MODID);

    public static final DeferredBlock<ScannerBlock> SCANNER_BLOCK = BLOCKS.registerBlock("scanner", props -> new ScannerBlock(props.strength(5.0F).sound(SoundType.METAL).requiresCorrectToolForDrops().lightLevel(state -> state.getValue(ModBlockStateProperties.ACTIVE) ? 7 : 0)));
    public static final DeferredHolder<MenuType<?>, MenuType<ScannerMenu>> SCANNER_MENU = MENU_TYPES.register("scanner", () -> IMenuTypeExtension.create((windowId, inv, data) -> new ScannerMenu(windowId, inv.player.level(), data.readBlockPos(), inv, inv.player)));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ScannerBlockEntity>> SCANNER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("scanner", () -> BlockEntityType.Builder.of(ScannerBlockEntity::new, SCANNER_BLOCK.get()).build(null));
    public static final DeferredItem<ScannerBlockItem> SCANNER_BLOCK_ITEM = ITEMS.registerItem("scanner", props -> new ScannerBlockItem(SCANNER_BLOCK.get(), props, TieredBlockItem.Tier.BASIC));

    public static final DeferredBlock<EncoderBlock> ENCODER_BLOCK = BLOCKS.registerBlock("encoder", props -> new EncoderBlock(props.strength(5.0F).sound(SoundType.METAL).requiresCorrectToolForDrops().lightLevel(state -> state.getValue(ModBlockStateProperties.ACTIVE) ? 7 : 0)));
    public static final DeferredHolder<MenuType<?>, MenuType<EncoderMenu>> ENCODER_MENU = MENU_TYPES.register("encoder", () -> IMenuTypeExtension.create((windowId, inv, data) -> new EncoderMenu(windowId, inv.player.level(), data.readBlockPos(), inv, inv.player)));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EncoderBlockEntity>> ENCODER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("encoder", () -> BlockEntityType.Builder.of(EncoderBlockEntity::new, ENCODER_BLOCK.get()).build(null));
    public static final DeferredItem<EncoderBlockItem> ENCODER_BLOCK_ITEM = ITEMS.registerItem("encoder", props -> new EncoderBlockItem(ENCODER_BLOCK.get(), props, TieredBlockItem.Tier.BASIC));

    public static final DeferredBlock<ProducerBlock> PRODUCER_BLOCK = BLOCKS.registerBlock("producer", props -> new ProducerBlock(props.strength(5.0F).sound(SoundType.METAL).requiresCorrectToolForDrops().lightLevel(state -> state.getValue(ModBlockStateProperties.ACTIVE) ? 7 : 0)));
    public static final DeferredHolder<MenuType<?>, MenuType<ProducerMenu>> PRODUCER_MENU = MENU_TYPES.register("producer", () -> IMenuTypeExtension.create((windowId, inv, data) -> new ProducerMenu(windowId, inv.player.level(), data.readBlockPos(), inv, inv.player)));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ProducerBlockEntity>> PRODUCER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("creator", () -> BlockEntityType.Builder.of(ProducerBlockEntity::new, PRODUCER_BLOCK.get()).build(null));
    public static final DeferredItem<ProducerBlockItem> PRODUCER_BLOCK_ITEM = ITEMS.registerItem("producer", props -> new ProducerBlockItem(PRODUCER_BLOCK.get(), props, TieredBlockItem.Tier.ULTIMATE));

    public static final DeferredBlock<ReplicatorBlock> REPLICATOR_BLOCK = BLOCKS.registerBlock("replicator", props -> new ReplicatorBlock(props.strength(5.0F).sound(SoundType.METAL).requiresCorrectToolForDrops().lightLevel(state -> state.getValue(ModBlockStateProperties.ACTIVE) ? 7 : 0)));
    public static final DeferredHolder<MenuType<?>, MenuType<ReplicatorMenu>> REPLICATOR_MENU = MENU_TYPES.register("replicator", () -> IMenuTypeExtension.create((windowId, inv, data) -> new ReplicatorMenu(windowId, inv.player.level(), data.readBlockPos(), inv, inv.player)));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ReplicatorBlockEntity>> REPLICATOR_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("replicator", () -> BlockEntityType.Builder.of(ReplicatorBlockEntity::new, REPLICATOR_BLOCK.get()).build(null));
    public static final DeferredItem<ReplicatorBlockItem> REPLICATOR_BLOCK_ITEM = ITEMS.registerItem("replicator", props -> new ReplicatorBlockItem(REPLICATOR_BLOCK.get(), props, TieredBlockItem.Tier.ELITE));

    public static final DeferredHolder<FluidType, FluidType> STABILIZER_TYPE = FLUID_TYPES.register("stabilizer", StabilizerFluidType::new);
    public static final DeferredHolder<Fluid, FlowingFluid> STABILIZER = FLUIDS.register("stabilizer", () -> new BaseFlowingFluid.Source(ModContent.STABILIZER_PROPERIES));
    public static final DeferredHolder<Fluid, FlowingFluid> STABILIZER_FLOWING = FLUIDS.register("stabilizer_flowing", () -> new BaseFlowingFluid.Flowing(ModContent.STABILIZER_PROPERIES));
    public static final DeferredHolder<Block, StabilizerFluidBlock> STABILIZER_FLUID_BLOCK = BLOCKS.register("stabilizer_fluid_block", () -> new StabilizerFluidBlock(STABILIZER.get(), BlockBehaviour.Properties.of().noCollission().strength(1.0F).noLootTable()));
    public static final DeferredHolder<Item, BucketItem> STABILIZER_BUCKET = ITEMS.register("stabilizer_bucket", () -> new BucketItem(STABILIZER.get(), new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final BaseFlowingFluid.Properties STABILIZER_PROPERIES = new BaseFlowingFluid.Properties(STABILIZER_TYPE, STABILIZER, STABILIZER_FLOWING).bucket(STABILIZER_BUCKET).block(STABILIZER_FLUID_BLOCK);

    public static final DeferredHolder<FluidType, FluidType> UMATTER_TYPE = FLUID_TYPES.register("umatter", UMatterFluidType::new);
    public static final DeferredHolder<Fluid, FlowingFluid> UMATTER = FLUIDS.register("umatter", () -> new BaseFlowingFluid.Source(ModContent.UMATTER_PROPERTIES));
    public static final DeferredHolder<Fluid, FlowingFluid> UMATTER_FLOWING = FLUIDS.register("umatter_flowing", () -> new BaseFlowingFluid.Flowing(ModContent.UMATTER_PROPERTIES));
    public static final DeferredHolder<Block, UMatterFluidBlock> UMATTER_FLUID_BLOCK = BLOCKS.register("umatter_fluid_block", () -> new UMatterFluidBlock(UMATTER.get(), BlockBehaviour.Properties.of().noCollission().strength(1.0F).noLootTable()));
    public static final DeferredHolder<Item, BucketItem> UMATTER_BUCKET = ITEMS.register("umatter_bucket", () -> new BucketItem(UMATTER.get(), new Item.Properties().stacksTo(1)));
    public static final BaseFlowingFluid.Properties UMATTER_PROPERTIES = new BaseFlowingFluid.Properties(UMATTER_TYPE, UMATTER, UMATTER_FLOWING).bucket(UMATTER_BUCKET).block(UMATTER_FLUID_BLOCK);

    public static final DeferredItem<ThumbDrive4Item> THUMBDRIVE_4_ITEM = ITEMS.registerItem("thumb_drive_4", props -> new ThumbDrive4Item(props.stacksTo(1).component(ModContent.THUMBDRIVE_CONTAINER, ThumbDriveContents.EMPTY), TieredItem.Tier.BASIC));
    public static final DeferredItem<ThumbDrive8Item> THUMBDRIVE_8_ITEM = ITEMS.registerItem("thumb_drive_8", props -> new ThumbDrive8Item(props.stacksTo(1).component(ModContent.THUMBDRIVE_CONTAINER, ThumbDriveContents.EMPTY), TieredItem.Tier.ADVANCED));
    public static final DeferredItem<ThumbDrive16Item> THUMBDRIVE_16_ITEM = ITEMS.registerItem("thumb_drive_16", props -> new ThumbDrive16Item(props.stacksTo(1).component(ModContent.THUMBDRIVE_CONTAINER, ThumbDriveContents.EMPTY), TieredItem.Tier.ELITE));
    public static final DeferredItem<ThumbDrive32Item> THUMBDRIVE_32_ITEM = ITEMS.registerItem("thumb_drive_32", props -> new ThumbDrive32Item(props.stacksTo(1).component(ModContent.THUMBDRIVE_CONTAINER, ThumbDriveContents.EMPTY), TieredItem.Tier.ULTIMATE));
    public static final DeferredItem<BlackHoleItem> BLACK_HOLE_ITEM = ITEMS.registerItem("black_hole", BlackHoleItem::new);

    public static final DeferredHolder<Item, IronPlateItem> IRON_PLATE_ITEM = ITEMS.register("iron_plate", IronPlateItem::new);
    public static final DeferredHolder<Item, ComputeModuleItem> COMPUTE_MODULE_ITEM = ITEMS.register("compute_module", ComputeModuleItem::new);
    public static final DeferredHolder<Item, TransistorItem> TRANSISTOR_ITEM = ITEMS.register("transistor", TransistorItem::new);
    public static final DeferredHolder<Item, TransistorRawItem> TRANSISTOR_RAW_ITEM = ITEMS.register("transistor_raw", TransistorRawItem::new);

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
