package realmayus.youmatter.network;

//import realmayus.youmatter.creator.ContainerCreator;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record PacketChangeSettingsCreatorServer(boolean isActivated) implements CustomPacketPayload {

    public static final Type<PacketChangeSettingsCreatorServer> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("youmatter", "packet_change_settings_creator"));

    public static final StreamCodec<ByteBuf, PacketChangeSettingsCreatorServer> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL,
            PacketChangeSettingsCreatorServer::isActivated,
            PacketChangeSettingsCreatorServer::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}