package com.kobaltromero.matterz.machine;

import com.kobaltromero.matterz.machine.generic.Machine;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class MachineProducer extends Machine {
    public MachineProducer(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }
}
