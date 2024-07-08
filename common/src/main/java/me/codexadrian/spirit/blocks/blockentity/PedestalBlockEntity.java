package me.codexadrian.spirit.blocks.blockentity;

import me.codexadrian.spirit.registry.SpiritBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PedestalBlockEntity extends BlockEntity implements WorldlyContainer {

    ItemStack item = ItemStack.EMPTY;
    public int age;

    public PedestalBlockEntity(BlockPos $$1, BlockState $$2) {
        super(SpiritBlocks.PEDESTAL_ENTITY.get(), $$1, $$2);
    }

    public static void tick(Level level1, BlockPos blockPos, BlockState blockState1, BlockEntity blockEntity) {
        if(blockEntity instanceof PedestalBlockEntity soulPedestal) {
            soulPedestal.age = (soulPedestal.age + 1) % Integer.MAX_VALUE;
        }
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return item.isEmpty();
    }

    @Override
    public ItemStack getItem(int i) {
        return i == 0 ? item : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int i, int j) {
        var item = removeItemNoUpdate(i);
        update(j);
        return item;
    }

    public void update(int j) {
        this.setChanged();
        if(getLevel() != null) getLevel().sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), j);
    }

    @Override
    public ItemStack removeItemNoUpdate(int i) {
        if (i == 0) {
            ItemStack crystal = item;
            item = ItemStack.EMPTY;
            return crystal;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void setItem(int i, @NotNull ItemStack itemStack) {
        if (i == 0) {
            item = itemStack;
            this.setChanged();
        }
    }

    @Override
    public void clearContent() {
        item = ItemStack.EMPTY;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public void load(@NotNull CompoundTag compoundTag) {
        super.load(compoundTag);
        if (compoundTag.contains("item")) {
            item = ItemStack.of(compoundTag.getCompound("item"));
        } else {
            item = ItemStack.EMPTY;
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag compoundTag) {
        super.saveAdditional(compoundTag);
        if (!item.isEmpty()) {
            compoundTag.put("item", item.save(new CompoundTag()));
        } else {
            compoundTag.remove("item");
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    public boolean stillValid(Player player) {
        return worldPosition.distSqr(player.blockPosition()) <= 16;
    }

    @Override
    public int[] getSlotsForFace(@NotNull Direction direction) {
        return new int[0];
    }

    @Override
    public boolean canPlaceItemThroughFace(int i, @NotNull ItemStack itemStack, @Nullable Direction direction) {
        return false;
    }

    @Override
    public boolean canTakeItemThroughFace(int i, @NotNull ItemStack itemStack, @NotNull Direction direction) {
        return false;
    }
}
