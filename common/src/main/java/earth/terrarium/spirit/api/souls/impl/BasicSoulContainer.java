package earth.terrarium.spirit.api.souls.impl;

import earth.terrarium.botarium.util.Serializable;
import earth.terrarium.spirit.api.souls.Updatable;
import earth.terrarium.spirit.api.souls.base.SoulContainer;
import earth.terrarium.spirit.api.souls.stack.SoulStack;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class BasicSoulContainer implements SoulContainer, Serializable {
    private static final String NBT_KEY = "Souls";

    private final NonNullList<SoulStack> stacks;
    private final int slotCapacity;

    private Updatable updatable;

    public BasicSoulContainer(int slotCount, int slotCapacity, Updatable updatable) {
        this.stacks = NonNullList.withSize(slotCount, SoulStack.EMPTY);
        this.slotCapacity = slotCapacity;
        this.updatable = updatable;
    }

    public BasicSoulContainer(int slotCount, int slotCapacity, ItemStack stack) {
        this(slotCount, slotCapacity, () -> {});
        deserialize(stack.getOrCreateTag());
        this.updatable = () -> this.serialize(stack.getOrCreateTag());
    }

    public BasicSoulContainer(int slotCount, int slotCapacity, BlockEntity entity) {
        this(slotCount, slotCapacity, entity::setChanged);
    }

    //return what was inserted
    @Override
    public int insertIntoSlot(int slot, SoulStack soulStack, boolean simulate) {
        if (slot < stacks.size()) {
            SoulStack stack = stacks.get(slot);
            if (stack.isEmpty()) {
                int insertAmount = Math.min(soulStack.getAmount(), slotCapacity);
                if (!simulate) {
                    stacks.set(slot, soulStack.copyWithAmount(insertAmount));
                    updatable.update();
                }
                return insertAmount;
            } else if (stack.is(soulStack)) {
                int insertAmount = Math.min(soulStack.getAmount(), slotCapacity - stack.getAmount());
                if (!simulate) {
                    stack.grow(insertAmount);
                    updatable.update();
                }
                return insertAmount;
            }
        }
        return 0;
    }

    @Override
    public int insert(SoulStack soulStack, boolean simulate) {
        SoulStack remaining = soulStack.copy();
        for (int slot = 0; slot < slotCount(); slot++) {
            remaining.shrink(insertIntoSlot(slot, remaining, simulate));
            if (remaining.isEmpty()) {
                break;
            }
        }
        return soulStack.getAmount() - remaining.getAmount();
    }

    @Override
    public SoulStack extractFromSlot(int slot, int amount, boolean simulate) {
        if (slot < stacks.size()) {
            SoulStack stack = stacks.get(slot);
            if (!stack.isEmpty()) {
                int extractedAmount = Mth.clamp(amount, 0, stack.getAmount());
                if (!simulate) {
                    stack.shrink(extractedAmount);
                    updatable.update();
                }
                return stack.copyWithAmount(extractedAmount);
            }
        }
        return SoulStack.EMPTY;
    }

    @Override
    public SoulStack extract(int amount, boolean simulate) {
        SoulStack extracted = SoulStack.EMPTY;
        for (int slot = 0; slot < slotCount(); slot++) {
            extracted = extractFromSlot(slot, amount, simulate);
            if (!extracted.isEmpty()) {
                break;
            }
        }
        return extracted;
    }

    @Override
    public int slotCapacity(int slot) {
        return slotCapacity;
    }

    @Override
    public int slotCount() {
        return stacks.size();
    }

    @Override
    public SoulStack getStackInSlot(int slot) {
        return stacks.get(slot).copy();
    }

    @Override
    public void deserialize(CompoundTag tag) {
        stacks.clear();
        ListTag listTag = tag.getList(NBT_KEY, 10);

        for(int i = 0; i < listTag.size(); ++i) {
            CompoundTag compoundTag = listTag.getCompound(i);
            int j = compoundTag.getByte("Slot") & 255;
            if (j >= 0 && j < stacks.size()) {
                stacks.set(j, SoulStack.fromTag(compoundTag));
            }
        }
    }

    @Override
    public CompoundTag serialize(CompoundTag tag) {
        ListTag listTag = new ListTag();

        for(int i = 0; i < stacks.size(); ++i) {
            SoulStack itemStack = stacks.get(i);
            if (!itemStack.isEmpty()) {
                CompoundTag compoundTag = new CompoundTag();
                compoundTag.putByte("Slot", (byte)i);
                itemStack.toTag(compoundTag);
                listTag.add(compoundTag);
            }
        }

        tag.put(NBT_KEY, listTag);

        return tag;
    }

    public NonNullList<SoulStack> getStacks() {
        return stacks;
    }
}
