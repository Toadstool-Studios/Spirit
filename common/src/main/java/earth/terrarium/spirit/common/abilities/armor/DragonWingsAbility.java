package earth.terrarium.spirit.common.abilities.armor;

import earth.terrarium.spirit.api.abilities.ColorPalette;
import earth.terrarium.spirit.api.abilities.armor.ArmorAbility;
import earth.terrarium.spirit.common.registry.SpiritAbilities;
import earth.terrarium.spirit.common.util.AbilityUtils;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class DragonWingsAbility implements ArmorAbility {

    @Override
    public void onUnequip(Player player, EquipmentSlot slot, ItemStack stack) {
        player.getAbilities().mayfly = false;
        player.getAbilities().flying = false;
        player.onUpdateAbilities();
    }

    @Override
    public void onArmorTick(ItemStack stack, Level level, Player player) {
        if(!player.getAbilities().mayfly) {
            player.getAbilities().mayfly = true;
            player.onUpdateAbilities();
        }
    }

    @Override
    public ColorPalette getColor() {
        return new ColorPalette(0xFFba03fc, 0xFF57228c, 0xFFf58aff);
    }
}
