package earth.terrarium.spirit.api.souls;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public interface Atunable {

    boolean meetsRequirements(Level level, BlockPos centerPos, ItemStack atunnableItem);

    void clearAttunement(ItemStack stack);

    boolean canAttune(ItemStack stack, EntityType<?> type, boolean critical);

    boolean isAttunedFor(ItemStack stack, EntityType<?> type);

    boolean isCritical(ItemStack stack);
}
