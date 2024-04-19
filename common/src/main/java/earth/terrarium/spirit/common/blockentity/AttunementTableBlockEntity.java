package earth.terrarium.spirit.common.blockentity;

import earth.terrarium.spirit.common.registry.SpiritBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class AttunementTableBlockEntity extends BlockEntity {
    public AttunementTableBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(SpiritBlockEntities.ATTUNEMENT_TABLE.get(), blockPos, blockState);
    }
}
