package com.kobaltromero.youmatter_redux.components;

import com.google.common.collect.Iterables;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;


// Slightly modified ItemContainerContents for the thumb drive to allow for more than 256 slots per thumb drive.
public final class ThumbDriveContents {
    private static final int MAX_SIZE = 16384;
    public static final ThumbDriveContents EMPTY = new ThumbDriveContents(NonNullList.create());
    public static final Codec<ThumbDriveContents> CODEC = ThumbDriveContents.Slot.CODEC
            .sizeLimitedListOf(MAX_SIZE)
            .xmap(ThumbDriveContents::fromSlots, ThumbDriveContents::asSlots);
    public static final StreamCodec<RegistryFriendlyByteBuf, ThumbDriveContents> STREAM_CODEC = ItemStack.OPTIONAL_STREAM_CODEC
            .apply(ByteBufCodecs.list(MAX_SIZE))
            .map(ThumbDriveContents::new, contents -> contents.items);
    private final NonNullList<ItemStack> items;
    private final int hashCode;

    private ThumbDriveContents(NonNullList<ItemStack> items) {
        if (items.size() > MAX_SIZE) {
            throw new IllegalArgumentException("Got " + items.size() + " items, but maximum is 16384");
        } else {
            this.items = items;
            this.hashCode = ItemStack.hashStackList(items);
        }
    }

    private ThumbDriveContents(int size) {
        this(NonNullList.withSize(size, ItemStack.EMPTY));
    }

    private ThumbDriveContents(List<ItemStack> items) {
        this(items.size());

        for (int i = 0; i < items.size(); i++) {
            setItem(i, items.get(i));
        }
    }

    private void setItem(int index, ItemStack itemStack) {
        if (!itemStack.isEmpty() && itemStack.getCount() > 1) {
            throw new IllegalArgumentException("Each slot can only contain one item regardless of stack size");
        }
        this.items.set(index, itemStack);
    }

    private static ThumbDriveContents fromSlots(List<ThumbDriveContents.Slot> slots) {
        OptionalInt optionalint = slots.stream().mapToInt(ThumbDriveContents.Slot::index).max();
        if (optionalint.isEmpty()) {
            return EMPTY;
        } else {
            ThumbDriveContents contents = new ThumbDriveContents(optionalint.getAsInt() + 1);

            for (ThumbDriveContents.Slot contents$slot : slots) {
                contents.setItem(contents$slot.index(), contents$slot.item());
            }

            return contents;
        }
    }

    public static ThumbDriveContents fromItems(List<ItemStack> items) {
        int i = findLastNonEmptySlot(items);
        if (i == -1) {
            return EMPTY;
        } else {
            ThumbDriveContents contents = new ThumbDriveContents(i + 1);

            for (int j = 0; j <= i; j++) {
                contents.setItem(j, items.get(j).copy());
            }

            return contents;
        }
    }

    private static int findLastNonEmptySlot(List<ItemStack> items) {
        for (int i = items.size() - 1; i >= 0; i--) {
            if (!items.get(i).isEmpty()) {
                return i;
            }
        }

        return -1;
    }

    private List<ThumbDriveContents.Slot> asSlots() {
        List<ThumbDriveContents.Slot> list = new ArrayList<>();

        for (int i = 0; i < this.items.size(); i++) {
            ItemStack itemstack = this.items.get(i);
            if (!itemstack.isEmpty()) {
                list.add(new ThumbDriveContents.Slot(i, itemstack));
            }
        }

        return list;
    }

    public Iterable<ItemStack> nonEmptyItems() {
        return Iterables.filter(this.items, stack -> !stack.isEmpty());
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else {
            return other instanceof ThumbDriveContents contents && ItemStack.listMatches(this.items, contents.items);
        }
    }

    @Override
    public int hashCode() {
        return this.hashCode;
    }

    public int getSlots() {
        return this.items.size();
    }

    record Slot(int index, ItemStack item) {
        public static final Codec<ThumbDriveContents.Slot> CODEC = RecordCodecBuilder.create(
                builder -> builder.group(
                                Codec.intRange(0, 16383).fieldOf("slot").forGetter(ThumbDriveContents.Slot::index),
                                ItemStack.CODEC.fieldOf("item").forGetter(ThumbDriveContents.Slot::item)
                        )
                        .apply(builder, ThumbDriveContents.Slot::new)
        );
    }
}