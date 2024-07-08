package me.codexadrian.spirit.compat.rei.categories;

import me.codexadrian.spirit.Spirit;
import me.codexadrian.spirit.compat.jei.ingredients.EntityIngredient;
import me.codexadrian.spirit.compat.rei.SpiritPlugin;
import me.codexadrian.spirit.compat.rei.displays.PedestalDisplay;
import me.codexadrian.spirit.registry.SpiritBlocks;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PedestalRecipeCategory implements DisplayCategory<PedestalDisplay> {
    public static final ResourceLocation GUI_BACKGROUND = new ResourceLocation(Spirit.MODID, "textures/gui/soul_transmutation.png");
    public static final ResourceLocation ID = new ResourceLocation(Spirit.MODID, "soul_transmutation");
    public static final CategoryIdentifier<PedestalDisplay> RECIPE = CategoryIdentifier.of(ID);
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

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(SpiritBlocks.SOUL_PEDESTAL.get().asItem().getDefaultInstance());
    }

    @Override
    public Component getTitle() {
        return Component.translatable("spirit.jei.soul_transmutation.title");
    }

    @Override
    public CategoryIdentifier<? extends PedestalDisplay> getCategoryIdentifier() {
        return RECIPE;
    }

    @Override
    public int getDisplayWidth(PedestalDisplay display) {
        return 150 + 8;
    }

    @Override
    public int getDisplayHeight() {
        return 100 + 8;
    }

    @Override
    public List<Widget> setupDisplay(PedestalDisplay display, Rectangle bounds) {
        var widgets = new ArrayList<Widget>();
        widgets.add(Widgets.createRecipeBase(bounds));
        var startX = bounds.getCenterX() - 150 / 2;
        var startY = bounds.getCenterY() - 100 / 2;
        widgets.add(Widgets.createTexturedWidget(GUI_BACKGROUND, startX, startY, 150, 100));
        var recipe = display.recipe();

        for (int i = 0; i < Math.min(recipe.ingredients().size(), 8); i++) {
            widgets.add(Widgets.createSlot(new Point(startX + slots.get(i)[0], startY + slots.get(i)[1])).markInput().entries(EntryIngredients.ofIngredient(recipe.ingredients().get(i))).disableBackground());
        }

        var nbt = new CompoundTag();
        nbt.putBoolean("Corrupted", true);
        var entityTypes = recipe.entityInput().stream().filter(Holder::isBound).map(Holder::value).map(type -> new EntityIngredient(type, 45F, Optional.of(nbt))).toList();
        if(recipe.activationItem().isPresent()) {
            widgets.add(Widgets.createSlot(new Point(startX + 93, startY + 21)).entries(EntryIngredients.ofIngredient(recipe.activationItem().get()).map(stack -> {
                if (recipe.consumesActivator()) {
                    stack.tooltip(Component.translatable("spirit.jei.soul_transmutation.consumes").withStyle(ChatFormatting.RED));
                }

                return stack;
            })));
        } else {
            widgets.add(Widgets.createTooltip(new Rectangle(startX + 93, startY + 21, 18, 18), Component.translatable("spirit.jei.soul_transmutation.empty_hand")));
        }
        widgets.add(Widgets.createSlot(new Rectangle(startX + 28 - 1, startY + 37 - 1, 26, 26)).markInput().disableBackground().entries(EntryIngredients.of(SpiritPlugin.ENTITY_INGREDIENT, entityTypes)));
        widgets.add(Widgets.createSlot(new Rectangle(startX + 124 - 1, startY + 37 - 1, 26, 26)).markOutput().disableBackground().entry(EntryStack.of(SpiritPlugin.ENTITY_INGREDIENT, new EntityIngredient(recipe.entityOutput(), -45F, Optional.of(nbt)))));

        return widgets;
    }
}
