package earth.terrarium.spirit.common.registry;

import earth.terrarium.botarium.common.fluid.FluidApi;
import earth.terrarium.spirit.api.abilities.armor.ArmorAbility;
import earth.terrarium.spirit.api.abilities.armor.ArmorAbilityManager;
import earth.terrarium.spirit.common.item.armor.SoulSteelArmor;
import earth.terrarium.spirit.common.util.AbilityUtils;

public class SpiritContainers {
    public static void init() {
        FluidApi.registerFluidItem(SpiritItems.SOUL_STEEL_HELMET, stack -> {
            ArmorAbility ability = SoulSteelArmor.getAbility(stack);
            if (ability != null) {
                return ability.getFluidContainer(stack);
            }
            return null;
        });
    }
}
