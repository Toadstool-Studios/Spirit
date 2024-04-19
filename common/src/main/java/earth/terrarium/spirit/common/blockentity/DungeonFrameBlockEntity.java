package earth.terrarium.spirit.common.blockentity;

import earth.terrarium.spirit.api.souls.base.SoulContainingBlock;
import earth.terrarium.spirit.api.souls.impl.BasicSoulContainer;
import earth.terrarium.spirit.common.registry.SpiritBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class DungeonFrameBlockEntity extends BlockEntity implements SoulContainingBlock<BasicSoulContainer> {
    BasicSoulContainer container = new BasicSoulContainer(4, 1, this);

    public DungeonFrameBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(SpiritBlockEntities.DUNGEON_FRAME.get(), blockPos, blockState);
    }

    @Override
    public @Nullable BasicSoulContainer getContainer(Level level, BlockPos pos, @Nullable BlockState state, @Nullable BlockEntity entity, @Nullable Direction direction) {
        return container;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        container.serialize(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        container.deserialize(tag);
    }
}
