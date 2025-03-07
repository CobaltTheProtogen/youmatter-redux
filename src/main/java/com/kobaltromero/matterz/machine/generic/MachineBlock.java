package com.kobaltromero.matterz.machine.generic;

import com.kobaltromero.matterz.api.machine.ITier;
import com.kobaltromero.matterz.api.machine.MachineType;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class MachineBlock extends AbstractMachineBlock {
    public MachineBlock(Properties properties, int maxEnergy, int maxFluid, MachineType type, ITier tier) {
        super(properties, maxEnergy, maxFluid, type, tier);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return null;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return null;
    }
}
