package com.kobaltromero.youmatter_redux.items.tiered.machines;

import com.kobaltromero.youmatter_redux.items.tiered.MachineBlockItem;
import com.kobaltromero.youmatter_redux.util.ITier;
import com.kobaltromero.youmatter_redux.util.MachineType;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class ScannerBlockItem extends MachineBlockItem {
    public ScannerBlockItem(Block block, Properties props, MachineType type, ITier tier) {
        super(block, props, type, tier);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        if (Screen.hasAltDown()) {
            tooltip.add(Component.literal(I18n.get("youmatter.tooltip.scanner")));
        }
    }
}
