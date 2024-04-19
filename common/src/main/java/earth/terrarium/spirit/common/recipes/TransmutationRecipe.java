package earth.terrarium.spirit.common.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefullib.common.codecs.recipes.IngredientCodec;
import com.teamresourceful.resourcefullib.common.recipe.CodecRecipe;
import earth.terrarium.spirit.api.rituals.components.RitualComponent;
import earth.terrarium.spirit.api.rituals.components.RitualComponentManager;
import earth.terrarium.spirit.api.rituals.results.RitualResult;
import earth.terrarium.spirit.api.rituals.results.RitualResultManager;
import earth.terrarium.spirit.common.registry.SpiritRecipes;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public record TransmutationRecipe(ResourceLocation id, Ingredient catalyst, short duration,
                                  List<RitualComponent<?>> inputs, RitualResult<?> result) implements CodecRecipe<Container>, MagicalRecipe {

    public static Codec<TransmutationRecipe> codec(ResourceLocation id) {
        return RecordCodecBuilder.create(instance -> instance.group(
                RecordCodecBuilder.point(id),
                IngredientCodec.CODEC.fieldOf("catalyst").forGetter(TransmutationRecipe::catalyst),
                Codec.SHORT.fieldOf("duration").orElse((short) 60).forGetter(TransmutationRecipe::duration),
                RitualComponentManager.CODEC.listOf().fieldOf("inputs").forGetter(TransmutationRecipe::inputs),
                RitualResultManager.CODEC.fieldOf("result").forGetter(TransmutationRecipe::result)
        ).apply(instance, TransmutationRecipe::new));
    }

    @Override
    public boolean matches(Container container, Level level) {
        return false;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return SpiritRecipes.TRANSMUTATION_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return SpiritRecipes.TRANSMUTATION.get();
    }

    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        List<Ingredient> list = new ArrayList<>(new ArrayList<>(inputs).stream().map(RitualComponent::getIngredients).flatMap(List::stream).toList());
        list.add(catalyst);
        return NonNullList.of(Ingredient.EMPTY, list.toArray(new Ingredient[0]));
    }
}
