package earth.terrarium.spirit.common.blockentity;

import earth.terrarium.botarium.common.fluid.FluidConstants;
import earth.terrarium.botarium.common.fluid.base.BotariumFluidBlock;
import earth.terrarium.botarium.common.fluid.impl.SimpleFluidContainer;
import earth.terrarium.botarium.common.fluid.impl.WrappedBlockFluidContainer;
import earth.terrarium.botarium.common.fluid.utils.FluidParticleOptions;
import earth.terrarium.spirit.api.client.DripParticleOptions;
import earth.terrarium.spirit.common.registry.SpiritBlockEntities;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SoulBasinBlockEntity extends BlockEntity implements BotariumFluidBlock<WrappedBlockFluidContainer> {
    public static final int ANIMATION_TIME = 22;

    private WrappedBlockFluidContainer fluidContainer;
    public int age;
    @Nullable
    private BlockPos ritualPos;

    public SoulBasinBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(SpiritBlockEntities.SOUL_BASIN.get(), blockPos, blockState);
    }

    public void tick() {
        if (level != null && level instanceof ClientLevel clientLevel) {
            if (ritualPos != null) {
                this.age = (this.age + 1) % Integer.MAX_VALUE;
                Vec3 vector = getVector();
                if (age > ANIMATION_TIME) {
                    clientLevel.addParticle(new FluidParticleOptions(fluidContainer.getFirstFluid()), this.getBlockPos().getX() + 0.5, this.getBlockPos().getY() + 1.3, this.getBlockPos().getZ() + 0.5, vector.x, vector.y, vector.z);
                    clientLevel.addParticle(new DripParticleOptions(fluidContainer.getFirstFluid()), this.getBlockPos().getX() + 0.5, this.getBlockPos().getY() + 1.3, this.getBlockPos().getZ() + 0.5, 0, 0, 0);
                    //clientLevel.addParticle(new DripParticleOptions(fluidContainer.getFirstFluid()), this.getBlockPos().getX() + 0.25 + clientLevel.random.nextDouble() / 2, this.getBlockPos().getY() + 3, this.getBlockPos().getZ() + 0.25 + clientLevel.random.nextDouble() / 2, 0, 0, 0);
                }
            } else {
                this.age = 0;
            }
        }
    }

    //get speed vector from basin to ritual
    public Vec3 getVector() {
        if (ritualPos == null) return new Vec3(0, 0, 0);
        Vec3 basin = new Vec3(this.getBlockPos().getX(), this.getBlockPos().getY() + 1.3, this.getBlockPos().getZ());
        Vec3 ritual = new Vec3(ritualPos.getX(), ritualPos.getY() + 10, ritualPos.getZ());
        // make the arc very large
        return ritual.subtract(basin).normalize().scale(0.5);
    }

    public void setRitualPos(BlockPos ritualPos) {
        this.ritualPos = ritualPos;
        this.age = 0;
        this.setChanged();
        if (level != null) {
            this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
        }
    }

    public boolean hasRitual() {
        return ritualPos != null;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (ritualPos != null) {
            tag.put("RitualPos", NbtUtils.writeBlockPos(ritualPos));
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("RitualPos")) {
            ritualPos = NbtUtils.readBlockPos(tag.getCompound("RitualPos"));
        } else {
            ritualPos = null;
        }
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public WrappedBlockFluidContainer getFluidContainer() {
        if (fluidContainer == null) {
            fluidContainer = new WrappedBlockFluidContainer(this, new SimpleFluidContainer(FluidConstants.fromMillibuckets(4000), 1, (integer, fluidHolder) -> true));
        }
        return fluidContainer;
    }
}
