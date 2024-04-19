package earth.terrarium.spirit.common.item.trinkets;

import earth.terrarium.spirit.api.souls.Atunable;
import earth.terrarium.spirit.common.config.SpiritConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class PetrificationCharm extends Item implements Atunable {
    public static final String ATTUNEMENT_TAG = "Attunement";
    public static final String CRITICAL_TAG = "Critical";
    public PetrificationCharm(Properties properties) {
        super(properties);
    }

    @Override
    public boolean meetsRequirements(Level level, BlockPos centerPos, ItemStack atunnableItem) {
        return false;
    }

    @Override
    public void clearAttunement(ItemStack stack) {
        stack.getOrCreateTag().remove(ATTUNEMENT_TAG);
        stack.getOrCreateTag().remove(CRITICAL_TAG);
    }

    @Override
    public boolean canAttune(ItemStack stack, EntityType<?> type, boolean critical) {
        return stack.getOrCreateTag().isEmpty();
    }

    @Override
    public boolean isAttunedFor(ItemStack stack, EntityType<?> type) {
        if (stack.getOrCreateTag().contains(ATTUNEMENT_TAG)) {
            return stack.getOrCreateTag().getString(ATTUNEMENT_TAG).equals(EntityType.getKey(type).toString());
        } else {
            return false;
        }
    }

    @Override
    public boolean isCritical(ItemStack stack) {
        return stack.getOrCreateTag().getBoolean(CRITICAL_TAG);
    }

    public static int getAttunementBonus(ItemStack stack, EntityType<?> type) {
        int bonus = SpiritConfig.petrificationCharmDropChance;
        if (stack.getItem() instanceof PetrificationCharm charm) {
            if (charm.isAttunedFor(stack, type)) {
                bonus += SpiritConfig.attunementBonus;
                if (charm.isCritical(stack)) {
                    bonus += SpiritConfig.criticalBonus;
                }
            }
        }
        return bonus;
    }
}
