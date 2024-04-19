package earth.terrarium.spirit.common.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefullib.common.color.Color;
import com.teamresourceful.resourcefullib.common.recipe.CodecRecipe;
import earth.terrarium.spirit.api.souls.impl.BasicSoulContainer;
import earth.terrarium.spirit.api.souls.stack.SoulStack;
import earth.terrarium.spirit.api.souls.util.SoulIngredient;
import earth.terrarium.spirit.common.registry.SpiritRecipes;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public record DungeonRecipe(ResourceLocation id, List<SoulIngredient> inputs, ResourceLocation structure, Color portalColor) implements CodecRecipe<Container> {

    public static Codec<DungeonRecipe> codec(ResourceLocation id) {
        return RecordCodecBuilder.create(dungeonRecipeInstance -> dungeonRecipeInstance.group(
                RecordCodecBuilder.point(id),
                SoulIngredient.CODEC.listOf().fieldOf("inputs").forGetter(DungeonRecipe::inputs),
                ResourceLocation.CODEC.fieldOf("structure").forGetter(DungeonRecipe::structure),
                Color.CODEC.fieldOf("portalColor").forGetter(DungeonRecipe::portalColor)
        ).apply(dungeonRecipeInstance, DungeonRecipe::new));
    }

    @Override
    public boolean matches(Container container, Level level) {
        return false;
    }

    public boolean matches(BasicSoulContainer container) {
        Object2IntMap<SoulIngredient> ingredients = new Object2IntArrayMap<>();
        inputs.forEach(ingredient -> ingredients.put(ingredient, 0));
        for (SoulStack stack : container.getStacks()) {
            for (SoulIngredient ingredient : inputs) {
                if (ingredient.test(stack)) {
                    ingredients.put(ingredient, ingredients.getInt(ingredient) + 1);
                }
            }
        }
        return ingredients.values().stream().allMatch(value -> value > 0);
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return SpiritRecipes.DUNGEON_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return SpiritRecipes.DUNGEON.get();
    }

    public static Optional<DungeonRecipe> getRecipe(BasicSoulContainer container, Level level) {
        return level.getRecipeManager().getAllRecipesFor(SpiritRecipes.DUNGEON.get()).stream()
                .filter(recipe -> recipe.matches(container))
                .findFirst();
    }
}
