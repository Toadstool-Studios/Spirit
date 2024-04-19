package earth.terrarium.spirit.api.rituals.components.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefullib.common.codecs.recipes.IngredientCodec;
import earth.terrarium.spirit.Spirit;
import earth.terrarium.spirit.api.rituals.components.RitualComponent;
import earth.terrarium.spirit.api.rituals.components.RitualComponentSerializer;
import earth.terrarium.spirit.common.blockentity.PedestalBlockEntity;
import earth.terrarium.spirit.common.recipes.MagicalRecipe;
import earth.terrarium.spirit.compat.rei.ComponentUtils;
import earth.terrarium.spirit.compat.rei.categories.TransmutationRecipeCategory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record ItemComponent(Ingredient item) implements RitualComponent<ItemComponent> {

    public static final Serializer SERIALIZER = new Serializer();

    public static Codec<ItemComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            IngredientCodec.CODEC.fieldOf("ingredient").forGetter(ItemComponent::item)
    ).apply(instance, ItemComponent::new));

    @Override
    public boolean matches(MagicalRecipe recipe, Level level, BlockPos blockPos, BlockPos ritualPos) {
        if (!level.isClientSide() && level.getBlockEntity(blockPos) instanceof PedestalBlockEntity pedestal) {
            return item.test(pedestal.getItem(0));
        }
        return false;
    }

    @Override
    public void onRitualBegin(MagicalRecipe recipe, Level level, @Nullable BlockPos componentPos, BlockPos ritualPos) {
        if (!level.isClientSide() && componentPos != null && level.getBlockEntity(componentPos) instanceof PedestalBlockEntity pedestal) {
            pedestal.setProcessTime(ritualPos, recipe.duration());
        }
    }

    @Override
    public void onRitualAbort(MagicalRecipe recipe, Level level, @Nullable BlockPos componentPos, BlockPos ritualPos) {
        if (!level.isClientSide() && componentPos != null && level.getBlockEntity(componentPos) instanceof PedestalBlockEntity pedestal) {
            pedestal.resetProcessTime();
        }
    }

    @Override
    public void onRitualComplete(MagicalRecipe recipe, Level level, BlockPos componentPos, BlockPos ritualPos) {
        if (!level.isClientSide() && level.getBlockEntity(componentPos) instanceof PedestalBlockEntity pedestal) {
            ItemStack itemStack = pedestal.getItem(0);
            Item craftingRemainingItem = itemStack.getItem().getCraftingRemainingItem();
            if (craftingRemainingItem != null) {
                ItemStack remainingItem = new ItemStack(craftingRemainingItem);
                itemStack.shrink(1);
                if (itemStack.isEmpty()) {
                    pedestal.setItem(0, remainingItem);
                    level.sendBlockUpdated(componentPos, level.getBlockState(componentPos), level.getBlockState(componentPos), Block.UPDATE_ALL);
                } else {
                    Block.popResource(level, componentPos.above(), remainingItem);
                    pedestal.setItem(0, itemStack);
                    level.sendBlockUpdated(componentPos, level.getBlockState(componentPos), level.getBlockState(componentPos), Block.UPDATE_ALL);
                }
            } else {
                itemStack.shrink(1);
                pedestal.setItem(0, itemStack);
                level.sendBlockUpdated(componentPos, level.getBlockState(componentPos), level.getBlockState(componentPos), Block.UPDATE_ALL);
            }
            level.sendBlockUpdated(componentPos, level.getBlockState(componentPos), level.getBlockState(componentPos), Block.UPDATE_ALL);
        }
    }

    @Override
    public List<Ingredient> getIngredients() {
        return List.of(item);
    }

    @Override
    public ComponentUtils.ReiPlacer getREIPlacer() {
        return ComponentUtils.itemInputPlacer(this);
    }

    @Override
    public RitualComponentSerializer<ItemComponent> getSerializer() {
        return SERIALIZER;
    }

    public static class Serializer implements RitualComponentSerializer<ItemComponent> {
        public static final ResourceLocation ID = new ResourceLocation(Spirit.MODID, "item");
        public static Codec<ItemComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                IngredientCodec.CODEC.fieldOf("ingredient").forGetter(ItemComponent::item)
        ).apply(instance, ItemComponent::new));


        @Override
        public ResourceLocation id() {
            return ID;
        }

        @Override
        public Codec<ItemComponent> codec() {
            return CODEC;
        }
    }
}
