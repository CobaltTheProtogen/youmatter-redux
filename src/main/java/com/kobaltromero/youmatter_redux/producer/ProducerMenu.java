package com.kobaltromero.youmatter_redux.producer;


import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import com.kobaltromero.youmatter_redux.ModContent;
import com.kobaltromero.youmatter_redux.YMConfig;
import com.kobaltromero.youmatter_redux.util.RegistryUtil;



public class ProducerMenu extends AbstractContainerMenu {

    public ProducerBlockEntity producer;
    private IItemHandler playerInventory;


    public ProducerMenu(int windowId, Level level, BlockPos pos, Inventory playerInventory, Player player) {
        super(ModContent.PRODUCER_MENU.get(), windowId);
        producer = level.getBlockEntity(pos) instanceof ProducerBlockEntity creator ? creator : null;
        this.playerInventory = new InvWrapper(playerInventory);

        addPlayerSlots(this.playerInventory);
        addCustomSlots();
    }

    @Override
    public boolean stillValid(Player player) {
        Level level = producer.getLevel();
        BlockPos pos = producer.getBlockPos();

        return !level.getBlockState(pos).is(ModContent.PRODUCER_BLOCK.get()) ? false : player.distanceToSqr(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
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
        addSlot(new SlotItemHandler(producer.getItemHandler(), 1, 52, 20));
        addSlot(new SlotItemHandler(producer.getItemHandler(), 2, 52, 62));
        addSlot(new SlotItemHandler(producer.getItemHandler(), 3, 110, 20));
        addSlot(new SlotItemHandler(producer.getItemHandler(), 4, 110, 62));
    }

    /**
     * This is actually needed in order to achieve shift click functionality in the Controller GUI. If this method isn't overridden, the game crashes.
     */
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            itemStack = slotStack.copy();

            if (index >= 36 && index <= 39) { //originating slot is custom slot
                if (!this.moveItemStackTo(slotStack, 0, 36, true)) {
                    return ItemStack.EMPTY; // Inventory is full, can't transfer item!
                }
            } else {
                if(slotStack.getItem() instanceof BucketItem bucket) {
                    if (bucket.content.equals(ModContent.STABILIZER.get()) || YMConfig.CONFIG.alternativeStabilizer.get().equalsIgnoreCase(RegistryUtil.getRegistryName(bucket.content).getPath())) {
                        if(!this.moveItemStackTo(slotStack, 36, 37, false)) {
                            return ItemStack.EMPTY; // custom slot is full, can't transfer item!
                        }
                    } else if(bucket == Items.BUCKET) {
                        if(!this.moveItemStackTo(slotStack, 38, 39, false)) {
                            return ItemStack.EMPTY; // custom slot is full, can't transfer item!
                        }
                    }
                } else if(slotStack.getItem().equals(Items.BUCKET)) {
                    if(!this.moveItemStackTo(slotStack, 38, 39, false)) {
                        return ItemStack.EMPTY; // custom slot is full, can't transfer item!
                    }

                } else {
                    IFluidHandlerItem h = slotStack.getCapability(Capabilities.FluidHandler.ITEM);
                    if(h != null) {
                        if (h.getFluidInTank(0).isEmpty() || h.getFluidInTank(0).getFluid().isSame(ModContent.UMATTER.get())) {
                            if(!this.moveItemStackTo(slotStack, 38, 39, false)) {
                                return ItemStack.EMPTY; // custom slot is full, can't transfer item!
                            }
                        } else if (h.getFluidInTank(0).getFluid().isSame(ModContent.STABILIZER.get()) || YMConfig.CONFIG.alternativeStabilizer.get().equalsIgnoreCase(RegistryUtil.getRegistryName(h.getFluidInTank(0).getFluid()).getPath())) {
                            if(!this.moveItemStackTo(slotStack, 36, 37, false)) {
                                return ItemStack.EMPTY; // custom slot is full, can't transfer item!
                            }
                        } else {
                            return ItemStack.EMPTY;
                        }
                        return ItemStack.EMPTY;
                    }
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
