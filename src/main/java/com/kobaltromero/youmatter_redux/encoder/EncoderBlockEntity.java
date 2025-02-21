package com.kobaltromero.youmatter_redux.encoder;

import com.kobaltromero.youmatter_redux.components.ThumbDriveContents;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import com.kobaltromero.youmatter_redux.ModContent;
import com.kobaltromero.youmatter_redux.YMConfig;
import com.kobaltromero.youmatter_redux.items.ThumbDriveItem;
import com.kobaltromero.youmatter_redux.util.MyEnergyStorage;

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
    public void loadAdditional(@NotNull CompoundTag compound, HolderLookup.Provider provider) {
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
    public void saveAdditional(@NotNull CompoundTag compound, HolderLookup.@NotNull Provider provider) {
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
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider provider) {
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
        if (queue.size() > 0) {
            ItemStack processIS = queue.get(queue.size() - 1);
            if (!processIS.isEmpty()) {
                if (inventory != null) {
                    ItemStack thumbDriveStack = inventory.getStackInSlot(1);

                    if (thumbDriveStack.getItem() instanceof ThumbDriveItem thumb) {
                        if (progress < 100) {
                            if (getEnergy() >= YMConfig.CONFIG.energyEncoder.get()) {
                                ThumbDriveContents contents = thumbDriveStack.get(ModContent.THUMBDRIVE_CONTAINER.get());

                                if (contents == null) {
                                    // Create NEW ThumbDriveContents if it's null
                                    contents = new ThumbDriveContents(new Int2ObjectArrayMap<>());
                                }

                                int itemCount = contents.getSlots();

                                if (itemCount < thumb.getMaxStorageInKb()) {
                                    progress++;
                                    myEnergyStorage.extractEnergy(YMConfig.CONFIG.energyEncoder.get(), false);
                                }
                            }
                        } else { // Progress complete
                            ThumbDriveContents contents = thumbDriveStack.get(ModContent.THUMBDRIVE_CONTAINER.get());

                            if (contents == null) {
                                // Create NEW ThumbDriveContents if it's null
                                contents = new ThumbDriveContents(new Int2ObjectArrayMap<>());
                            }

                            Item itemToAdd = processIS.getItem();
                            int nextAvailableSlot = findNextAvailableSlot(contents, thumb);

                            if (nextAvailableSlot != -1) {
                                contents.setItem(nextAvailableSlot, itemToAdd);
                                thumbDriveStack.set(ModContent.THUMBDRIVE_CONTAINER.get(), contents); // VERY IMPORTANT: Set the contents back to the stack
                                queue.remove(processIS);
                                progress = 0;
                            }
                        }
                    }
                }
            }
        }
    }

    private int findNextAvailableSlot(ThumbDriveContents contents, ThumbDriveItem thumb){
        for(int i = 0; i < thumb.getMaxStorageInKb(); i++){
            if(contents.getItem(i) == Items.AIR){
                return i;
            }
        }
        return -1;
    }

    @Override
    public @NotNull Component getDisplayName() {
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