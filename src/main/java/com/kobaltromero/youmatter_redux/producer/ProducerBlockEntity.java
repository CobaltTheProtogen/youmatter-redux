package com.kobaltromero.youmatter_redux.producer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.ItemStackHandler;
import com.kobaltromero.youmatter_redux.ModContent;
import com.kobaltromero.youmatter_redux.YMConfig;
import com.kobaltromero.youmatter_redux.replicator.ReplicatorBlockEntity;
import com.kobaltromero.youmatter_redux.util.GeneralUtils;
import com.kobaltromero.youmatter_redux.util.MyEnergyStorage;
import com.kobaltromero.youmatter_redux.util.RegistryUtil;

public class ProducerBlockEntity extends BlockEntity implements MenuProvider {

    public ProducerBlockEntity(BlockPos pos, BlockState state) {
        super(ModContent.PRODUCER_BLOCK_ENTITY.get(), pos, state);
    }

    private static final int MAX_UMATTER = 16000;
    private static final int MAX_STABILIZER = 16000;

    private boolean isActivated = true;

    boolean isActivated() {
        return isActivated;
    }

    public void setActivated(boolean activated) {
        isActivated = activated;
        setChanged();

        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public ItemStackHandler inventory = new ItemStackHandler(5) {
        @Override
        protected void onContentsChanged(int slot) {
            ProducerBlockEntity.this.setChanged();
        }
    };

    private final FluidTank uTank = new FluidTank(MAX_UMATTER) {
        @Override
        protected void onContentsChanged() {
            BlockState state = level.getBlockState(worldPosition);
            level.sendBlockUpdated(worldPosition, state, state, 3);
            setChanged();
        }
    };

    private FluidTank sTank = new FluidTank(MAX_STABILIZER) {
        @Override
        protected void onContentsChanged() {
            BlockState state = level.getBlockState(worldPosition);
            level.sendBlockUpdated(worldPosition, state, state, 3);
            setChanged();
        }
    };

    FluidTank getUTank() {
        return uTank;
    }

    FluidTank getSTank() {
        return sTank;
    }

    private final IFluidHandler fluidHandler = new IFluidHandler() {
        @Override
        public int getTanks() {
            return 2;
        }

        @Nonnull
        @Override
        public FluidStack getFluidInTank(int tank) {
            return switch (tank) {
                case 0 -> uTank.getFluid();
                case 1 -> sTank.getFluid();
                default -> null;
            };
        }


        @Override
        public int getTankCapacity(int tank) {
            return switch (tank) {
                case 0 -> MAX_UMATTER;
                case 1 -> MAX_STABILIZER;
                default -> 0;
            };
        }

        @Override
        public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
            if (tank == 0) {
                return false;
            } else if (tank == 1 && (stack.getFluid().equals(ModContent.STABILIZER.get()) || YMConfig.CONFIG.alternativeStabilizer.get().equalsIgnoreCase(RegistryUtil.getRegistryName(stack.getFluid()).getPath()))) {
                return true;
            }
            return false;
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            if (resource.getFluid().equals(ModContent.STABILIZER.get())) {
                if (MAX_STABILIZER - getSTank().getFluidAmount() < resource.getAmount()) {
                    return sTank.fill(new FluidStack(resource.getFluid(), MAX_STABILIZER), action);
                } else {
                    return sTank.fill(resource, action);
                }
            }
            return 0;
        }


        @Nonnull
        @Override
        public FluidStack drain(FluidStack resource, FluidAction action) {
            if (resource.getFluid().equals(ModContent.UMATTER.get())) {
                FluidStack toDrain = uTank.getFluidAmount() < resource.getAmount() ? uTank.getFluid() : resource;
                uTank.drain(toDrain, action);
                return toDrain;
            }
            return null;
        }

        @Nonnull
        @Override
        public FluidStack drain(int maxDrain, FluidAction action) {
            return uTank.getFluid().getFluid() != null ? uTank.drain(uTank.getFluid(), action) : null;
        }
    };

    public int getEnergy() {
        return myEnergyStorage.getEnergyStored();
    }

    public void setEnergy(int energy) {
        myEnergyStorage.setEnergy(energy);
    }

    private final MyEnergyStorage myEnergyStorage = new MyEnergyStorage(this, 1000000, Integer.MAX_VALUE);

