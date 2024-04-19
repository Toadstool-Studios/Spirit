package earth.terrarium.spirit.api.souls.base;

import earth.terrarium.spirit.api.souls.Updatable;
import earth.terrarium.spirit.api.souls.base.SoulContainer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface SoulContainingItem<T extends SoulContainer> {
    @Nullable
    T getContainer(ItemStack stack);
}
