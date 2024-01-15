package earth.terrarium.spirit.api.souls.base;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public interface MobContainer extends SoulContainer {
    boolean insertMob(LivingEntity mob);

    @Nullable LivingEntity extractMob(Level level);
}