    @Override
    public void setRemoved() {
        super.setRemoved();
        level.invalidateCapabilities(worldPosition);
    }

    @Override
    public void loadAdditional(CompoundTag compound, HolderLookup.Provider provider) {
        super.loadAdditional(compound, provider);

        if (compound.contains("uTank")) {
            CompoundTag tagUTank = compound.getCompound("uTank");
            uTank.readFromNBT(provider, tagUTank);
        }
        if (compound.contains("sTank")) {
            CompoundTag tagSTank = compound.getCompound("sTank");
            sTank.readFromNBT(provider, tagSTank);
        }
        if (compound.contains("energy")) {
            setEnergy(compound.getInt("energy"));
        }
        if (compound.contains("isActivated")) {
            isActivated = compound.getBoolean("isActivated");
        }
        if (compound.contains("inventory")) {
            inventory.deserializeNBT(provider, (CompoundTag) compound.get("inventory"));
        }
    }

    @Override
    public void saveAdditional(CompoundTag compound, HolderLookup.Provider provider) {
        super.saveAdditional(compound, provider);
        CompoundTag tagSTank = new CompoundTag();
        CompoundTag tagUTank = new CompoundTag();
        sTank.writeToNBT(provider, tagSTank);
        uTank.writeToNBT(provider, tagUTank);
        compound.put("uTank", tagUTank);
        compound.put("sTank", tagSTank);
        compound.putInt("energy", getEnergy());
        compound.putBoolean("isActivated", isActivated);
        if (compound.contains("inventory")) {
            inventory.deserializeNBT(provider, (CompoundTag) compound.get("inventory"));
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

    private int currentPartTick = 0;

    public static void tick(Level level, BlockPos pos, BlockState state, ProducerBlockEntity be) {
        be.tick(level, pos, state);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (currentPartTick == 40) { // 2 sec
            boolean isActive = false;
            if (isActivated()) {
                if (getEnergy() >= 0.3f * 1000000 && sTank.getFluidAmount() >= 125) { // if energy more than 30 % of max energy
                    if (uTank.getFluidAmount() + YMConfig.CONFIG.productionPerTick.get() <= MAX_UMATTER) {
                        sTank.drain(125, IFluidHandler.FluidAction.EXECUTE);
                        uTank.fill(new FluidStack(ModContent.UMATTER.get(), YMConfig.CONFIG.productionPerTick.get()), IFluidHandler.FluidAction.EXECUTE);
                        myEnergyStorage.extractEnergy(Math.round(getEnergy() / 3f), false);
                        isActive = true;
                    }
                }
            }
            level.setBlock(pos, state.setValue(ProducerBlock.ACTIVE, isActive), 3);
            //Auto-outputting U-Matter
            Object[] neighborTE = getNeighborTileEntity(pos);
            if (neighborTE != null) {
                IFluidHandler h = level.getCapability(Capabilities.FluidHandler.BLOCK, ((BlockPos) neighborTE[0]), (Direction) neighborTE[1]);
                if (h != null) {
                    int amountToDrain = Math.min(uTank.getFluidAmount(), 1000); // set a maximum output of 1000 mB (every two seconds)
                    uTank.drain(h.fill(new FluidStack(ModContent.UMATTER.get(), amountToDrain), IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE);
                }
            }
            currentPartTick = 0;
        } else if ((currentPartTick % 5) == 0) { // every five ticks
            if (inventory != null) {
                if (!(inventory.getStackInSlot(3).isEmpty()) && GeneralUtils.canAddItemToSlot(inventory.getStackInSlot(4), inventory.getStackInSlot(3), false)) {
                    ItemStack item = inventory.getStackInSlot(3);
                    if (item.getItem() instanceof BucketItem) {
                        if (getUTank().getFluidAmount() >= 1000) {
                            getUTank().drain(1000, IFluidHandler.FluidAction.EXECUTE);
                            inventory.setStackInSlot(3, ItemStack.EMPTY);
                            inventory.insertItem(4, new ItemStack(ModContent.UMATTER_BUCKET.get(), 1), false);
                        }
                    } else {
                        IFluidHandlerItem h = item.getCapability(Capabilities.FluidHandler.ITEM);
                        if (h != null) {
                            if (h.getFluidInTank(0).getFluid().isSame(ModContent.UMATTER.get()) || h.getFluidInTank(0).isEmpty()) {
                                if (h.getTankCapacity(0) - h.getFluidInTank(0).getAmount() < getUTank().getFluidAmount()) { //fluid in S-Tank is more than what fits in the item's tank
                                    getUTank().drain(h.fill(new FluidStack(ModContent.UMATTER.get(), h.getTankCapacity(0) - h.getFluidInTank(0).getAmount()), IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE);
                                } else { //S-Tank's fluid fits perfectly in item's tank
                                    getUTank().drain(h.fill(getUTank().getFluid(), IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE);
                                }
                            }
                        }
                        inventory.setStackInSlot(3, ItemStack.EMPTY);
                        inventory.insertItem(4, item, false);
                    }
                }
                if (!inventory.getStackInSlot(1).isEmpty()) {
                    ItemStack item = inventory.getStackInSlot(1);
                    if (item.getItem() instanceof BucketItem && GeneralUtils.canAddItemToSlot(inventory.getStackInSlot(2), new ItemStack(Items.BUCKET, 1), false)) {
                        IFluidHandlerItem h = item.getCapability(Capabilities.FluidHandler.ITEM);
                        if (h != null) {
                            if (!h.getFluidInTank(0).isEmpty() && (h.getFluidInTank(0).getFluid().isSame(ModContent.STABILIZER.get()) || YMConfig.CONFIG.alternativeStabilizer.get().equalsIgnoreCase(RegistryUtil.getRegistryName(h.getFluidInTank(0).getFluid()).getPath()))) {
                                if (MAX_STABILIZER - getSTank().getFluidAmount() >= 1000) {
                                    getSTank().fill(new FluidStack(ModContent.STABILIZER.get(), 1000), IFluidHandler.FluidAction.EXECUTE);
                                    inventory.setStackInSlot(1, ItemStack.EMPTY);
                                    inventory.insertItem(2, new ItemStack(Items.BUCKET, 1), false);
                                }
                            }
                        }
                    } else if (GeneralUtils.canAddItemToSlot(inventory.getStackInSlot(2), inventory.getStackInSlot(1), false)) {
                        IFluidHandlerItem h = item.getCapability(Capabilities.FluidHandler.ITEM);
                        if (h != null) {
                            if (h.getFluidInTank(0).getFluid().isSame(ModContent.STABILIZER.get()) || YMConfig.CONFIG.alternativeStabilizer.get().equalsIgnoreCase(RegistryUtil.getRegistryName(h.getFluidInTank(0).getFluid()).getPath())) {
                                if (h.getFluidInTank(0).getAmount() > MAX_STABILIZER - getSTank().getFluidAmount()) { //given fluid is more than what fits in the S-Tank
                                    getSTank().fill(h.drain(MAX_STABILIZER - getSTank().getFluidAmount(), IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE);
                                } else { //given fluid fits perfectly in S-Tank
                                    getSTank().fill(h.drain(h.getFluidInTank(0).getAmount(), IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE);
                                }
                            }
                        }
                        inventory.setStackInSlot(1, ItemStack.EMPTY);
                        inventory.insertItem(2, item, false);
                    }
                }
            }
            currentPartTick++;
        } else {
            currentPartTick++;
        }
    }


    private Object[] getNeighborTileEntity(BlockPos ProducerPos) {
        Object[] result = null;

        for (Direction facing : Direction.values()) {
            BlockPos offsetPos = ProducerPos.relative(facing);
            BlockEntity offsetBe = level.getBlockEntity(offsetPos);

            if (offsetBe != null) {
                // Prioritize ReplicatorBlockEntity
                if (offsetBe instanceof ReplicatorBlockEntity) {
                    return new Object[]{
                            offsetPos,
                            facing
                    }; // position, facing
                }

                IFluidHandler h = level.getCapability(Capabilities.FluidHandler.BLOCK, offsetPos, facing);
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
        return Component.translatable(ModContent.PRODUCER_BLOCK.get().getDescriptionId());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int windowID, Inventory playerInventory, Player playerEntity) {
        return new ProducerMenu(windowID, level, worldPosition, playerInventory, playerEntity);

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