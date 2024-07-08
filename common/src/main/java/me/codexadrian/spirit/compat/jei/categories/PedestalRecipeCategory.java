package me.codexadrian.spirit.compat.jei.categories;

import me.codexadrian.spirit.Spirit;
import me.codexadrian.spirit.compat.jei.SpiritPlugin;
import me.codexadrian.spirit.compat.jei.ingredients.BigEntityRenderer;
import me.codexadrian.spirit.compat.jei.ingredients.EntityIngredient;
import me.codexadrian.spirit.recipe.PedestalRecipe;
import me.codexadrian.spirit.registry.SpiritBlocks;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class PedestalRecipeCategory extends BaseCategory<PedestalRecipe> {
    public static final ResourceLocation GUI_BACKGROUND = new ResourceLocation(Spirit.MODID, "textures/gui/soul_transmutation.png");
    public static final ResourceLocation ID = new ResourceLocation(Spirit.MODID, "soul_transmutation");
    public static final RecipeType<PedestalRecipe> RECIPE = new RecipeType<>(ID, PedestalRecipe.class);
    private static final List<int[]> slots = List.of(
            new int[]{32, 11},
            new int[]{55, 18},
            new int[]{62, 41},
            new int[]{55, 64},
            new int[]{32, 71},
            new int[]{9, 64},
            new int[]{2, 41},
            new int[]{9, 18}
    );

    public PedestalRecipeCategory(IGuiHelper guiHelper) {
        super(guiHelper,
                RECIPE,
                Component.translatable("spirit.jei.soul_transmutation.title"),
                guiHelper.drawableBuilder(GUI_BACKGROUND, 0,0, 150, 100).build(),
                guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, SpiritBlocks.SOUL_PEDESTAL.get().asItem().getDefaultInstance()));
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, PedestalRecipe recipe, @NotNull IFocusGroup focuses) {
        for (int i = 0; i < Math.min(recipe.ingredients().size(), 8); i++) {
            builder.addSlot(RecipeIngredientRole.INPUT, slots.get(i)[0], slots.get(i)[1]).addIngredients(recipe.ingredients().get(i));
        }
        var nbt = new CompoundTag();
        nbt.putBoolean("Corrupted", true);
        var entityTypes = recipe.entityInput().stream().filter(Holder::isBound).map(Holder::value).toList();
        var entityIngredients = entityTypes.stream().map(type -> new EntityIngredient(type, 45F, Optional.of(nbt))).toList();
        var spawnEggs = entityTypes.stream().map(SpawnEggItem::byId).filter(Objects::nonNull).map(SpawnEggItem::getDefaultInstance).toList();
        if(recipe.activationItem().isPresent()) {
            builder.addSlot(RecipeIngredientRole.CATALYST, 93, 21).addIngredients(recipe.activationItem().get()).addTooltipCallback((recipeSlotView, tooltip) -> {
                if(recipe.consumesActivator()) tooltip.add(Component.translatable("spirit.jei.soul_transmutation.consumes").withStyle(ChatFormatting.RED));
            });
        }
        builder.addSlot(RecipeIngredientRole.INPUT, 28, 37).addIngredients(SpiritPlugin.ENTITY_INGREDIENT, entityIngredients).setCustomRenderer(SpiritPlugin.ENTITY_INGREDIENT, BigEntityRenderer.INSTANCE);
        builder.addInvisibleIngredients(RecipeIngredientRole.INPUT).addIngredientsUnsafe(spawnEggs);
        builder.addSlot(RecipeIngredientRole.OUTPUT, 124, 37).addIngredient(SpiritPlugin.ENTITY_INGREDIENT, new EntityIngredient(recipe.entityOutput(), -45F, recipe.outputNbt().isPresent() ? Optional.of(nbt.merge(recipe.outputNbt().get())) : Optional.of(nbt))).setCustomRenderer(SpiritPlugin.ENTITY_INGREDIENT, BigEntityRenderer.INSTANCE);
        builder.addInvisibleIngredients(RecipeIngredientRole.OUTPUT).addIngredients(Ingredient.of(SpawnEggItem.byId(recipe.entityOutput())));
    }

    @Override
    public List<Component> getTooltipStrings(PedestalRecipe recipe, @NotNull IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        List<Component> components = new ArrayList<>();
        if(recipe.activationItem().isEmpty() && mouseX > 91 && mouseX < 111 && mouseY > 19 && mouseY < 39) {
            components.add(Component.translatable("spirit.jei.soul_transmutation.empty_hand"));
        }
        return components;
    }
}
