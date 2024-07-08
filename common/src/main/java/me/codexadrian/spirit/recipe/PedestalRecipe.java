package me.codexadrian.spirit.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.codexadrian.spirit.Spirit;
import me.codexadrian.spirit.data.SyncedData;
import me.codexadrian.spirit.data.TagAndListSetCodec;
import me.codexadrian.spirit.registry.SpiritItems;
import me.codexadrian.spirit.registry.SpiritMisc;
import me.codexadrian.spirit.utils.CodecUtils;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.List;
import java.util.Optional;

public record PedestalRecipe(ResourceLocation id, HolderSet<EntityType<?>> entityInput, Optional<Ingredient> activationItem, boolean consumesActivator, List<Ingredient> ingredients,
                             EntityType<?> entityOutput, int duration,
                             Optional<CompoundTag> outputNbt) implements SyncedData {
    public static Codec<PedestalRecipe> codec(ResourceLocation id) {
        return RecordCodecBuilder.create(instance -> instance.group(
                RecordCodecBuilder.point(id),
                TagAndListSetCodec.of(Registry.ENTITY_TYPE).fieldOf("entityInput").forGetter(PedestalRecipe::entityInput),
                CodecUtils.INGREDIENT_CODEC.optionalFieldOf("activatorItem").forGetter(PedestalRecipe::activationItem),
                Codec.BOOL.fieldOf("consumesActivator").orElse(true).forGetter(PedestalRecipe::consumesActivator),
                CodecUtils.INGREDIENT_CODEC.listOf().fieldOf("itemInputs").forGetter(PedestalRecipe::ingredients),
                Registry.ENTITY_TYPE.byNameCodec().fieldOf("entityOutput").forGetter(PedestalRecipe::entityOutput),
                Codec.INT.fieldOf("duration").orElse(60).forGetter(PedestalRecipe::duration),
                CompoundTag.CODEC.optionalFieldOf("outputNbt").forGetter(PedestalRecipe::outputNbt)
        ).apply(instance, PedestalRecipe::new));
    }

    @Override
    public ResourceLocation getId() {
        return id();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SpiritMisc.SOUL_TRANSMUTATION_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return SpiritMisc.SOUL_TRANSMUTATION_RECIPE.get();
    }

    public static List<PedestalRecipe> getRecipesForEntity(EntityType<?> entity, ItemStack stack, RecipeManager manager) {
        return manager.getAllRecipesFor(SpiritMisc.SOUL_TRANSMUTATION_RECIPE.get()).stream().filter(recipe -> {
            boolean stackMatches;
            if(recipe.activationItem().isPresent()) {
                stackMatches = recipe.activationItem().get().test(stack);
            } else {
                stackMatches = true;
            }
            return recipe.entityInput().contains(entity.builtInRegistryHolder()) && stackMatches;
        }).toList();
    }

    public static Optional<PedestalRecipe> getEffect(String id, RecipeManager manager) {
        return (Optional<PedestalRecipe>) manager.byKey(ResourceLocation.tryParse(id));
    }
}
