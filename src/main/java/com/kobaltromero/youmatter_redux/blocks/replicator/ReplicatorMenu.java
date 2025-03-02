package com.kobaltromero.youmatter_redux.blocks.replicator;

import com.kobaltromero.youmatter_redux.block_entities.MachineBlockEntity;
import com.kobaltromero.youmatter_redux.blocks.generic.MachineBlock;
import com.kobaltromero.youmatter_redux.util.MachineType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import com.kobaltromero.youmatter_redux.ModContent;
import com.kobaltromero.youmatter_redux.items.tiered.thumbdrives.ThumbDriveItem;
import com.kobaltromero.youmatter_redux.util.DisplaySlot;

import java.util.Optional;

public class ReplicatorMenu extends AbstractContainerMenu {

    public MachineBlockEntity machine;
    private IItemHandler playerInventory;

    public ReplicatorMenu(int windowId, Level level, BlockPos pos, Inventory playerInventory, Player player) {
        super(ModContent.REPLICATOR_MENU.get(), windowId);
        machine = level.getBlockEntity(pos) instanceof MachineBlockEntity replicator ? replicator : null;
        this.playerInventory = new InvWrapper(playerInventory);

        addPlayerSlots(this.playerInventory);
        addCustomSlots();
    }

    @Override
    public boolean stillValid(Player player) {
        Level level = machine.getLevel();
        BlockPos pos = machine.getBlockPos();

        return level.getBlockState(pos).getBlock() instanceof MachineBlock && machine.getMachineType() == MachineType.REPLICATOR && player.distanceToSqr(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
    }

    private void addPlayerSlots(IItemHandler itemHandler) {
        // Slots for the main inventory
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                int x = col * 18 + 8;
                int y = row * 18 + 85;
                addSlot(new SlotItemHandler(itemHandler, col + row * 9 + 9, x, y));
            }
        }
        // Slots for the hotbar
        for (int row = 0; row < 9; ++row) {
            int x = 8 + row * 18;
            int y = 143;
            addSlot(new SlotItemHandler(itemHandler, row, x, y));
        }
    }

    private void addCustomSlots() {
        addSlot(new SlotItemHandler(machine.getItemHandler(), 0, 150, 60));
        addSlot(new SlotItemHandler(machine.getItemHandler(), 1, 89, 60));
        addSlot(new DisplaySlot(machine.getItemHandler(), 2, 89, 17));
        addSlot(new SlotItemHandler(machine.getItemHandler(), 3, 47, 18));
        addSlot(new SlotItemHandler(machine.getItemHandler(), 4, 47, 60));
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            itemStack = slotStack.copy();

            if (index >= 36 && index <= 40) { //originating slot is custom slot
                if (!this.moveItemStackTo(slotStack, 0, 36, true)) {
                    return ItemStack.EMPTY; // Inventory is full, can't transfer item!
                }
            } else {
                if (slotStack.getItem() instanceof ThumbDriveItem) {
                    if (!this.moveItemStackTo(slotStack, 36, 37, false)) {
                        return ItemStack.EMPTY; // custom slot is full, can't transfer item!
                    }
                } else if (slotStack.getItem() instanceof BucketItem bucket) {
                    if (bucket.content.isSame(machine.getTank().getFluidInTank(0).getFluid()) || machine.getTank().isEmpty()) {
                        if (bucket.content.isSame(ModContent.UMATTER.get())) {
                            if (!this.moveItemStackTo(slotStack, 39, 40, false)) {
                                return ItemStack.EMPTY; // custom slot is full, can't transfer item!
                            }
                        }
                    }
                } else {
                    IFluidHandlerItem h = slotStack.getCapability(Capabilities.FluidHandler.ITEM);
                    if (h != null) {
                        if (h.getFluidInTank(0).getFluid().isSame(machine.getTank().getFluidInTank(0).getFluid()) || machine.getTank().isEmpty()) {
                            if (h.getFluidInTank(0).getFluid().isSame(ModContent.UMATTER.get())) {
                                if (!this.moveItemStackTo(slotStack, 39, 40, false)) {
                                    return ItemStack.EMPTY; // custom slot is full, can't transfer item!
                                }
                            }
                        } else {
                            return ItemStack.EMPTY;
                        }
                    }
                    return ItemStack.EMPTY;
                }
                return ItemStack.EMPTY;
            }

            if (slotStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemStack;
    }
}