package com.kobaltromero.matterz.machine.generic;

import com.kobaltromero.matterz.ModBlockStateProperties;
import com.kobaltromero.matterz.api.machine.ITier;
import com.kobaltromero.matterz.api.machine.MachineType;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public abstract class AbstractMachineBlock extends BaseEntityBlock {
    public static final BooleanProperty ACTIVE = ModBlockStateProperties.ACTIVE;
    public static final BooleanProperty CONTAINS_FLUID = ModBlockStateProperties.CONTAINS_FLUID;
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    private final int maxEnergy;
    private final int maxFluid;
    private final MachineType type;
    private final ITier tier;

    public AbstractMachineBlock(BlockBehaviour.Properties properties, int maxEnergy, int maxFluid, MachineType type, ITier tier) {
        super(properties);
        this.maxEnergy = maxEnergy;
        this.maxFluid = maxFluid;
        this.type = type;
        this.tier = tier;
        this.registerDefaultState(this.stateDefinition.any().setValue(ACTIVE, false).setValue(CONTAINS_FLUID, false));
    }

    public MachineType getMachineType() {
        return type;
    }

    public int getMaxEnergy() {
        return maxEnergy;
    }

    public int getMaxFluid() {
        return maxFluid;
    }

    public ITier getTier() {
        return tier;
    }
}
