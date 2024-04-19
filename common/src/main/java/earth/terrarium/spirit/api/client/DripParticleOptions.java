package earth.terrarium.spirit.api.client;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import earth.terrarium.botarium.common.fluid.base.FluidHolder;
import earth.terrarium.spirit.common.registry.SpiritParticles;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record DripParticleOptions(FluidHolder fluid) implements ParticleOptions {

    @Override
    public @NotNull ParticleType<?> getType() {
        return SpiritParticles.DRIP.get();
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buffer) {
        fluid.writeToBuffer(buffer);
    }

    @Override
    public @NotNull String writeToString() {
        return String.format("%s %s", BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()), BuiltInRegistries.FLUID.getKey(fluid.getFluid()));
    }

    public static final Codec<DripParticleOptions> CODEC = FluidHolder.NEW_CODEC.xmap(DripParticleOptions::new, DripParticleOptions::fluid);

    public static final Deserializer<DripParticleOptions> DESERIALIZER = new Deserializer<>() {

        public @NotNull DripParticleOptions fromCommand(ParticleType<DripParticleOptions> particleTypeIn, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            return new DripParticleOptions(FluidHolder.of(BuiltInRegistries.FLUID.get(ResourceLocation.tryParse(reader.readQuotedString()))));
        }

        public @NotNull DripParticleOptions fromNetwork(ParticleType<DripParticleOptions> particleTypeIn, FriendlyByteBuf buffer) {
            return new DripParticleOptions(FluidHolder.readFromBuffer(buffer));
        }
    };
}
