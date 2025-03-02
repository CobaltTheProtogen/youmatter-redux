package com.kobaltromero.youmatter_redux.util;

import com.kobaltromero.youmatter_redux.ModContent;
import com.kobaltromero.youmatter_redux.block_entities.MachineBlockEntity;
import com.kobaltromero.youmatter_redux.blocks.basic.producer.ProducerMenu;
import com.kobaltromero.youmatter_redux.blocks.generic.MachineBlock;
import com.kobaltromero.youmatter_redux.blocks.replicator.ReplicatorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

//TODO: Actually do something with this enum...
public enum MachineType {
    PRODUCER {
        @Override
        public void performAction(Level level, BlockPos pos, BlockState state, MachineBlockEntity blockEntity) {
            if (blockEntity.currentPartTick == 40) { // Machine runs every 2 seconds.
                blockEntity.currentPartTick = 0;

                // Check if conditions are met to produce U-Matter
                if (blockEntity.isActivated()
                        && blockEntity.getEnergy() >= (blockEntity.max_energy * 0.3f)
                        && blockEntity.getTank().getFluidAmount() <= blockEntity.MAX_UMATTER) {
                    if (Math.random() < blockEntity.getProbability()) {
                        blockEntity.getTank().fill(
                                new FluidStack(ModContent.UMATTER.get(), (int) blockEntity.getBaseAmplifier()),
                                IFluidHandler.FluidAction.EXECUTE
                        );
                    }
                    if (blockEntity.getTier() != Tier.CREATIVE) {
                        blockEntity.getEnergyHandler().extractEnergy(
                                Math.round(blockEntity.getEnergy() / 3f), false
                        );
                    }
                }

                // Auto-outputting U-Matter
                Object[] neighborTE = blockEntity.getNeighborTileEntity(level, pos);
                if (neighborTE != null) {
                    IFluidHandler fluidHandler = level.getCapability(
                            Capabilities.FluidHandler.BLOCK,
                            (BlockPos) neighborTE[0],
                            (Direction) neighborTE[1]
                    );
                    if (fluidHandler != null) {
                        int amountToDrain = Math.min(blockEntity.getTank().getFluidAmount(), 1000); // Max 1000 mB
                        blockEntity.getTank().drain(
                                fluidHandler.fill(
                                        new FluidStack(ModContent.UMATTER.get(), amountToDrain),
                                        IFluidHandler.FluidAction.EXECUTE
                                ),
                                IFluidHandler.FluidAction.EXECUTE
                        );
                    }
                }
            } else {
                blockEntity.currentPartTick++;
            }

            // Update block state
            blockEntity.isActive = blockEntity.getEnergy() >= blockEntity.max_energy * 0.3f && blockEntity.isActivated();
            blockEntity.containsFluid = blockEntity.getTank().getFluidAmount() > 0;
            level.setBlock(
                    pos,
                    state.setValue(MachineBlock.ACTIVE, blockEntity.isActive)
                            .setValue(MachineBlock.CONTAINS_FLUID, blockEntity.containsFluid),
                    3
            );
        }

        @Override
        public AbstractContainerMenu createMenu(int windowID, Level level, BlockPos pos, Inventory playerInventory, Player playerEntity) {
            return new ProducerMenu(windowID, level, pos, playerInventory, playerEntity);
        }
    },
    REPLICATOR {
        @Override
        public void performAction(Level level, BlockPos pos, BlockState state, MachineBlockEntity blockEntity) {
            // TODO: Implement Replicator functionality
        }

        @Override
        public AbstractContainerMenu createMenu(int windowID, Level level, BlockPos pos, Inventory playerInventory, Player playerEntity) {
            return null;
        }
    },
    SCANNER {
        @Override
        public void performAction(Level level, BlockPos pos, BlockState state, MachineBlockEntity blockEntity) {
            // TODO: Implement Scanner functionality
        }

        @Override
        public AbstractContainerMenu createMenu(int windowID, Level level, BlockPos pos, Inventory playerInventory, Player playerEntity) {
            // TODO: Implement Scanner menu creation
            return null;
        }
    },
    ENCODER {
        @Override
        public void performAction(Level level, BlockPos pos, BlockState state, MachineBlockEntity blockEntity) {
            // TODO: Implement Encoder functionality
        }

        @Override
        public AbstractContainerMenu createMenu(int windowID, Level level, BlockPos pos, Inventory playerInventory, Player playerEntity) {
            // TODO: Implement Encoder menu creation
            return null;
        }
    },
    RECYCLER {
        @Override
        public void performAction(Level level, BlockPos pos, BlockState state, MachineBlockEntity blockEntity) {
            // TODO: Implement Recycler functionality
        }

        @Override
        public AbstractContainerMenu createMenu(int windowID, Level level, BlockPos pos, Inventory playerInventory, Player playerEntity) {
            // TODO: Implement Recycler menu creation
            return null;
        }
    };

    // Abstract method requires all machine types to define their behavior
    public abstract void performAction(Level level, BlockPos pos, BlockState state, MachineBlockEntity blockEntity);

    public abstract AbstractContainerMenu createMenu(int windowID, Level level, BlockPos pos, Inventory playerInventory, Player playerEntity);
}

