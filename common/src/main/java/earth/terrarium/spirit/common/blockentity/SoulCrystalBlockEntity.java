package earth.terrarium.spirit.common.blockentity;

import earth.terrarium.spirit.api.utils.SoulfulCreature;
import earth.terrarium.spirit.api.souls.base.SoulContainingBlock;
import earth.terrarium.spirit.common.containers.SoulCrystalBlockContainer;
import earth.terrarium.spirit.common.registry.SpiritBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SoulCrystalBlockEntity extends BlockEntity implements SoulContainingBlock<SoulCrystalBlockContainer> {
    private final SoulCrystalBlockContainer soulContainer = new SoulCrystalBlockContainer(this);
    private Entity entity;
    public int age;

    public SoulCrystalBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(SpiritBlockEntities.SOUL_CRYSTAL.get(), blockPos, blockState);
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag) {
        super.saveAdditional(compoundTag);
        getContainer().serialize(compoundTag);
    }

    @Override
    public void load(CompoundTag compoundTag) {
        super.load(compoundTag);
        getContainer().deserialize(compoundTag);
    }

    public Entity getOrCreateEntity() {
        if ((this.entity == null || this.entity.getType() != this.getContainer().getStackInSlot(0).getEntity()) && this.hasLevel() && getContainer().getStackInSlot(0).getEntity() != null) {
            this.entity = this.getContainer().getStackInSlot(0).getEntity().create(getLevel());
            if (entity instanceof SoulfulCreature corrupted) corrupted.setIfSoulless(true);
        }
        return entity;
    }

    @Override
    public @NotNull SoulCrystalBlockContainer getContainer(Level level, BlockPos pos, @Nullable BlockState state, @Nullable BlockEntity entity, @Nullable Direction direction) {
        return soulContainer;
    }

    public SoulCrystalBlockContainer getContainer() {
        return soulContainer;
    }
}
