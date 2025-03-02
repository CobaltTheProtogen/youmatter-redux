package com.kobaltromero.youmatter_redux.blocks.basic.producer;


import com.kobaltromero.youmatter_redux.block_entities.MachineBlockEntity;
import com.kobaltromero.youmatter_redux.blocks.generic.MachineBlock;
import com.kobaltromero.youmatter_redux.util.MachineType;
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


public class ProducerMenu extends AbstractContainerMenu {

    public MachineBlockEntity machine;
    private IItemHandler playerInventory;


    public ProducerMenu(int windowId, Level level, BlockPos pos, Inventory playerInventory, Player player) {
        super(ModContent.PRODUCER_MENU.get(), windowId);
        this.machine = level.getBlockEntity(pos) instanceof MachineBlockEntity machine ? machine : null;
        this.playerInventory = new InvWrapper(playerInventory);

        addPlayerSlots(this.playerInventory);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        Level level = machine.getLevel();
        BlockPos pos = machine.getBlockPos();

        return level.getBlockState(pos).getBlock() instanceof MachineBlock && machine.getMachineType() == MachineType.PRODUCER && player.distanceToSqr(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
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
}