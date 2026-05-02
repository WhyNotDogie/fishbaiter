package dev.dogie.fishbaiter;

import dev.dogie.fishbaiter.data.BaitComponent;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class FishDataComponents
{
    public static final DeferredRegister.DataComponents REGISTRAR = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, Fishbaiter.MODID);

    public static final Supplier<DataComponentType<BaitComponent>> BAIT_COMPONENT = REGISTRAR.registerComponentType(
            "bait",
            builder -> builder
                    // The codec to read/write the data to disk
                    .persistent(BaitComponent.BAIT_CODEC)
                    // The codec to read/write the data across the network
                    .networkSynchronized(BaitComponent.BAIT_STREAM_CODEC)
    );
}
