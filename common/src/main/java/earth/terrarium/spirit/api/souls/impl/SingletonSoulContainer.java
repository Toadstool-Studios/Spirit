package earth.terrarium.spirit.api.souls.impl;

import earth.terrarium.botarium.util.Serializable;
import earth.terrarium.spirit.api.souls.Updatable;
import earth.terrarium.spirit.api.souls.base.SoulContainer;
import earth.terrarium.spirit.api.souls.stack.SoulStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.function.Consumer;

public class SingletonSoulContainer implements SoulContainer, Serializable {
    private final int slotCapacity;

    private final Consumer<SingletonSoulContainer> updatable;
    private SoulStack stack = SoulStack.EMPTY;

    public SingletonSoulContainer(int slotCapacity, Consumer<SingletonSoulContainer> updatable) {
        this.slotCapacity = slotCapacity;
        this.updatable = updatable;
    }

    public SingletonSoulContainer(int slotCapacity, ItemStack stack) {
        this(slotCapacity, (container) -> container.serialize(stack.getOrCreateTag()));
        deserialize(stack.getOrCreateTag());
    }

    public SingletonSoulContainer(int slotCapacity, BlockEntity entity) {
        this(slotCapacity, (container) -> entity.setChanged());
    }

    @Override
    public int insertIntoSlot(int slot, SoulStack soulStack, boolean simulate) {
        return insert(soulStack, simulate);
    }

    //return what was inserted
    @Override
    public int insert(SoulStack soulStack, boolean simulate) {
        if (stack.isEmpty()) {
            int insertAmount = Math.min(soulStack.getAmount(), slotCapacity);
            if (!simulate) {
                stack = soulStack.copyWithAmount(insertAmount);
                updatable.accept(this);
            }
            return insertAmount;
        } else if (stack.is(soulStack)) {
            int insertAmount = Math.min(soulStack.getAmount(), slotCapacity - stack.getAmount());
            if (!simulate) {
                stack.grow(insertAmount);
                updatable.accept(this);
            }
            return insertAmount;
        }
        return 0;
    }

    @Override
    public SoulStack extractFromSlot(int slot, int amount, boolean simulate) {
        return extract(amount, simulate);
    }

    @Override
    public SoulStack extract(int amount, boolean simulate) {
        if (stack.isEmpty()) {
            return SoulStack.EMPTY;
        } else {
            SoulStack toExtract = stack.copyWithAmount(Mth.clamp(amount, 0, stack.getAmount()));
            if (!simulate) {
                if (stack.getAmount() == toExtract.getAmount()) {
                    stack = SoulStack.EMPTY;
                } else {
                    stack.shrink(toExtract.getAmount());
                }
                updatable.accept(this);
            }
            return toExtract;
        }
    }

    @Override
    public int slotCapacity(int slot) {
        return slotCapacity;
    }

    @Override
    public int slotCount() {
        return 1;
    }

    @Override
    public SoulStack getStackInSlot(int slot) {
        return stack.copy();
    }

    public void deserialize(CompoundTag nbt) {
        this.stack = SoulStack.fromTag(nbt.getCompound("Soul"));
    }

    public CompoundTag serialize(CompoundTag nbt) {
        nbt.put("Soul", stack.toTag(new CompoundTag()));
        return nbt;
    }
}
