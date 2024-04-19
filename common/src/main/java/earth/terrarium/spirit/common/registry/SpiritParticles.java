package earth.terrarium.spirit.common.registry;

import com.mojang.serialization.Codec;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry;
import earth.terrarium.botarium.common.fluid.utils.FluidParticleOptions;
import earth.terrarium.spirit.Spirit;
import earth.terrarium.spirit.api.client.DripParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import org.jetbrains.annotations.NotNull;

public class SpiritParticles {
    public static final ResourcefulRegistry<ParticleType<?>> PARTICLE_TYPES = ResourcefulRegistries.create(BuiltInRegistries.PARTICLE_TYPE, Spirit.MODID);

    public static final RegistryEntry<ParticleType<DripParticleOptions>> DRIP = PARTICLE_TYPES.register("drip", () -> new ParticleType<>(false, DripParticleOptions.DESERIALIZER) {
        @Override
        public @NotNull Codec<DripParticleOptions> codec() {
            return DripParticleOptions.CODEC;
        }
    });
}
