package earth.terrarium.spirit.common.recipes;

import com.teamresourceful.resourcefullib.common.recipe.CodecRecipe;
import earth.terrarium.spirit.api.abilities.armor.ArmorAbility;
import earth.terrarium.spirit.api.rituals.components.RitualComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.List;

public record ArmorAttunement(ResourceLocation id, short duration, EntityType<?> entityType, List<RitualComponent<?>> inputs, ArmorAbility ability) implements CodecRecipe<Container>, AttunementRecipe {
    @Override
    public boolean matches(Container container, Level level) {
        return false;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return null;
    }

    @Override
    public RecipeType<?> getType() {
        return null;
    }
}
