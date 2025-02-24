package com.kobaltromero.youmatter_redux.blocks.encoder;

import com.kobaltromero.youmatter_redux.blocks.scanner.ScannerBlockEntity;
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

    public EncoderBlockEntity(BlockPos pos, BlockState state) {
        super(ModContent.ENCODER_BLOCK_ENTITY.get(), pos, state);
    }

    public ItemStackHandler inventory = new ItemStackHandler(5) {
        @Override
        protected void onContentsChanged(int slot) {
            EncoderBlockEntity.this.setChanged();
        }

        @Override
        public @NotNull ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            // Only allow insertion into slot 1 (index 0)
            if (slot == 1) {
                return super.insertItem(slot, stack, simulate);
            }
            // If trying to insert into any other slot, reject the insertion
            return stack;
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
        level.invalidateCapabilities(worldPosition);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, EncoderBlockEntity be) {
        be.tick(level, pos, state);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        boolean isActive = false;
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

                    boolean encoded = false;
                    for (ItemStack encodedStack : list) {
                        if (ItemStack.isSameItem(encodedStack, processIS)) {
                            encoded = true;
                            break;
                        }
                    }

                    if (!encoded && list.size() < thumb.getMaxStorageInKb()) {
                        if (progress < 100) {
                            if (getEnergy() >= YMConfig.CONFIG.energyEncoder.get()) {
                                progress++;
                                myEnergyStorage.extractEnergy(YMConfig.CONFIG.energyEncoder.get(), false);
                                isActive = true; // Machine is active when it has enough power to encode
                            } else {
                                isActive = false; // Machine is inactive when it doesn't have enough power to encode
                            }
                        } else {
                            list.add(processIS);
                            inventory.getStackInSlot(1).set(ModContent.THUMBDRIVE_CONTAINER.get(), ThumbDriveContents.fromItems(list));
                            queue.removeIf(item -> ItemStack.isSameItem(item, processIS));
                            progress = 0;
                            isActive = true; // Machine is active when encoding is complete
                        }
                    } else {
                        queue.removeIf(item -> ItemStack.isSameItem(item, processIS));
                        progress = 0;
                        isActive = true; // Machine is active when processing the item
                    }
                } else {
                    // Reset progress if the thumb drive is taken out
                    progress = 0;
                }
            } else {
                // Remove empty item stack from the queue to prevent it from being processed again
                queue.remove(processIS);
                isActive = false; // Machine is inactive when processing empty item stack
            }
        }

        // Set isActive to false when queue is empty
        if (queue.isEmpty()) {
            isActive = false;
        }

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