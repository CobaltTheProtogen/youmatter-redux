package com.kobaltromero.youmatter_redux.blocks.generic;

import com.kobaltromero.youmatter_redux.ModBlockStateProperties;
import com.kobaltromero.youmatter_redux.ModContent;
import com.kobaltromero.youmatter_redux.block_entities.MachineBlockEntity;
import com.kobaltromero.youmatter_redux.util.ITier;
import com.kobaltromero.youmatter_redux.util.MachineType;
import com.mojang.serialization.MapCodec;
import mekanism.common.tags.MekanismTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MachineBlock extends BaseEntityBlock {
    public static final BooleanProperty ACTIVE = ModBlockStateProperties.ACTIVE;
    public static final BooleanProperty CONTAINS_FLUID = ModBlockStateProperties.CONTAINS_FLUID;
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    private int max_energy;

    private MachineType type;

    private ITier tier;

    public ITier getTier() {
        return tier;
    }

    public int getMaxEnergy() {
        return max_energy;
    }

    public MachineType getMachineType() {
        return type;
    }

    public MachineBlock(BlockBehaviour.Properties props, int max_energy, MachineType type, ITier tier) {
        super(props);
        this.max_energy = max_energy;
        this.type = type;
        this.tier = tier;
        this.registerDefaultState(this.stateDefinition.any().setValue(ACTIVE, false).setValue(CONTAINS_FLUID, false));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return null;
    }

    @Override
    public @NotNull RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        MachineBlockEntity machine = null;
        if (level.getBlockEntity(pos) instanceof MachineBlockEntity) {
            machine = (MachineBlockEntity) level.getBlockEntity(pos);
        }
        return machine;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> be) {
        return createTickerHelper(be, ModContent.MACHINE_BLOCK_ENTITY.get(), MachineBlockEntity::tick);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof MachineBlockEntity machine) {
                IItemHandler handler = machine.getItemHandler();
                if (handler != null) {
                    for (int i = 0; i < handler.getSlots(); i++) {
                        Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), handler.getStackInSlot(i));
                    }
                }
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    /**
     * EVENT that is called when you right-click the block,
     */
    @Override
    public @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        MenuProvider menuProvider = getMenuProvider(state, level, pos);
        if (menuProvider != null) {
            player.openMenu(menuProvider, buf -> buf.writeBlockPos(pos));
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MachineBlockEntity(pos, state);
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Player player = context.getPlayer();
        Direction playerFacing = player != null ? player.getDirection().getOpposite() : Direction.NORTH;
        return this.defaultBlockState().setValue(FACING, playerFacing);
    }

    /**
     * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed blockstate.
     */
    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, ACTIVE, CONTAINS_FLUID);
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!level.isClientSide()) { // Check if we are on the server side
            if (!player.isShiftKeyDown()) { // Check if the player is not sneaking
                if (stack.is(MekanismTags.Items.TOOLS_WRENCH)) { // Check if the item is a wrench
                    level.setBlock(pos, state.cycle(FACING), UPDATE_ALL); // Cycle the block's facing direction
                    return ItemInteractionResult.SUCCESS; // Return success
                }
            } else { // Player is sneaking
                if (stack.is(MekanismTags.Items.TOOLS_WRENCH)) { // Check if the item is a wrench
                    level.removeBlock(pos, false); // Remove the block without dropping the item
                    ItemStack itemStack = new ItemStack(state.getBlock()); // Create an ItemStack from the block state
                    Vec3 position = new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5); // Calculate the position to drop the item
                    ItemEntity itemEntity = new ItemEntity(level, position.x, position.y, position.z, itemStack); // Create the item entity
                    level.addFreshEntity(itemEntity); // Add the item entity to the level
                    return ItemInteractionResult.SUCCESS; // Return success
                }
            }
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult); // Return the default interaction result
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        if (!level.isClientSide) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof MachineBlockEntity machine) {
                boolean hasSignal = level.hasNeighborSignal(pos);
                if (hasSignal != machine.getLastSignal()) {
                    machine.setLastSignal(hasSignal);
                    if (hasSignal != machine.isActivated()) {
                        machine.setActivated(hasSignal);
                        level.updateNeighborsAt(pos, state.getBlock());
                    }
                }
            }
        }
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof MachineBlockEntity machine) {
            boolean isPowered = level.hasNeighborSignal(pos);
            if (isPowered != machine.getLastSignal()) {
                machine.setLastSignal(isPowered);
                if (machine.isActivated() != isPowered) {
                    machine.setActivated(isPowered);
                    if (isPowered) {
                        level.scheduleTick(pos, state.getBlock(), 4); // Schedule a tick if powered
                    }
                }
            }
        }
    }
}
