package realmayus.youmatter.fluid;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;
import realmayus.youmatter.ModContent;
import realmayus.youmatter.YouMatter;

public class StabilizerFluidType extends FluidType {
    public StabilizerFluidType() {
        super(Properties.create()
                .descriptionId("block.youmatter.stabilizer")
                .fallDistanceModifier(0.0F)
                .canExtinguish(false)
                .canConvertToSource(false)
                .supportsBoating(false)
                .canHydrate(false)
                .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY));
    }
}
