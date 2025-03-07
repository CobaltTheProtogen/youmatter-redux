package com.kobaltromero.matterz.api.machine;

import com.kobaltromero.matterz.machine.generic.AbstractMachineBlock;

public interface IMachine {
    AbstractMachineBlock getMachineBlock();

    ITier getTier();

    int getMaxEnergy();

    int getMaxFluid();

    MachineType getMachineType();

    boolean isActivated();

    void setActivated(boolean isActivated);

    boolean containsFluid();

    void setContainsFluid(boolean containsFluid);

    boolean isMachineActive();

    void setMachineActive(boolean isActive);

    boolean getLastSignal();

    void setLastSignal(boolean lastSignal);

    boolean isCurrentMode();

    void setCurrentMode(boolean currentMode);

    int getCurrentPartTick();

    void setCurrentPartTick(int currentPartTick);

    void incrementPartTick();

    int getProgress();

    void setProgress(int progress);

    void incrementProgress();
}
