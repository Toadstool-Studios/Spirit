package me.codexadrian.spirit.blocks.blockentity;

import me.codexadrian.spirit.Corrupted;
import me.codexadrian.spirit.registry.SpiritBlocks;
import me.codexadrian.spirit.utils.SoulUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SoulCageBlockEntity extends BlockEntity implements WorldlyContainer {

    public EntityType<?> type;
    private ItemStack soulCrystal = ItemStack.EMPTY;

    @Nullable
    public Entity entity;

    private final SoulCageSpawner enabledSpawner = new SoulCageSpawner(this);

    public SoulCageBlockEntity(BlockPos pos, BlockState state) {
        super(SpiritBlocks.SOUL_CAGE_ENTITY.get(), pos, state);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, SoulCageBlockEntity blockEntity) {
        if (blockEntity.hasLevel() && !blockEntity.isEmpty()) {
            blockEntity.enabledSpawner.tick();
        }
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public boolean triggerEvent(int i, int j) {
        return this.enabledSpawner.onEventTriggered(i) || super.triggerEvent(i, j);
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return soulCrystal.isEmpty();
    }

    @Override
    public ItemStack getItem(int i) {
        return i == 0 ? soulCrystal : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int i, int j) {
        var itemStack = removeItemNoUpdate(i);
        update(Block.UPDATE_ALL);
        return itemStack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int i) {
        if (i == 0) {
            ItemStack crystal = soulCrystal;
            soulCrystal = ItemStack.EMPTY;

            return crystal;
        }

        return ItemStack.EMPTY;
    }

    @Override
    public void setItem(int i, @NotNull ItemStack itemStack) {
        if (i == 0)
            soulCrystal = itemStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return worldPosition.distSqr(player.blockPosition()) <= 16;
    }

    @Override
    public void clearContent() {
        soulCrystal = ItemStack.EMPTY;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public void load(@NotNull CompoundTag compoundTag) {
        super.load(compoundTag);
        type = null;
        soulCrystal = ItemStack.of(compoundTag.getCompound("crystal"));
        setType();
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag compoundTag) {
        super.saveAdditional(compoundTag);
        compoundTag.put("crystal", soulCrystal.save(new CompoundTag()));
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag);
        return tag;
    }

    public void setType() {
        String soulCrystalType = SoulUtils.getSoulCrystalType(soulCrystal);
        if (soulCrystalType != null) {
            type = Registry.ENTITY_TYPE.get(new ResourceLocation(soulCrystalType));
        } else {
            type = null;
        }
    }

    public Entity getOrCreateEntity() {
        if(this.entity == null && this.getLevel() != null) {
            this.entity = this.type.create(getLevel());
            if(entity instanceof Corrupted corrupted) corrupted.setCorrupted();
        }
        return entity;
    }

    public SoulCageSpawner getSpawner() {
        return this.enabledSpawner;
    }

    public void update(int update) {
        this.setChanged();
        if(this.getLevel() != null) getLevel().sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), update);
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
