package earth.terrarium.spirit.api.rituals.components;

import earth.terrarium.spirit.common.recipes.MagicalRecipe;
import earth.terrarium.spirit.compat.rei.ComponentUtils;
import earth.terrarium.spirit.compat.rei.categories.TransmutationRecipeCategory;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface RitualComponent<T extends RitualComponent<T>> {

    boolean matches(MagicalRecipe recipe, Level level, @Nullable BlockPos componentPos, BlockPos ritualPos);

    void onRitualBegin(MagicalRecipe recipe, Level level, @Nullable BlockPos componentPos, BlockPos ritualPos);

    void onRitualAbort(MagicalRecipe recipe, Level level, @Nullable BlockPos componentPos, BlockPos ritualPos);

    void onRitualComplete(MagicalRecipe recipe, Level level, @Nullable BlockPos componentPos, BlockPos ritualPos);

    default boolean requiresSpecificPosition() {
        return true;
    }

    List<Ingredient> getIngredients();

    ComponentUtils.ReiPlacer getREIPlacer();

    RitualComponentSerializer<T> getSerializer();
}
