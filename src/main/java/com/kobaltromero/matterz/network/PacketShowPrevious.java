package com.kobaltromero.matterz.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record PacketShowPrevious() implements CustomPacketPayload {

    public static final Type<PacketShowPrevious> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("youmatter", "packet_show_previous"));

    public static final StreamCodec<ByteBuf, PacketShowPrevious> STREAM_CODEC = StreamCodec.unit(new PacketShowPrevious());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
