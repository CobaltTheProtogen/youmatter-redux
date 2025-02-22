package com.kobaltromero.youmatter_redux.network;

import net.neoforged.neoforge.network.handling.IPayloadContext;

import com.kobaltromero.youmatter_redux.producer.ProducerMenu;
import com.kobaltromero.youmatter_redux.replicator.ReplicatorMenu;

public class PacketHandler {
    private PacketHandler() {
        // Private constructor to prevent instantiation
    }

    public static class ProducerSettings {

        private ProducerSettings() {}

        public static void handle(final PacketChangeSettingsProducerServer data, final IPayloadContext ctx) {
            // This is the player the packet was sent to the server from
            ctx.enqueueWork(() -> {
                if (ctx.player().containerMenu instanceof ProducerMenu openContainer) {
                    openContainer.producer.setActivated(data.isActivated());
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
                    openContainer.replicator.setActivated(data.isActivated());
                    openContainer.replicator.setCurrentMode(data.mode());
                }
            });
        }
    }
}
