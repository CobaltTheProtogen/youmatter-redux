package realmayus.youmatter.network;

import net.neoforged.neoforge.network.handling.IPayloadContext;

import realmayus.youmatter.creator.CreatorMenu;
import realmayus.youmatter.replicator.ReplicatorMenu;

public class PacketHandler {
    private PacketHandler() {
        // Private constructor to prevent instantiation
    }

    public static class CreatorSettings {

        private CreatorSettings() {}

        public static void handle(final PacketChangeSettingsCreatorServer data, final IPayloadContext ctx) {
            // This is the player the packet was sent to the server from
            ctx.enqueueWork(() -> {
                if (ctx.player().containerMenu instanceof CreatorMenu openContainer) {
                    openContainer.creator.setActivated(data.isActivated());
                }
            });
        }
    }

    public static class ShowNext {

        private ShowNext() {}

        public static void handle(final PacketShowNext data, final IPayloadContext ctx) {
            ctx.enqueueWork(() -> {
                if (ctx.player().containerMenu instanceof ReplicatorMenu openContainer) {
                    openContainer.replicator.renderNext();
                }
            });
        }
    }

    public static class ShowPrevious {

        private ShowPrevious() {}

        public static void handle(final PacketShowPrevious data, final IPayloadContext ctx) {
            ctx.enqueueWork(() -> {
                if (ctx.player().containerMenu instanceof ReplicatorMenu openContainer) {
                    openContainer.replicator.renderPrevious();
                }
            });
        }
    }

    public static class ReplicatorSettings {

        private ReplicatorSettings() {}

        public static void handle(final PacketChangeSettingsReplicatorServer data, final IPayloadContext ctx) {
            ctx.enqueueWork(() -> {
                if (ctx.player().containerMenu instanceof ReplicatorMenu openContainer) {
                    openContainer.replicator.setActive(data.isActivated());
                    openContainer.replicator.setCurrentMode(data.mode());
                }
            });
        }
    }
}
