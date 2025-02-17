package realmayus.youmatter.items;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import realmayus.youmatter.ModContent;

import javax.annotation.Nullable;
import java.util.List;

public class ThumbdriveItem extends Item {
    public ThumbdriveItem() {
        super(new Properties().stacksTo(1).component(DataComponents.CONTAINER, ItemContainerContents.EMPTY));
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        ItemContainerContents itemsStored = stack.get(DataComponents.CONTAINER);
        if (itemsStored != null)
            if (itemsStored.getSlots() > 0) {
                tooltip.add(Component.literal(I18n.get("youmatter.tooltip.dataStored")));
                tooltip.add(Component.literal(I18n.get("youmatter.tooltip.remainingSpace", itemsStored.getSlots(), 8)));
            } else {
                tooltip.add(Component.literal(I18n.get("youmatter.tooltip.noDataStored")));
            }
    }
}
