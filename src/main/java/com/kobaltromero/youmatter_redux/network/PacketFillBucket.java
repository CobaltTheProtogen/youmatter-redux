package com.kobaltromero.youmatter_redux.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.MenuProvider;

public record PacketFillBucket(BlockPos pos) implements CustomPacketPayload {

    public static final Type<PacketFillBucket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("youmatter", "packet_fill_bucket"));

    public static final StreamCodec<ByteBuf, PacketFillBucket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            PacketFillBucket::pos,
            PacketFillBucket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
