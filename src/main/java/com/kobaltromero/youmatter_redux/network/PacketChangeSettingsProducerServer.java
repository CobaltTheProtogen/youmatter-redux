package com.kobaltromero.youmatter_redux.network;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record PacketChangeSettingsProducerServer(boolean isActivated) implements CustomPacketPayload {

    public static final Type<PacketChangeSettingsProducerServer> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("youmatter", "packet_change_settings_producer"));

    public static final StreamCodec<ByteBuf, PacketChangeSettingsProducerServer> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL,
            PacketChangeSettingsProducerServer::isActivated,
            PacketChangeSettingsProducerServer::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}