package earth.terrarium.spirit.common.containers;

import earth.terrarium.botarium.util.Serializable;
import earth.terrarium.spirit.api.souls.InteractionMode;
import earth.terrarium.spirit.api.souls.Updatable;
import earth.terrarium.spirit.api.souls.base.SoulContainer;
import earth.terrarium.spirit.api.souls.stack.SoulStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class SoulCrystalContainer implements SoulContainer, Serializable {
    private EntityType<?> type = null;
    private final ItemStack stack;

    public SoulCrystalContainer(ItemStack stack) {
        this.stack = stack;
        deserialize(stack.getOrCreateTag());
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
        SoulStack extract = SoulStack.of(type, Mth.clamp(amount, 0, stack.getCount()));
        if (!simulate) stack.shrink(extract.getAmount());
        return extract;
    }

    @Override
    public int slotCapacity(int slot) {
        return 64;
    }

    @Override
    public int slotCount() {
        return 1;
    }

    @Override
    public SoulStack getStackInSlot(int slot) {
        return SoulStack.of(type, stack.getCount());
    }

    @Override
    public CompoundTag serialize(CompoundTag tag) {
        if (type != null) {
            tag.putString("Entity", EntityType.getKey(type).toString());
        } else {
            tag.putString("Entity", "");
        }
        return tag;
    }

    @Override
    public void deserialize(CompoundTag tag) {
        type = tag.getString("Entity").isEmpty() ? null : EntityType.byString(tag.getString("Entity")).orElse(null);
    }
}

