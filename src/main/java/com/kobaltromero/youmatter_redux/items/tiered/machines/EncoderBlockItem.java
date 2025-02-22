package com.kobaltromero.youmatter_redux.items.tiered.machines;

import com.kobaltromero.youmatter_redux.items.tiered.TieredBlockItem;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class EncoderBlockItem extends TieredBlockItem {
    public EncoderBlockItem(Block block, Properties props, Tier tier) {
        super(block, props, tier);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        if (Screen.hasAltDown()) {
            tooltip.add(Component.literal(I18n.get("youmatter.tooltip.encoder")));
        }
    }
}
