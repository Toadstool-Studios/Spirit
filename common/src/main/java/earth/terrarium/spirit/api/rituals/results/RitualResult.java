package earth.terrarium.spirit.api.rituals.results;

import earth.terrarium.spirit.compat.rei.ComponentUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface RitualResult<T extends RitualResult<T>> {
    ItemStack getItemRepresentation();
    void onRitualComplete(Level level, BlockPos blockPos, ItemStack catalyst);
    ComponentUtils.ReiPlacer getREIPlacer();
    RitualResultSerializer<T> getSerializer();
}
