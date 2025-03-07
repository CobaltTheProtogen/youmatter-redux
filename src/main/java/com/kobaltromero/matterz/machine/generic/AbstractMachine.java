package com.kobaltromero.matterz.machine.generic;

import com.kobaltromero.matterz.api.machine.IMachine;
import com.kobaltromero.matterz.api.machine.ITier;
import com.kobaltromero.matterz.api.machine.MachineType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractMachine extends BlockEntity implements IMachine {
    protected boolean isActivated;
    protected int maxEnergy;
    protected MachineType type;
    protected ITier tier;

    public AbstractMachine(BlockEntityType<?> be, BlockPos pos, BlockState blockState) {
        super(be, pos, blockState);
    }

    @Override
    public ITier getTier() {
        return tier;
    }

    @Override
    public int getMaxEnergy() {
        return maxEnergy;
    }

    @Override
    public int getMaxFluid() {
        return getMachineBlock().getMaxFluid();
    }

    @Override
    public MachineType getMachineType() {
        return type;
    }

    @Override
    public boolean isActivated() {
        return isActivated;
    }

    @Override
    public void setActivated(boolean isActivated) {
        this.isActivated = isActivated;
    }

    @Override
    public boolean containsFluid() {
        return false;
    }

    @Override
    public void setContainsFluid(boolean containsFluid) {

    }

    @Override
    public boolean isMachineActive() {
        return false;
    }

    @Override
    public void setMachineActive(boolean isActive) {

    }

    @Override
    public boolean getLastSignal() {
        return false;
    }

    @Override
    public void setLastSignal(boolean lastSignal) {

    }

    @Override
    public boolean isCurrentMode() {
        return false;
    }

    @Override
    public void setCurrentMode(boolean currentMode) {

    }

    @Override
    public int getCurrentPartTick() {
        return 0;
    }

    @Override
    public void setCurrentPartTick(int currentPartTick) {

    }

    @Override
    public void incrementPartTick() {

    }

    @Override
    public int getProgress() {
        return 0;
    }

    @Override
    public void setProgress(int progress) {

    }

    @Override
    public void incrementProgress() {

    }
}



/* public void replicatorFunctionality(Level level, BlockPos pos, BlockState state) {
        if (currentPartTick == 5) {
            currentPartTick = 0;
            if (inventory == null) return;

            ItemStack item = inventory.getStackInSlot(3);
            if (!item.isEmpty()) {
                if (item.getItem() instanceof BucketItem) {
                    if (GeneralUtils.canAddItemToSlot(inventory.getStackInSlot(4), new ItemStack(Items.BUCKET, 1), false)) {
                        IFluidHandlerItem h = item.getCapability(Capabilities.FluidHandler.ITEM);
                        if (!h.getFluidInTank(0).isEmpty() && h.getFluidInTank(0).getFluid().isSame(ModContent.UMATTER.get())) {
                            if (MAX_UMATTER - getTank().getFluidAmount() >= 1000) {
                                getTank().fill(new FluidStack(ModContent.UMATTER.get(), 1000), IFluidHandler.FluidAction.EXECUTE);
                                inventory.setStackInSlot(3, ItemStack.EMPTY);
                                inventory.insertItem(4, new ItemStack(Items.BUCKET, 1), false);
                            }
                        }
                    }
                } else if (GeneralUtils.canAddItemToSlot(inventory.getStackInSlot(4), item, false)) {
                    IFluidHandlerItem h = item.getCapability(Capabilities.FluidHandler.ITEM);
                    if (h.getFluidInTank(0).getFluid().isSame(ModContent.UMATTER.get())) {
                        if (h.getFluidInTank(0).getAmount() > MAX_UMATTER - getTank().getFluidAmount()) {
                            getTank().fill(h.drain(MAX_UMATTER - getTank().getFluidAmount(), IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE);
                        } else {
                            getTank().fill(h.drain(h.getFluidInTank(0).getAmount(), IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE);
                        }
                    }
                    inventory.setStackInSlot(3, ItemStack.EMPTY);
                    inventory.insertItem(4, item, false);
                }
            }

            ItemStack thumbdrive = inventory.getStackInSlot(0);
            if (thumbdrive.isEmpty()) {
                inventory.setStackInSlot(2, ItemStack.EMPTY);
                cachedItems = null;
                currentIndex = 0;
                progress = 0;
            } else {
                if (thumbdrive.has(ModContent.THUMBDRIVE_CONTAINER.get())) {
                    ThumbDriveContents contents = thumbdrive.get(ModContent.THUMBDRIVE_CONTAINER.get());
                    if (contents != null) {
                        cachedItems = new ArrayList<>();
                        for (ItemStack newItem : contents.nonEmptyItems()) {
                            if (newItem != null) {
                                cachedItems.add(newItem.copy());
                            }
                        }
                        renderItem(cachedItems, currentIndex);
                        if (progress == 0) {
                            if (!inventory.getStackInSlot(2).isEmpty() && isActivated && myEnergyStorage != null && myEnergyStorage.getEnergyStored() >= YMConfig.CONFIG.energyReplicator.get() && tank.getFluidAmount() >= GeneralUtils.getUMatterAmountForItem(cachedItems.get(currentIndex).getItem())) {
                                currentItem = cachedItems.get(currentIndex);
                                tank.drain(GeneralUtils.getUMatterAmountForItem(currentItem.getItem()), IFluidHandler.FluidAction.EXECUTE);
                                progress++;
                                myEnergyStorage.extractEnergy(YMConfig.CONFIG.energyReplicator.get(), false);
                            }
                        } else if (isActivated) {
                            if (progress >= 100) {
                                if (!inventory.getStackInSlot(2).isEmpty()) {
                                    if (!currentMode) {
                                        isActivated = false;
                                    }
                                    inventory.insertItem(1, currentItem, false);
                                }
                                progress = 0;
                            } else {
                                if (currentItem != null && !currentItem.isEmpty() && ItemStack.isSameItem(currentItem, inventory.getStackInSlot(2)) && (inventory.getStackInSlot(1).isEmpty() || GeneralUtils.canAddItemToSlot(inventory.getStackInSlot(1), currentItem, false)) && myEnergyStorage != null && myEnergyStorage.getEnergyStored() >= YMConfig.CONFIG.energyReplicator.get()) {
                                    progress++;
                                    myEnergyStorage.extractEnergy(YMConfig.CONFIG.energyReplicator.get(), false);
                                } else {
                                    progress = 0;
                                }
                            }
                        }
                    }
                }
            }
        }

        isActive = currentItem != null && myEnergyStorage != null && myEnergyStorage.getEnergyStored() >= YMConfig.CONFIG.energyReplicator.get() && (tank.getFluidAmount() >= GeneralUtils.getUMatterAmountForItem(currentItem.getItem()) || progress > 0) && isActivated();
        containsFluid = tank != null && tank.getFluidAmount() > 0;

        level.setBlock(pos, state.setValue(MachineBlock.ACTIVE, isActive).setValue(MachineBlock.CONTAINS_FLUID, containsFluid), 3);
        currentPartTick++;
    }
*/

/*
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
 */
