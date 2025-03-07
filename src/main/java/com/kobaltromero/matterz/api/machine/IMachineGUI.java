package com.kobaltromero.matterz.api.machine;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

public interface IMachineGUI {
    AbstractContainerMenu create(int windowId, Inventory inventory, Player player);
}
