package com.kobaltromero.youmatter_redux.network;

import com.kobaltromero.youmatter_redux.ModContent;
import com.kobaltromero.youmatter_redux.block_entities.MachineBlockEntity;
import com.kobaltromero.youmatter_redux.blocks.replicator.ReplicatorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import com.kobaltromero.youmatter_redux.blocks.basic.producer.ProducerMenu;

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
                    openContainer.machine.setActivated(data.isActivated());
                }
            });
        }
    }

    public static class FillBucket {

        private FillBucket() {}

        public static void handle(final PacketFillBucket data, final IPayloadContext ctx) {
            ctx.enqueueWork(() -> {
                ServerPlayer player = (ServerPlayer) ctx.player();
                BlockPos pos = data.pos();
                BlockEntity machine = player.level().getBlockEntity(pos);

                if (machine == null) {
                    System.out.println("Block entity is null. Check the position: " + pos);
                    return;
                }
                ItemStack carriedItem = player.containerMenu.getCarried();
                if (carriedItem.getItem() == Items.BUCKET && carriedItem.getCount() > 0) {
                    if (machine instanceof MachineBlockEntity producer) {
                        if (producer.getTank().getFluidAmount() >= 1000) {
                            ItemStack uMatterBucket = new ItemStack(ModContent.UMATTER_BUCKET);
                            if (!player.getInventory().add(uMatterBucket)) {
                                // If inventory is full, drop the item at the player's position
                                player.drop(uMatterBucket, false);
                            }
                            carriedItem.shrink(1);
                            player.containerMenu.setCarried(carriedItem);
                            producer.getTank().drain(1000, IFluidHandler.FluidAction.EXECUTE);
                            System.out.println("Packet sent!");
                        } else {
                            System.out.println("Not enough fluid in the machine!");
                        }
                    } else {
                        System.out.println("Block entity is not an instance of ProducerBlockEntity.");
                    }
                }
            });
        }
    }

    public static class ShowNext {

        private ShowNext() {}

        public static void handle(final PacketShowNext data, final IPayloadContext ctx) {
            ctx.enqueueWork(() -> {
                if (ctx.player().containerMenu instanceof ReplicatorMenu openContainer) {
                   // openContainer.machine.renderNext();
                }
            });
        }
    }

    public static class ShowPrevious {

        private ShowPrevious() {}

        public static void handle(final PacketShowPrevious data, final IPayloadContext ctx) {
            ctx.enqueueWork(() -> {
                if (ctx.player().containerMenu instanceof ReplicatorMenu openContainer) {
                  // openContainer.machine.renderPrevious();
                }
            });
        }
    }

    public static class ReplicatorSettings {

        private ReplicatorSettings() {}

        public static void handle(final PacketChangeSettingsReplicatorServer data, final IPayloadContext ctx) {
            ctx.enqueueWork(() -> {
                if (ctx.player().containerMenu instanceof ReplicatorMenu openContainer) {
                    openContainer.machine.setActivated(data.isActivated());
                    openContainer.machine.setCurrentMode(data.mode());
                }
            });
        }
    }
}
