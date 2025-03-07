package com.kobaltromero.matterz.api.registry;

import com.kobaltromero.matterz.MatterZ;
import com.kobaltromero.matterz.api.machine.Tier;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class MZKeys {
    public static final ResourceKey<Registry<Tier>> TIER = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(MatterZ.ID, "tier"));
}
