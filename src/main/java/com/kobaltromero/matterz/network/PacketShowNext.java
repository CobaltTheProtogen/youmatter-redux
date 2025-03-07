package com.kobaltromero.matterz.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record PacketShowNext() implements CustomPacketPayload {

    public static final Type<PacketShowNext> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("youmatter", "packet_show_next"));

    public static final StreamCodec<ByteBuf, PacketShowNext> STREAM_CODEC = StreamCodec.unit(new PacketShowNext());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
