package earth.terrarium.spirit.api.storage;

import earth.terrarium.spirit.api.souls.Tier;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface Tierable {
    Tier getTier(ItemStack stack);
    Tier getNextTier(ItemStack stack);
}