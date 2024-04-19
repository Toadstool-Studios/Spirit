package earth.terrarium.spirit.common.recipes;

import earth.terrarium.spirit.api.rituals.components.RitualComponent;
import net.minecraft.world.entity.EntityType;

import java.util.List;

public interface AttunementRecipe {
    EntityType<?> entityType();
    List<RitualComponent<?>> inputs();
}
