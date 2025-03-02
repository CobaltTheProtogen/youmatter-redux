package com.kobaltromero.youmatter_redux.blocks.scanner;

import com.kobaltromero.youmatter_redux.util.GeneralUtils;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.ItemStackHandler;
import com.kobaltromero.youmatter_redux.ModContent;
import com.kobaltromero.youmatter_redux.YMConfig;
import com.kobaltromero.youmatter_redux.blocks.encoder.EncoderBlock;
import com.kobaltromero.youmatter_redux.blocks.encoder.EncoderBlockEntity;
import com.kobaltromero.youmatter_redux.util.MyEnergyStorage;
import com.kobaltromero.youmatter_redux.util.RegistryUtil;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Objects;

public class ScannerBlockEntity extends BlockEntity implements MenuProvider {

    public boolean hasEncoder = false;
    private int scans = 0;
    private Item storedItem = null;
    private boolean isActive = false;

    public ScannerBlockEntity(BlockPos pos, BlockState state) {
        super(ModContent.SCANNER_BLOCK_ENTITY.get(), pos, state);
    }

    public boolean getHasEncoder() {
        return hasEncoder;
    }

    public void setHasEncoder(boolean hasEncoder) {
        this.hasEncoder = hasEncoder;
        setChanged();
    }

    public ItemStackHandler inventory = new ItemStackHandler(5) {
        @Override
        protected void onContentsChanged(int slot) {
            ScannerBlockEntity.this.setChanged();
        }
    };

    private int progress = 0;

    public int getProgress() {
        return progress;
    }

    public int getScansRequired(Item item) {
        return GeneralUtils.getScansRequiredForItem(item);
    }

    public int getScans() {
        return scans;
    }

    public void setScans(int scans) {
        this.scans = scans;
        setChanged();
    }

    public void setProgress(int progress) {
        this.progress = progress;
        setChanged();
    }

    public int getEnergy() {
        return myEnergyStorage.getEnergyStored();
    }

    public void setEnergy(int energy) {
        myEnergyStorage.setEnergy(energy);
    }

    private final MyEnergyStorage myEnergyStorage = new MyEnergyStorage(this, 1000000, Integer.MAX_VALUE);

    @Override
    public void loadAdditional(CompoundTag compound, HolderLookup.Provider provider) {
        super.loadAdditional(compound, provider);
        if (compound.contains("progress")) {
            setProgress(compound.getInt("progress"));
        }
        if(compound.contains("scans")) {
            setScans(compound.getInt("scans"));
        }
        if (compound.contains("energy")) {
            setEnergy(compound.getInt("energy"));
        }
        if (compound.contains("inventory")) {
            inventory.deserializeNBT(provider, (CompoundTag) compound.get("inventory"));
        }
        setHasEncoder(compound.getBoolean("encoder"));
    }

    @Override
    public void saveAdditional(CompoundTag compound, HolderLookup.Provider provider) {
        super.saveAdditional(compound, provider);
        compound.putInt("progress", getProgress());
        compound.putInt("energy", getEnergy());
        compound.putBoolean("encoder", getHasEncoder());
        compound.putInt("scans", scans);
        if (inventory != null) {
            compound.put("inventory", inventory.serializeNBT(provider));
        }
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        return saveWithoutMetadata(provider);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, ScannerBlockEntity be) {
        be.tick(level, pos, state);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide()) return;

        BlockPos encoderPos = getNeighborEncoder(this.worldPosition);
        boolean encoderExists = encoderPos != null;

        if (encoderExists && !hasEncoder) {
            setChanged();
            level.sendBlockUpdated(pos, state, state, 3);
        }

        hasEncoder = encoderExists;
        isActive = myEnergyStorage != null && myEnergyStorage.getEnergyStored() >= YMConfig.CONFIG.energyScanner.get() && !inventory.getStackInSlot(1).isEmpty();

        if (encoderExists) {
            if (inventory != null) {
                ItemStack currentItem = inventory.getStackInSlot(1);
                boolean hasCurrentItem = !currentItem.isEmpty();
                boolean itemAllowed = isItemAllowed(currentItem);

                if (hasCurrentItem && itemAllowed) {
                    if (storedItem == null || currentItem.getItem() != storedItem) {
                        setProgress(0);
                        scans = 0;
                        storedItem = currentItem.getItem();
                    } else if (getEnergy() > YMConfig.CONFIG.energyScanner.get()) {
                        if (getProgress() < 100) {
                            setProgress(getProgress() + 1);
                            myEnergyStorage.extractEnergy(YMConfig.CONFIG.energyScanner.get(), false);
                        } else {
                            setProgress(0);
                            scans++;

                            if (currentItem.isStackable()) {
                                currentItem.shrink(1);
                            } else {
                                inventory.setStackInSlot(1, ItemStack.EMPTY);
                            }

                            if (scans >= getScansRequired(currentItem.getItem())) {
                                ItemStack encodedItem = new ItemStack(currentItem.getItem(), 1);
                                ((EncoderBlockEntity) Objects.requireNonNull(level.getBlockEntity(encoderPos))).ignite(encodedItem);
                                scans = 0;
                                storedItem = null;
                            }
                        }
                    }
                } else {
                    setProgress(0);
                }
            }
        } else if (hasEncoder) {
            setChanged();
            level.sendBlockUpdated(pos, state, state, 3);
        }

        level.setBlock(pos, state.setValue(ScannerBlock.ACTIVE, isActive), 3);
    }

    private boolean isItemAllowed(ItemStack itemStack) {
        boolean matches = YMConfig.CONFIG.filterItems.get().stream().anyMatch(s -> s.equalsIgnoreCase(Objects.requireNonNull(RegistryUtil.getRegistryName(itemStack.getItem())).toString()));
        //If list should act as a blacklist AND it contains the item, disallow scanning
        if (YMConfig.CONFIG.filterMode.get() && matches) {
            return false;
            //If list should act as a whitelist AND it DOESN'T contain the item, disallow scanning
        } else if (YMConfig.CONFIG.filterMode.get() || matches) return true;
        else return false;
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        this.invalidateCapabilities();
    }

    @Nullable
    private BlockPos getNeighborEncoder(BlockPos scannerPos) {
        for(Direction facing : Direction.values()) {
            BlockPos offsetPos = scannerPos.relative(facing);

            if(level.getBlockState(offsetPos).getBlock() instanceof EncoderBlock) {
                if(level.getBlockEntity(offsetPos) instanceof EncoderBlockEntity) {
                    return offsetPos;
                }
            }
        }
        return null;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable(ModContent.SCANNER_BLOCK.get().getDescriptionId());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int windowID, Inventory playerInventory, Player player) {
        return new ScannerMenu(windowID, level, worldPosition, playerInventory, player);
    }

    public ItemStackHandler getItemHandler() {
        return inventory;
    }

    public IEnergyStorage getEnergyHandler() {
        return myEnergyStorage;
    }
}
