package earth.terrarium.spirit.common.recipes;

import earth.terrarium.spirit.api.rituals.results.RitualResult;
import net.minecraft.world.item.crafting.Ingredient;

public interface MagicalRecipe {
    short duration();
    Ingredient catalyst();
    RitualResult<?> result();
}
