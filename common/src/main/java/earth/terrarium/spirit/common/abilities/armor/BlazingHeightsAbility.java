package earth.terrarium.spirit.common.abilities.armor;

import earth.terrarium.botarium.common.fluid.FluidConstants;
import earth.terrarium.botarium.common.fluid.base.FluidContainer;
import earth.terrarium.botarium.common.fluid.base.FluidHolder;
import earth.terrarium.botarium.common.fluid.base.ItemFluidContainer;
import earth.terrarium.botarium.common.item.ItemStackHolder;
import earth.terrarium.spirit.api.abilities.ColorPalette;
import earth.terrarium.spirit.api.abilities.armor.ArmorAbility;
import earth.terrarium.spirit.common.config.SpiritConfig;
import earth.terrarium.spirit.common.util.AbilityUtils;
import earth.terrarium.spirit.common.util.KeybindUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;

public class BlazingHeightsAbility implements ArmorAbility {
    @Override
    public void onArmorTick(ItemStack stack, Level level, Player player) {
        ItemStackHolder holder = new ItemStackHolder(stack);
        if (KeybindUtils.jumpKeyDown(player) && drainBlazeLiquid(player, holder, true)) {
            drainBlazeLiquid(player, holder, false);
            player.fallDistance /= 2;
            double speed = 0.5;
            player.setDeltaMovement(player.getDeltaMovement().add(0.0, speed, 0.0));
            if (player.getDeltaMovement().y() > speed) {
                player.setDeltaMovement(player.getDeltaMovement().x(), speed, player.getDeltaMovement().z());
            }
        }
    }

    public static boolean drainBlazeLiquid(Player player, ItemStackHolder stack, boolean simulate) {
        ItemFluidContainer container = FluidContainer.of(stack);
        if (container != null && container.getFirstFluid().is(Fluids.LAVA) && container.getFirstFluid().getFluidAmount() > SpiritConfig.blazeDrainAmount) {
            long amount = FluidConstants.fromMillibuckets(SpiritConfig.blazeDrainAmount);
            FluidHolder extracted = container.extractFluid(FluidHolder.of(Fluids.LAVA, amount), simulate);
            if (!simulate && stack.isDirty()) {
                AbilityUtils.refreshArmorItem(player, stack.getStack());
            }
            return extracted.getFluidAmount() == amount;
        }
        return false;
    }

    @Override
    public ColorPalette getColor() {
        return new ColorPalette(0xFFFFC700, 0xFFE77000, 0xFFFFF04E);
    }
}
