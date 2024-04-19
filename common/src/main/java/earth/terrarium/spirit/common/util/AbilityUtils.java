package earth.terrarium.spirit.common.util;

import earth.terrarium.botarium.common.fluid.FluidConstants;
import earth.terrarium.botarium.common.fluid.base.FluidContainer;
import earth.terrarium.botarium.common.fluid.base.FluidHolder;
import earth.terrarium.botarium.common.fluid.base.ItemFluidContainer;
import earth.terrarium.botarium.common.item.ItemStackHolder;
import earth.terrarium.spirit.api.abilities.armor.ArmorAbility;
import earth.terrarium.spirit.api.abilities.tool.ToolAbility;
import earth.terrarium.spirit.common.config.SpiritConfig;
import earth.terrarium.spirit.common.item.armor.SoulSteelArmor;
import earth.terrarium.spirit.common.item.tools.SoulSteelTool;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class AbilityUtils {
    //get the cooked result of an item
    public static ItemStack getCookedResult(Level level, ItemStack itemStack) {
        ItemStack result = itemStack.copy();
        Optional<SmeltingRecipe> first = level.getRecipeManager().getAllRecipesFor(RecipeType.SMELTING).stream().filter(recipe -> recipe.getIngredients().get(0).test(result)).findFirst();
        return first.map(smeltingRecipe -> smeltingRecipe.getResultItem(level.registryAccess()).copy()).orElse(itemStack);
    }

    public static void onArmorEquip(Player player, EquipmentSlot slot, ItemStack stack) {
        ArmorAbility ability = SoulSteelArmor.getAbility(stack);
        if (ability != null) {
            ability.onEquip(player, slot, stack);
        }
    }

    public static void onArmorUnequip(Player player, EquipmentSlot slot, ItemStack stack) {
        ArmorAbility ability = SoulSteelArmor.getAbility(stack);
        if (ability != null) {
            ability.onUnequip(player, slot, stack);
        }
    }

    public static boolean onEntityHit(LivingEntity victim, DamageSource source, float amount) {
        if (source.getEntity() instanceof Player player) {
            ItemStack stack = player.getMainHandItem();
            if (stack.getItem() instanceof SoulSteelTool tool) {
                ToolAbility ability = tool.getAbility(stack);
                if (ability != null) {
                    return ability.onHit(source, victim, amount);
                }
            }
        }
        return true;
    }

    public static boolean hasArmorAbility(Player player, ArmorAbility ability, @Nullable EquipmentSlot slot) {
        if (slot == null) {
            for (ItemStack stack : player.getArmorSlots()) {
                if (SoulSteelArmor.getAbility(stack) == ability) {
                    return true;
                }
            }
        } else {
            ItemStack stack = player.getItemBySlot(slot);
            return SoulSteelArmor.getAbility(stack) == ability;
        }
        return false;
    }

    public static boolean hasArmorAbility(Player player, ArmorAbility ability) {
        return hasArmorAbility(player, ability, null);
    }

    public static void refreshArmorItem(Player player, ItemStack newArmorItem) {
        if (newArmorItem.getItem() instanceof SoulSteelArmor armor) {
            EquipmentSlot slot = armor.getEquipmentSlot();
            player.setItemSlot(slot, newArmorItem);
        }
    }
}
