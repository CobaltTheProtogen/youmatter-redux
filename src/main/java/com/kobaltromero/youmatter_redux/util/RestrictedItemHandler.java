package com.kobaltromero.youmatter_redux.util;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.wrapper.ForwardingItemHandler;

import java.util.function.Supplier;

public class RestrictedItemHandler extends ForwardingItemHandler {

    public RestrictedItemHandler(IItemHandler delegate) {
        super(delegate);
    }

    public RestrictedItemHandler(Supplier<IItemHandler> delegate) {
        super(delegate);
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (slot == 0) {
            return ItemStack.EMPTY; // Prevent extraction from slot 0
        } else {
            return delegate.get().extractItem(slot, amount, simulate);
        }
    }
}
