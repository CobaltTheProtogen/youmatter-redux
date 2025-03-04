package com.kobaltromero.youmatter_redux.block_entities;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.kobaltromero.youmatter_redux.blocks.generic.MachineBlock;
import com.kobaltromero.youmatter_redux.blocks.replicator.RestrictedItemHandler;
import com.kobaltromero.youmatter_redux.util.*;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import com.kobaltromero.youmatter_redux.ModContent;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MachineBlockEntity extends BlockEntity implements MenuProvider {

    //TODO: Finish Generic MachineBlockEntity class. I promise I know what I am doing. Don't judge me.

    public MachineBlock machine = (MachineBlock) this.getBlockState().getBlock();
    public int max_energy;
    private ITier tier;
    private MachineType type;

    public final int MAX_UMATTER = 16_000;


    private boolean currentMode = true;

    private boolean isActivated = false;
    private boolean lastSignal;

    public boolean isActive = false;
    public boolean containsFluid = false;

    public MachineBlockEntity(BlockPos pos, BlockState state) {
        super(ModContent.MACHINE_BLOCK_ENTITY.get(), pos, state);
        this.max_energy = machine.getMaxEnergy();
        this.type = machine.getMachineType();
        this.tier = machine.getTier();
    }


    public boolean isCurrentMode() {
        return currentMode;
    }

    public void setCurrentMode(boolean currentMode) {
        this.currentMode = currentMode;
        setChanged();

        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public boolean isActivated() {
        return isActivated;
    }

    public void setActivated(boolean activated) {
        isActivated = activated;
        setChanged();

        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public float getBaseAmplifier() {
        return tier.getBaseAmplifier();
    }

    public float getProbability() {
        return tier.getProbability();
    }

    public boolean getLastSignal() {
        return lastSignal;
    }

    public void setLastSignal(boolean lastSignal) {
        this.lastSignal = lastSignal;
    }

    private final FluidTank tank = new FluidTank(MAX_UMATTER) {
        @Override
        protected void onContentsChanged() {
            BlockState state = level.getBlockState(worldPosition);
            level.sendBlockUpdated(worldPosition, state, state, 3);
            setChanged();
        }
    };

    public FluidTank getTank() {
        return tank;
    }

    public ITier getTier() {
        return tier;
    }

    private final IFluidHandler fluidHandler = new IFluidHandler() {
        @Override
        public int getTanks() {
            return 1;
        }

        @Override
        public @NotNull FluidStack getFluidInTank(int tank) {
                return getTank().getFluid();
        }


        @Override
        public int getTankCapacity(int tank) {
            return MAX_UMATTER;
        }

        @Override
        public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
            if(machine.getMachineType() == MachineType.REPLICATOR) {
                return stack.getFluid().isSame(ModContent.UMATTER.get());
            } else {
                return false;
            }
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            if (machine.getMachineType() == MachineType.REPLICATOR) {
                if (resource.getFluid().isSame(ModContent.UMATTER.get())) {
                    if (MAX_UMATTER - getTank().getFluidAmount() < resource.getAmount()) {
                        return tank.fill(new FluidStack(resource.getFluid(), MAX_UMATTER), action);
                    } else {
                        return tank.fill(resource, action);
                    }
                }
            } else {
                return 0;
            }
            return 0;
        }


        @Nonnull
        @Override
        public FluidStack drain(FluidStack resource, FluidAction action) {
            if (resource.getFluid().equals(ModContent.UMATTER.get())) {
                FluidStack toDrain = tank.getFluidAmount() < resource.getAmount() ? tank.getFluid() : resource;
                tank.drain(toDrain, action);
                return toDrain;
            }
            return null;
        }

        @Nonnull
        @Override
        public FluidStack drain(int maxDrain, FluidAction action) {
            return tank.getFluid().getFluid() != null ? tank.drain(tank.getFluid(), action) : null;
        }
    };

    public ItemStackHandler inventory = new ItemStackHandler(5) {
        @Override
        protected void onContentsChanged(int slot) {
            MachineBlockEntity.this.setChanged();
        }
    };

    public RestrictedItemHandler restrictedInventory = new RestrictedItemHandler(inventory);

    public List<ItemStack> cachedItems;

    public int getEnergy() {
        return myEnergyStorage.getEnergyStored();
    }

    public void setEnergy(int energy) {
        if(getTier() != Tier.CREATIVE) {
            myEnergyStorage.setEnergy(energy);
        } else {
            myEnergyStorage.setEnergy(max_energy);
        }
    }

    private int progress = 0;

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
        setChanged();
    }

    public final MyEnergyStorage myEnergyStorage = new MyEnergyStorage(this, max_energy, Integer.MAX_VALUE);

    @Override
    public void setRemoved() {
        super.setRemoved();
        this.invalidateCapabilities();
    }

    @Override
    public void loadAdditional(CompoundTag compound, HolderLookup.Provider provider) {
        super.loadAdditional(compound, provider);
        tank.readFromNBT(provider, compound.getCompound("tank"));
        setEnergy(compound.getInt("energy"));
        setActivated(compound.getBoolean("isActivated"));
        setProgress(compound.getInt("progress"));
        setCurrentMode(compound.getBoolean("mode"));
        if (compound.contains("inventory")) {
            inventory.deserializeNBT(provider, (CompoundTag) compound.get("inventory"));
        }
    }

    @Override
    public void saveAdditional(CompoundTag compound, HolderLookup.Provider provider) {
        super.saveAdditional(compound, provider);
        CompoundTag tagTank = new CompoundTag();
        tank.writeToNBT(provider, tagTank);
        compound.put("tank", tagTank);
        compound.putInt("energy", getEnergy());
        compound.putBoolean("isActivated", isActivated);
        compound.putBoolean("mode", isCurrentMode());
        compound.putInt("progress", getProgress());
        if (inventory != null) {
            compound.put("inventory", inventory.serializeNBT(provider));
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        return saveWithoutMetadata(provider);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public int currentPartTick = 0;

    // Current displayed item index -> cachedItems
    public int currentIndex = 0;
    private ItemStack currentItem;

    public static void tick(Level level, BlockPos pos, BlockState state, MachineBlockEntity be) {
        if (level != null) {
            be.tick(level, pos, state);
        }
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (type != null) {
            type.performAction(level, pos, state, this);
        }
    }

    public Object[] getNeighborTileEntity(Level level, BlockPos pos) {
        Object[] result = null;

        for (Direction facing : Direction.values()) {
            BlockPos offsetPos = pos.relative(facing);
            BlockEntity offsetBe = level.getBlockEntity(offsetPos);

            if (offsetBe != null) {
                // Prioritize Replicator
                if (offsetBe.getBlockState().getBlock() instanceof MachineBlock machine && machine.getMachineType() == MachineType.REPLICATOR) {
                    return new Object[]{
                            offsetPos,
                            facing
                    }; // position, facing
                }

                IFluidHandler h = this.level.getCapability(Capabilities.FluidHandler.BLOCK, offsetPos, facing);
                if (h != null && h.fill(new FluidStack(ModContent.UMATTER.get(), 500), IFluidHandler.FluidAction.SIMULATE) > 0) {
                    if (result == null) {
                        result = new Object[]{
                                offsetPos,
                                facing
                        };
                    }
                }
            }
        }

        return result; // found nothing or return the first non-replicator that can take fluid
    }



    @Override
    public Component getDisplayName() {
        return Component.literal(I18n.get(ModContent.BASIC_PRODUCER_BLOCK.get().getDescriptionId())).withColor(0x121213);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int windowID, Inventory playerInventory, Player playerEntity) {
        return type.createMenu(windowID, level, worldPosition, playerInventory, playerEntity);
    }

    public RestrictedItemHandler getRestrictedItemHandler() {
        return restrictedInventory;
    }

    public ItemStackHandler getItemHandler() {
        return inventory;
    }

    public IEnergyStorage getEnergyHandler() {
        return myEnergyStorage;
    }

    public IFluidHandler getFluidHandler() {
        return fluidHandler;
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