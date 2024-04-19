package earth.terrarium.spirit.common.registry;

import com.teamresourceful.resourcefullib.common.recipe.CodecRecipeSerializer;
import com.teamresourceful.resourcefullib.common.recipe.CodecRecipeType;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry;
import earth.terrarium.spirit.Spirit;
import earth.terrarium.spirit.common.recipes.DungeonRecipe;
import earth.terrarium.spirit.common.recipes.MultiblockRecipe;
import earth.terrarium.spirit.common.recipes.TransmutationRecipe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public class SpiritRecipes {
    public static final ResourcefulRegistry<RecipeSerializer<?>> RECIPE_SERIALIZERS = ResourcefulRegistries.create(BuiltInRegistries.RECIPE_SERIALIZER, Spirit.MODID);
    public static final ResourcefulRegistry<RecipeType<?>> RECIPE_TYPES = ResourcefulRegistries.create(BuiltInRegistries.RECIPE_TYPE, Spirit.MODID);

    //Transmutation
    public static final RegistryEntry<RecipeType<TransmutationRecipe>> TRANSMUTATION = RECIPE_TYPES.register("transmutation", () -> CodecRecipeType.of("transmutation"));
    public static final RegistryEntry<RecipeSerializer<TransmutationRecipe>> TRANSMUTATION_SERIALIZER = RECIPE_SERIALIZERS.register("transmutation", () -> new CodecRecipeSerializer<>(TRANSMUTATION.get(), TransmutationRecipe::codec));

    //Multiblock
    public static final RegistryEntry<RecipeType<MultiblockRecipe>> MULTIBLOCK = RECIPE_TYPES.register("multiblock", () -> CodecRecipeType.of("multiblock"));
    public static final RegistryEntry<RecipeSerializer<MultiblockRecipe>> MULTIBLOCK_SERIALIZER = RECIPE_SERIALIZERS.register("multiblock", () -> new CodecRecipeSerializer<>(MULTIBLOCK.get(), MultiblockRecipe::codec));

    //Misc
    public static final RegistryEntry<RecipeType<DungeonRecipe>> DUNGEON = RECIPE_TYPES.register("dungeon", () -> CodecRecipeType.of("dungeon"));
    public static final RegistryEntry<RecipeSerializer<DungeonRecipe>> DUNGEON_SERIALIZER = RECIPE_SERIALIZERS.register("dungeon", () -> new CodecRecipeSerializer<>(DUNGEON.get(), DungeonRecipe::codec));
}
