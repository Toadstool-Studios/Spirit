package earth.terrarium.spirit.common.block;

import earth.terrarium.spirit.common.blockentity.AttunementTableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class AttunementTableBlock extends BaseEntityBlock {
    public AttunementTableBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AttunementTableBlockEntity(pos, state);
    }
}
