package com.kobaltromero.youmatter_redux.items.tiered;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class TieredItem extends Item {

    public enum Tier {
        BASIC(0x03F288),
        ADVANCED(0xD22C20),
        ELITE(0x31E1DF),
        ULTIMATE(0xA300F0);

        private final int color;

        Tier(int color) {
            this.color = color;
        }

        public int getColor() {
            return color;
        }
    }

    private final Tier tier;

    public TieredItem(Properties properties, Tier tier) {
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

