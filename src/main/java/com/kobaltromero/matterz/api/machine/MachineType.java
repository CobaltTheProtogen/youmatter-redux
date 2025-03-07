package com.kobaltromero.matterz.api.machine;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.Nullable;

public class MachineType implements IMachineGUI {

    private final IMachineGUI menuProvider;

    public MachineType(IMachineGUI menuProvider) {
        this.menuProvider = menuProvider;
    }

    public @Nullable AbstractContainerMenu create(int windowId, Inventory playerInventory, Player player) {
        return menuProvider.create(windowId, playerInventory, player);
    }
}
