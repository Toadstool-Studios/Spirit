package earth.terrarium.spirit.api.rituals.components.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import earth.terrarium.botarium.common.fluid.FluidConstants;
import earth.terrarium.botarium.common.fluid.base.FluidHolder;
import earth.terrarium.botarium.common.fluid.utils.FluidIngredient;
import earth.terrarium.botarium.common.fluid.utils.QuantifiedFluidIngredient;
import earth.terrarium.spirit.Spirit;
import earth.terrarium.spirit.api.rituals.components.RitualComponent;
import earth.terrarium.spirit.api.rituals.components.RitualComponentSerializer;
import earth.terrarium.spirit.common.blockentity.SoulBasinBlockEntity;
import earth.terrarium.spirit.common.recipes.MagicalRecipe;
import earth.terrarium.spirit.compat.rei.ComponentUtils;
import earth.terrarium.spirit.compat.rei.categories.TransmutationRecipeCategory;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record BasinComponent(QuantifiedFluidIngredient fluidIngredient) implements RitualComponent<BasinComponent> {

    public static final Serializer SERIALIZER = new Serializer();

    @Override
    public boolean matches(MagicalRecipe recipe, Level level, BlockPos blockPos, BlockPos ritualPos) {
        if (level.getBlockEntity(blockPos) instanceof SoulBasinBlockEntity blockEntity) {
            for (FluidHolder fluid : blockEntity.getFluidContainer().getFluids()) {
                if (fluidIngredient.test(fluid)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onRitualBegin(MagicalRecipe recipe, Level level, @Nullable BlockPos componentPos, BlockPos ritualPos) {
        if (level.getBlockEntity(componentPos) instanceof SoulBasinBlockEntity blockEntity) {
            blockEntity.setRitualPos(ritualPos);
        }
    }

    @Override
    public void onRitualAbort(MagicalRecipe recipe, Level level, @Nullable BlockPos componentPos, BlockPos ritualPos) {
        if (level.getBlockEntity(componentPos) instanceof SoulBasinBlockEntity blockEntity) {
            blockEntity.setRitualPos(null);
        }
    }

    @Override
    public void onRitualComplete(MagicalRecipe recipe, Level level, BlockPos componentPos, BlockPos ritualPos) {
        if (level.getBlockEntity(componentPos) instanceof SoulBasinBlockEntity blockEntity) {
            for (FluidHolder fluid : blockEntity.getFluidContainer().getFluids()) {
                if (fluidIngredient.test(fluid)) {
                    FluidHolder toExtract = fluid.copyWithAmount(fluidIngredient.getFluidAmount());
                    blockEntity.getFluidContainer().internalExtract(toExtract, false);
                    blockEntity.setRitualPos(null);
                    break;
                }
            }
        }
    }

    @Override
    public List<Ingredient> getIngredients() {
        return fluidIngredient.getFluids().stream().map(FluidHolder::getFluid).map(Fluid::getBucket).map(Ingredient::of).toList();
    }

    @Override
    public ComponentUtils.ReiPlacer getREIPlacer() {
        return ComponentUtils.fluidPlacer(this);
    }

    @Override
    public RitualComponentSerializer<BasinComponent> getSerializer() {
        return SERIALIZER;
    }

    public static class Serializer implements RitualComponentSerializer<BasinComponent> {
        public static final ResourceLocation ID = new ResourceLocation(Spirit.MODID, "fluid");
        public static final Codec<BasinComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                FluidIngredient.CODEC.fieldOf("ingredient").forGetter(basinComponent -> basinComponent.fluidIngredient),
                Codec.LONG.fieldOf("millibuckets").orElse(1000L).forGetter(basinComponent -> FluidConstants.toMillibuckets(basinComponent.fluidIngredient.getFluidAmount()))
        ).apply(instance, (fluidIngredient1, aLong) -> new BasinComponent(new QuantifiedFluidIngredient(fluidIngredient1, FluidConstants.fromMillibuckets(aLong)))));


        @Override
        public ResourceLocation id() {
            return ID;
        }

        @Override
        public Codec<BasinComponent> codec() {
            return CODEC;
        }
    }
}
