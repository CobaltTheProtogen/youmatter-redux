package com.kobaltromero.youmatter_redux.blocks.replicator;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.ForwardingItemHandler;
import org.jetbrains.annotations.NotNull;

public class RestrictedItemHandler extends ForwardingItemHandler {
    public RestrictedItemHandler(IItemHandler delegate) {
        super(delegate);
    }
    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (!(slot == 1)) {
            return ItemStack.EMPTY; // Prevent extraction from all slots except slot 1.
        } else {
            return delegate.get().extractItem(slot, amount, simulate);
        }
    }
}
