package com.kobaltromero.youmatter_redux.scanner;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import com.kobaltromero.youmatter_redux.ModContent;

import javax.annotation.Nullable;

public class ScannerBlock extends BaseEntityBlock {
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

    public ScannerBlock() {
        super(Properties.of().strength(5.0F).sound(SoundType.METAL).requiresCorrectToolForDrops());
        this.registerDefaultState(this.stateDefinition.any().setValue(ACTIVE, false));
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return codec();
    }

    @Override
    public @NotNull RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ScannerBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, ModContent.SCANNER_BLOCK_ENTITY.get(), ScannerBlockEntity::tick);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof ScannerBlockEntity scanner) {
                IItemHandler handler = scanner.getItemHandler();
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
    public @NotNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        MenuProvider menuProvider = getMenuProvider(state, level, pos);
        if (menuProvider != null) {
            player.openMenu(menuProvider, buf -> buf.writeBlockPos(pos));
        }
        return InteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof ScannerBlockEntity scanner ? scanner : null;
    }

    @Override
    public @NotNull BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof ScannerBlockEntity scanner) {
            IItemHandler handler = scanner.getItemHandler();
            if (handler != null) {
                for (int i = 0; i < handler.getSlots(); i++) {
                    Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), handler.getStackInSlot(i));
                }
            }
            super.playerWillDestroy(level, pos, state, player);
        }
        return state;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ACTIVE);
    }
}
