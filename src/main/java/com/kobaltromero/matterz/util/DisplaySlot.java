package com.kobaltromero.matterz.util;

import javax.annotation.Nonnull;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class DisplaySlot extends SlotItemHandler {
    public DisplaySlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public boolean mayPlace(@Nonnull ItemStack stack) {
        return false;
    }

    @Override
    public boolean mayPickup(Player player) {
        return false;
    }


}
