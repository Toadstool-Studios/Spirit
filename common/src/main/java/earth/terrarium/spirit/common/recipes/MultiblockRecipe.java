package earth.terrarium.spirit.common.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefullib.common.codecs.recipes.IngredientCodec;
import com.teamresourceful.resourcefullib.common.recipe.CodecRecipe;
import earth.terrarium.spirit.api.rituals.results.RitualResult;
import earth.terrarium.spirit.api.rituals.results.RitualResultManager;
import earth.terrarium.spirit.common.registry.SpiritRecipes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public record MultiblockRecipe(ResourceLocation id, short duration, boolean destroysStructure, SoulfireMultiblock multiblock, Ingredient catalyst, RitualResult<?> result) implements CodecRecipe<Container>, MagicalRecipe {
    public static Codec<MultiblockRecipe> codec(ResourceLocation id) {
        return RecordCodecBuilder.create(instance -> instance.group(
                RecordCodecBuilder.point(id),
                Codec.SHORT.fieldOf("duration").orElse((short) 60).forGetter(MultiblockRecipe::duration),
                Codec.BOOL.fieldOf("destroysStructure").orElse(false).forGetter(MultiblockRecipe::destroysStructure),
                SoulfireMultiblock.CODEC.fieldOf("multiblock").forGetter(MultiblockRecipe::multiblock),
                IngredientCodec.CODEC.fieldOf("catalyst").forGetter(MultiblockRecipe::catalyst),
                RitualResultManager.CODEC.fieldOf("result").forGetter(MultiblockRecipe::result)
        ).apply(instance, MultiblockRecipe::new));
    }

    @Override
    public boolean matches(Container container, Level level) {
        return false;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return SpiritRecipes.MULTIBLOCK_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return SpiritRecipes.MULTIBLOCK.get();
    }
}
