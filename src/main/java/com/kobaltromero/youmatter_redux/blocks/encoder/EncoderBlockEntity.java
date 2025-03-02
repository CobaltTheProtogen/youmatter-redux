package com.kobaltromero.youmatter_redux.blocks.encoder;

import com.kobaltromero.youmatter_redux.components.ThumbDriveContents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
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
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.ItemStackHandler;
import com.kobaltromero.youmatter_redux.ModContent;
import com.kobaltromero.youmatter_redux.YMConfig;
import com.kobaltromero.youmatter_redux.items.tiered.thumbdrives.ThumbDriveItem;
import com.kobaltromero.youmatter_redux.util.MyEnergyStorage;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EncoderBlockEntity extends BlockEntity implements MenuProvider {

    private List<ItemStack> queue = new ArrayList<>();

    private boolean isActive = false;

    private boolean encoded = false;

    public EncoderBlockEntity(BlockPos pos, BlockState state) {
        super(ModContent.ENCODER_BLOCK_ENTITY.get(), pos, state);
    }

    public ItemStackHandler inventory = new ItemStackHandler(5) {
        @Override
        protected void onContentsChanged(int slot) {
            EncoderBlockEntity.this.setChanged();
        }
    };


    // Calling this method signals incoming data from a neighboring scanner
    public void ignite(ItemStack itemStack) {
        if (itemStack != ItemStack.EMPTY && itemStack != null) {
            queue.add(itemStack);
            setChanged();
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
        setProgress(compound.getInt("progress"));
        setEnergy(compound.getInt("energy"));

        if (compound.contains("inventory")) {
            inventory.deserializeNBT(provider, (CompoundTag) compound.get("inventory"));
        }

        if (compound.contains("queue") && compound.get("queue") instanceof ListTag) {
            queue = compound.getList("queue", Tag.TAG_COMPOUND).stream()
                    .filter(base -> base instanceof CompoundTag)
                    .map(base -> ItemStack.parseOptional(provider, (CompoundTag) base))
                    .filter(stack -> !stack.isEmpty())
                    .collect(Collectors.toList());
        }
    }


    @Override
    public void saveAdditional(CompoundTag compound, HolderLookup.Provider provider) {
        super.saveAdditional(compound, provider);
        compound.putInt("progress", getProgress());
        compound.putInt("energy", getEnergy());
        if (inventory != null) {
            compound.put("inventory", inventory.serializeNBT(provider));
        }
        ListTag tempCompoundList = new ListTag();
        for (ItemStack is : queue) {
            if (!is.isEmpty()) {
                tempCompoundList.add(is.save(provider, new CompoundTag()));
            }
        }
        compound.put("queue", tempCompoundList);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        return saveWithoutMetadata(provider);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        this.invalidateCapabilities();
    }

    public static void tick(Level level, BlockPos pos, BlockState state, EncoderBlockEntity be) {
        be.tick(level, pos, state);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if(level.isClientSide()) return;
        if (!queue.isEmpty()) {
            ItemStack processIS = queue.get(queue.size() - 1);

            // Ensure processIS is not empty
            if (!processIS.isEmpty()) {
                if (inventory != null && inventory.getStackInSlot(1).getItem() instanceof ThumbDriveItem thumb) {
                    ThumbDriveContents contents = inventory.getStackInSlot(1).get(ModContent.THUMBDRIVE_CONTAINER.get());

                    List<ItemStack> list = new ArrayList<>();
                    if (contents != null) {
                        for (ItemStack stack : contents.nonEmptyItems()) {
                            list.add(stack);
                        }
                    }

                    for (ItemStack encodedStack : list) {
                        encoded = ItemStack.isSameItem(encodedStack, processIS);
                        break;
                    }

                    if (!encoded && list.size() < thumb.getMaxStorageInKb()) {
                        if (progress < 100) {
                            if (getEnergy() >= YMConfig.CONFIG.energyEncoder.get()) {
                                progress++;
                                myEnergyStorage.extractEnergy(YMConfig.CONFIG.energyEncoder.get(), false);
                            }
                        } else {
                            list.add(processIS);
                            inventory.getStackInSlot(1).set(ModContent.THUMBDRIVE_CONTAINER.get(), ThumbDriveContents.fromItems(list));
                            queue.removeIf(item -> ItemStack.isSameItem(item, processIS));
                            progress = 0;
                        }
                    } else {
                        queue.removeIf(item -> ItemStack.isSameItem(item, processIS));
                        progress = 0;
                    }
                } else {
                    // Reset progress if the thumb drive is taken out
                    progress = 0;
                }
            } else {
                // Remove empty item stack from the queue to prevent it from being processed again
                queue.remove(processIS);
            }
        }

        // Ensure the isActive state remains consistent if conditions are met
        isActive = myEnergyStorage != null && myEnergyStorage.getEnergyStored() >= YMConfig.CONFIG.energyEncoder.get() && !queue.isEmpty() && inventory.getStackInSlot(1).getItem() instanceof ThumbDriveItem;

        // Update block state based on isActive variable
        level.setBlock(pos, state.setValue(EncoderBlock.ACTIVE, isActive), 3);
    }



    @Override
    public Component getDisplayName() {
        return Component.translatable(ModContent.ENCODER_BLOCK.get().getDescriptionId());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int windowID, Inventory playerInventory, Player player) {
        return new EncoderMenu(windowID, level, worldPosition, playerInventory, player);
    }

    public ItemStackHandler getItemHandler() {
        return inventory;
    }

    public IEnergyStorage getEnergyHandler() {
        return myEnergyStorage;
    }
}