package com.kobaltromero.youmatter_redux.items;

import com.kobaltromero.youmatter_redux.ModContent;
import com.kobaltromero.youmatter_redux.components.ThumbDriveContents;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ThumbDriveItem extends Item {

    public ThumbDriveContents getThumbDriveContents(ItemStack stack) {
        return stack.get(ModContent.THUMBDRIVE_CONTAINER.get());
    }

    public ThumbDriveItem() {
        super(new Properties().stacksTo(1).component(ModContent.THUMBDRIVE_CONTAINER, ThumbDriveContents.EMPTY));
    }

    public int getMaxStorageInKb() {
        return 1; // Minimum of 1 item can be stored. For obvious reasons.
    }

    @Override
    public boolean isBarVisible(@NotNull ItemStack stack) {
       return true;
    }


    @Override
    public int getBarWidth(@NotNull ItemStack stack) {
        ThumbDriveContents contents = getThumbDriveContents(stack);
        if(contents != null) {
            float filledRatio = (float)contents.getSlots() / (float)this.getMaxStorageInKb();
            return Math.round(filledRatio * 13.0F);
        }
        return 0;
    }

    @Override
    public int getBarColor(@NotNull ItemStack stack) {
         return 0xFFFF00;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        ThumbDriveContents contents = getThumbDriveContents(stack);
        if (contents != null) {
            int maxStorage = getMaxStorageInKb();
            int freeStorage = maxStorage - contents.getSlots();
            int color = getChatColor(freeStorage, maxStorage);
            if (Screen.hasAltDown()) {
                tooltip.add(Component.literal(I18n.get("youmatter.tooltip.remainingSpace", freeStorage, maxStorage)).withColor(color));
            }
        }
    }

    private static int getChatColor(int freeStorage, int maxStorage) {
        int percentageFree = (freeStorage * 100) / maxStorage;
        return switch (percentageFree / 25) {
            case 0 -> (percentageFree == 0) ? 0x808080 : 0xFF0000;
            case 1 -> 0xFF8000;
            case 2 -> 0xFFFF00;
            default -> 0x00FF00;
        };
    }
}
