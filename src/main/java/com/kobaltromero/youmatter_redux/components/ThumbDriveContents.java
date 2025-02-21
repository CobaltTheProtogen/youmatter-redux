package com.kobaltromero.youmatter_redux.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.ArrayList;
import java.util.List;


// Slightly modified ItemContainerContents for the thumb drive to allow for more than 256 slots per thumb drive.
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.apache.commons.lang3.tuple.Pair;

public final class ThumbDriveContents {
    private static final int MAX_SIZE = 16384;
    public static final ThumbDriveContents EMPTY = new ThumbDriveContents(new Int2ObjectArrayMap<>());
    public static final Codec<ThumbDriveContents> CODEC = createCodec();
    public static final StreamCodec<RegistryFriendlyByteBuf, ThumbDriveContents> STREAM_CODEC = createStreamCodec();

    private static Codec<ThumbDriveContents> createCodec() {
        Codec<Pair<Integer, Item>> itemCodec = RecordCodecBuilder.create(
                builder -> builder.group(
                        Codec.intRange(0, MAX_SIZE -1).fieldOf("slot").forGetter(Pair::getLeft),
                        BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(Pair::getRight)
                ).apply(builder, Pair::of)
        );

        return itemCodec.listOf().xmap(list -> {
            Int2ObjectArrayMap<Item> map = new Int2ObjectArrayMap<>();
            list.forEach(pair -> map.put(pair.getLeft(), pair.getRight()));
            return new ThumbDriveContents(map);
        }, contents -> {
            List<Pair<Integer, Item>> list = new ArrayList<>();
            contents.items.forEach((index, item) -> list.add(Pair.of(index, item)));
            return list;
        });

    }

    private static StreamCodec<RegistryFriendlyByteBuf, ThumbDriveContents> createStreamCodec() {
        return new StreamCodec<>() {
            @Override
            public void encode(RegistryFriendlyByteBuf buf, ThumbDriveContents contents) {
                buf.writeVarInt(contents.items.size()); // Write the number of items
                contents.items.forEach((index, item) -> {
                    buf.writeVarInt(index);             // Write the slot index
                    buf.writeResourceLocation(ResourceLocation.parse(item.toString())); //Write the Item's Registry Name
                });
            }

            @Override
            public ThumbDriveContents decode(RegistryFriendlyByteBuf buf) {
                int size = buf.readVarInt();
                Int2ObjectArrayMap<Item> items = new Int2ObjectArrayMap<>();
                for (int i = 0; i < size; i++) {
                    int index = buf.readVarInt();
                    Item item = BuiltInRegistries.ITEM.get(buf.readResourceLocation()); //Read the Item's Registry Name
                    items.put(index, item);
                }
                return new ThumbDriveContents(items);
            }
        };
    }
    private final Int2ObjectArrayMap<Item> items;
    private final int hashCode;

    public ThumbDriveContents(Int2ObjectArrayMap<Item> items) {
        if (items.size() > MAX_SIZE) {
            throw new IllegalArgumentException("Got " + items.size() + " items, but maximum is 16384");
        } else {
            this.items = items;
            this.hashCode = items.hashCode(); // Simplified hash code
        }
    }

    public void setItem(int index, Item item) {
        if (index < 0 || index >= MAX_SIZE) {
            throw new IndexOutOfBoundsException("Index " + index + " is out of bounds");
        }
        this.items.put(index, item);
    }



    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else {
            return other instanceof ThumbDriveContents contents && this.items.equals(contents.items);
        }
    }

    @Override
    public int hashCode() {
        return this.hashCode;
    }

    public int getSlots() {
        return this.items.size();
    }

    public Iterable<Item> nonEmptyItems() {
        return items.values();
    }

    public Item getItem(int slot){
        return items.getOrDefault(slot, Items.AIR); //Return air if nothing is in the slot
    }
}