package earth.terrarium.spirit.api.souls.base;

import earth.terrarium.spirit.api.souls.Updatable;
import earth.terrarium.spirit.api.souls.base.SoulContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public interface SoulContainingBlock<T extends SoulContainer> {
    @Nullable
    T getContainer(Level level, BlockPos pos, @Nullable BlockState state, @Nullable BlockEntity entity, @Nullable Direction direction);
}
