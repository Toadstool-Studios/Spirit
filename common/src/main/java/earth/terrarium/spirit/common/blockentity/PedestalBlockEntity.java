package earth.terrarium.spirit.common.blockentity;

import earth.terrarium.spirit.common.registry.SpiritBlockEntities;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PedestalBlockEntity extends BlockEntity implements WorldlyContainer {
    public static final int ANIMATION_TIME = 22;
    NonNullList<ItemStack> inventory = NonNullList.withSize(1, ItemStack.EMPTY);
    public BlockPos ritualPos;
    public int processTime;
    public int maxProcessTime;

    public PedestalBlockEntity(BlockPos blockPos, BlockState state) {
        super(SpiritBlockEntities.PEDESTAL.get(), blockPos, state);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, BlockEntity blockEntity) {
        if (blockEntity instanceof PedestalBlockEntity soulPedestal) {
            if (soulPedestal.maxProcessTime > 0) {
                soulPedestal.processTime++;
                if (level instanceof ClientLevel clientLevel && soulPedestal.processTime > ANIMATION_TIME) {
                    Vec3 vector = soulPedestal.getVector();
                    clientLevel.addParticle(new ItemParticleOption(ParticleTypes.ITEM, soulPedestal.inventory.get(0)), blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5, vector.x, vector.y, vector.z);
                }
                if (soulPedestal.processTime == soulPedestal.maxProcessTime) {
                    soulPedestal.resetProcessTime();
                }
            }
        }
    }

    public Vec3 getVector() {
        if (ritualPos == null) return new Vec3(0, 0, 0);
        Vec3 basin = new Vec3(this.getBlockPos().getX(), this.getBlockPos().getY() + 1.3, this.getBlockPos().getZ());
        Vec3 ritual = new Vec3(ritualPos.getX(), ritualPos.getY() + 10, ritualPos.getZ());
        // make the arc very large
        return ritual.subtract(basin).normalize().scale(0.5);
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return inventory.get(0).isEmpty();
    }

    @Override
    public @NotNull ItemStack getItem(int i) {
        return inventory.get(i);
    }

    public void update() {
        this.setChanged();
        if(getLevel() != null) getLevel().sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL);
    }

    @Override
    public @NotNull ItemStack removeItem(int i, int j) {
        return ContainerHelper.removeItem(inventory, i, j);
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int i) {
        return ContainerHelper.takeItem(inventory, i);
    }

    @Override
    public void setItem(int i, @NotNull ItemStack itemStack) {
        inventory.set(i, itemStack);
    }

    @Override
    public void clearContent() {
        inventory.clear();
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public void load(@NotNull CompoundTag compoundTag) {
        super.load(compoundTag);
        this.inventory = NonNullList.withSize(1, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(compoundTag, inventory);
        processTime = compoundTag.getInt("ProcessTime");
        maxProcessTime = compoundTag.getInt("MaxProcessTime");
        ritualPos = compoundTag.contains("RitualPos") ? BlockPos.of(compoundTag.getLong("RitualPos")) : null;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag compoundTag) {
        super.saveAdditional(compoundTag);
        ContainerHelper.saveAllItems(compoundTag, inventory);
        compoundTag.putInt("ProcessTime", processTime);
        compoundTag.putInt("MaxProcessTime", maxProcessTime);
        if (ritualPos != null) {
            compoundTag.putLong("RitualPos", ritualPos.asLong());
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    @Override
    public boolean stillValid(Player player) {
        return worldPosition.distSqr(player.blockPosition()) <= 16;
    }

    @Override
    public int[] getSlotsForFace(@NotNull Direction direction) {
        return new int[]{0};
    }

    @Override
    public boolean canPlaceItemThroughFace(int i, @NotNull ItemStack itemStack, @Nullable Direction direction) {
        return true;
    }

    @Override
    public boolean canTakeItemThroughFace(int i, @NotNull ItemStack itemStack, @NotNull Direction direction) {
        return true;
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public void setProcessTime(BlockPos ritualPos, int processTime) {
        this.ritualPos = ritualPos;
        this.maxProcessTime = processTime;
        this.update();
    }

    public void resetProcessTime() {
        this.ritualPos = null;
        this.processTime = 0;
        this.maxProcessTime = 0;
        this.update();
    }
}
