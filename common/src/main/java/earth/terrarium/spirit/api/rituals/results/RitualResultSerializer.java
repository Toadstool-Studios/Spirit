package earth.terrarium.spirit.api.rituals.results;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;

public interface RitualResultSerializer<T extends RitualResult<T>> {
    ResourceLocation id();
    Codec<T> codec();
}
