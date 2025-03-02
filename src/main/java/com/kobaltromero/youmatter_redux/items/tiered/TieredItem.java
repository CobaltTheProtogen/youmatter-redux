package com.kobaltromero.youmatter_redux.items.tiered;

import com.kobaltromero.youmatter_redux.util.ITier;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class TieredItem extends Item {
    public final ITier tier;

    public TieredItem(Properties properties, ITier tier) {
        super(properties);
        this.tier = tier;
    }

    public int getNameColor() {
        return tier.getColor();
    }

    @Override
    public @NotNull Component getName(ItemStack stack) {
        return Component.translatable(this.getDescriptionId(stack)).withColor(getNameColor());
    }
}

