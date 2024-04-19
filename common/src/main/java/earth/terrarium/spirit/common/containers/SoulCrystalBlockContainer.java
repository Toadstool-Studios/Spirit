package earth.terrarium.spirit.common.containers;

import earth.terrarium.botarium.util.Serializable;
import earth.terrarium.spirit.api.souls.InteractionMode;
import earth.terrarium.spirit.api.souls.Updatable;
import earth.terrarium.spirit.api.souls.base.SoulContainer;
import earth.terrarium.spirit.api.souls.stack.SoulStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;
import java.util.function.Consumer;

public class SoulCrystalBlockContainer implements SoulContainer, Serializable {
    private EntityType<?> type = null;
    private final Consumer<SoulCrystalBlockContainer> updatable;
    private final BlockEntity blockEntity;

    public SoulCrystalBlockContainer(BlockEntity blockEntity) {
        updatable = (container) -> blockEntity.setChanged();
        this.blockEntity = blockEntity;
    }

    @Override
    public int insertIntoSlot(int slot, SoulStack soulStack, boolean simulate) {
        return 0;
    }

    @Override
    public int insert(SoulStack soulStack, boolean simulate) {
        return 0;
    }

    @Override
    public SoulStack extractFromSlot(int slot, int amount, boolean simulate) {
        return extract(amount, simulate);
    }

    @Override
    public SoulStack extract(int amount, boolean simulate) {
        SoulStack extract = SoulStack.of(type, 1);
        if (!simulate && blockEntity.getLevel() != null) {
            blockEntity.getLevel().destroyBlock(blockEntity.getBlockPos(), false);
        }
        return extract;
    }

    @Override
    public int slotCapacity(int slot) {
        return 1;
    }

    @Override
    public int slotCount() {
        return 1;
    }

    @Override
    public SoulStack getStackInSlot(int slot) {
        return SoulStack.of(type, 1);
    }

    @Override
    public CompoundTag serialize(CompoundTag tag) {
        if (type != null) tag.putString("Entity", EntityType.getKey(type).toString());
        return tag;
    }

    @Override
    public void deserialize(CompoundTag tag) {
        type = tag.getString("Entity").isEmpty() ? null : EntityType.byString(tag.getString("Entity")).orElse(null);
    }
}

