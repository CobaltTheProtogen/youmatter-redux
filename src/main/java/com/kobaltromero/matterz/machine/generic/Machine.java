package com.kobaltromero.matterz.machine.generic;

import com.kobaltromero.matterz.api.machine.ITier;
import com.kobaltromero.matterz.api.machine.MachineType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class Machine extends AbstractMachine implements MenuProvider {
    public Machine(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }



    @Override
    public @Nullable AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player playerEntity) {
        return getMachineType().create(windowId, level, worldPosition, playerInventory, playerEntity);
    }

    @Override
    public Component getDisplayName() {
        return null;
    }

    @Override
    public AbstractMachineBlock getMachineBlock() {
        return null;
    }
}
