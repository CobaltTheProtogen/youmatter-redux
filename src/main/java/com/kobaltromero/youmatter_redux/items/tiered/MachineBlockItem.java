package com.kobaltromero.youmatter_redux.items.tiered;

import com.kobaltromero.youmatter_redux.util.ITier;
import com.kobaltromero.youmatter_redux.util.MachineType;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MachineBlockItem extends BlockItem {

    private MachineType type;
    private ITier tier;

    public MachineBlockItem(Block block, Item.Properties props, MachineType type, ITier tier) {
        super(block, props);
        this.type = type;
        this.tier = tier;
    }

    public int getNameColor() {
        return tier.getColor();
    }

    @Override
    public @NotNull Component getName(ItemStack stack) {
        return Component.translatable(this.getDescriptionId(stack)).withColor(getNameColor());
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        if (Screen.hasAltDown()) {
            String tooltipKey = switch (type) {
                case PRODUCER -> "youmatter.tooltip.producer";
                case REPLICATOR -> "youmatter.tooltip.replicator";
                case SCANNER -> "youmatter.tooltip.scanner";
                case ENCODER -> "youmatter.tooltip.encoder";
                default -> "youmatter.tooltip.null";
            };
            tooltip.add(Component.literal(I18n.get(tooltipKey)));
        }
    }
}

