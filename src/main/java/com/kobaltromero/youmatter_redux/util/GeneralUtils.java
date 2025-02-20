package com.kobaltromero.youmatter_redux.util;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import com.kobaltromero.youmatter_redux.YMConfig;

public class GeneralUtils {

    public static int getScansRequiredForItem(Item item) {
        if(YMConfig.CONFIG.getOverride(RegistryUtil.getRegistryName(item).toString()) != null) {
            return Integer.parseInt((String)YMConfig.CONFIG.getOverride(RegistryUtil.getRegistryName(item).toString())[2]);
        } else {
            return YMConfig.CONFIG.defaultScans.get();
        }
    }

    public static int getScansRequiredForItem(ItemStack[] items) {
        for(ItemStack item : items) {
            if (hasCustomScansRequired(item)) {
                return getScansRequiredForItem(item.getItem());
            }
        }
        return YMConfig.CONFIG.defaultScans.get();
    }

    public static int getUMatterAmountForItem(Item item) {
        if(YMConfig.CONFIG.getOverride(RegistryUtil.getRegistryName(item).toString()) != null) {
            return Integer.parseInt((String)YMConfig.CONFIG.getOverride(RegistryUtil.getRegistryName(item).toString())[1]);
        } else {
            return YMConfig.CONFIG.defaultAmount.get();
        }
    }

    public static int getUMatterAmountForItem(ItemStack[] items) {
        for(ItemStack item : items) {
            if (hasCustomUMatterValue(item)) {
                return getUMatterAmountForItem(item.getItem());
            }
        }
        return YMConfig.CONFIG.defaultAmount.get();
    }

    public static boolean hasCustomScansRequired(ItemStack item) {
        return YMConfig.CONFIG.getOverride(RegistryUtil.getRegistryName(item.getItem()).toString()) != null;
    }

    public static boolean hasCustomScansRequired(ItemStack[] items) {
        for(ItemStack is : items) {
            if(YMConfig.CONFIG.getOverride(RegistryUtil.getRegistryName(is.getItem()).toString()) != null) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasCustomUMatterValue(ItemStack item) {
        return YMConfig.CONFIG.getOverride(RegistryUtil.getRegistryName(item.getItem()).toString()) != null;
    }

    public static boolean hasCustomUMatterValue(ItemStack[] items) {
        for(ItemStack is : items) {
            if(YMConfig.CONFIG.getOverride(RegistryUtil.getRegistryName(is.getItem()).toString()) != null) {
                return true;
            }
        }
        return false;
    }

    public static boolean canAddItemToSlot(ItemStack slotStack, ItemStack givenStack, boolean stackSizeMatters) {
        boolean flag = slotStack.isEmpty();
        if (!flag && ItemStack.isSameItem(givenStack, slotStack) /*&& ItemStack.areItemStackTagsEqual(slotStack, givenStack)*/) {
            return slotStack.getCount() + (stackSizeMatters ? 0 : givenStack.getCount()) <= givenStack.getMaxStackSize();
        } else {
            return flag;
        }
    }
}
