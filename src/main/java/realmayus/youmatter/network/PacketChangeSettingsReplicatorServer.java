package realmayus.youmatter.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record PacketChangeSettingsReplicatorServer(boolean isActivated, boolean mode) implements CustomPacketPayload {

    public static final Type<PacketChangeSettingsReplicatorServer> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("youmatter", "packet_change_settings_replicator"));

    public static final StreamCodec<ByteBuf, PacketChangeSettingsReplicatorServer> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL,
            PacketChangeSettingsReplicatorServer::isActivated,
            ByteBufCodecs.BOOL,
            PacketChangeSettingsReplicatorServer::mode,
            PacketChangeSettingsReplicatorServer::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
