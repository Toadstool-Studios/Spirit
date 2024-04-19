package earth.terrarium.spirit.common.entity;

import com.mojang.datafixers.util.Either;
import earth.terrarium.spirit.api.rituals.results.RitualResult;
import earth.terrarium.spirit.api.rituals.results.impl.EntityResult;
import earth.terrarium.spirit.api.utils.RitualManager;
import earth.terrarium.spirit.common.recipes.MultiblockRecipe;
import earth.terrarium.spirit.common.recipes.TransmutationRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SoulReceptacle extends Entity {
    private static final EntityDataAccessor<Integer> PROCESS_TIME = SynchedEntityData.defineId(SoulReceptacle.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<String> MULTIBLOCK_RECIPE = SynchedEntityData.defineId(SoulReceptacle.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> TRANSMUTATION_RECIPE = SynchedEntityData.defineId(SoulReceptacle.class, EntityDataSerializers.STRING);

    BlockPos recipeOrigin = BlockPos.ZERO;
    ItemStack catalyst = ItemStack.EMPTY;
    Entity entityResult;
    RitualManager ritualManager = null;

    public SoulReceptacle(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(PROCESS_TIME, 0);
        this.entityData.define(MULTIBLOCK_RECIPE, "");
        this.entityData.define(TRANSMUTATION_RECIPE, "");
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        setProcessTime(compoundTag.getInt("ProcessTime"));
        var origin = NbtUtils.readBlockPos(compoundTag.getCompound("RecipeOrigin"));
        var cat = ItemStack.of(compoundTag.getCompound("Catalyst"));
        if (compoundTag.contains("Multiblock")) {
            ResourceLocation recipeKey = ResourceLocation.tryParse(compoundTag.getString("Multiblock"));
            if (recipeKey != null) {
                var multiblock = level().getRecipeManager().byKey(recipeKey).orElse(null);
                if (multiblock instanceof MultiblockRecipe multiblockRecipe) {
                    setResult(cat, multiblockRecipe, origin);
                }
            }
        } else if (compoundTag.contains("Transmutation")) {
            ResourceLocation recipeKey = ResourceLocation.tryParse(compoundTag.getString("Transmutation"));
            if (recipeKey != null) {
                var transmutation = level().getRecipeManager().byKey(recipeKey).orElse(null);
                if (transmutation instanceof TransmutationRecipe transmutationRecipe) {
                    setResult(cat, transmutationRecipe, origin);
                }
            }
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        compoundTag.putInt("ProcessTime", getProcessTime());
        compoundTag.put("RecipeOrigin", NbtUtils.writeBlockPos(recipeOrigin));
        compoundTag.put("Catalyst", catalyst.save(new CompoundTag()));
        Either<MultiblockRecipe, TransmutationRecipe> recipe = getRecipe();
        if (recipe != null) {
            if (recipe.left().isPresent()) {
                compoundTag.putString("Multiblock", recipe.left().get().getId().toString());
            } else if (recipe.right().isPresent()) {
                compoundTag.putString("Transmutation", recipe.right().get().getId().toString());
            }
        }
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }

    public void setResult(ItemStack catalyst, MultiblockRecipe recipe, BlockPos origin) {
        entityData.set(MULTIBLOCK_RECIPE, recipe.getId().toString());
        this.recipeOrigin = origin.immutable();
        this.catalyst = catalyst.copy();
        setProcessTime(0);
    }

    public void setResult(ItemStack catalyst, TransmutationRecipe recipe, BlockPos origin) {
        entityData.set(TRANSMUTATION_RECIPE, recipe.getId().toString());
        this.recipeOrigin = origin.immutable();
        this.catalyst = catalyst.copy();
        setProcessTime(0);
    }

    public void setProcessTime(int processTime) {
        entityData.set(PROCESS_TIME, processTime);
    }

    public int getProcessTime() {
        return entityData.get(PROCESS_TIME);
    }

    public Either<MultiblockRecipe, TransmutationRecipe> getRecipe() {
        if (!entityData.get(MULTIBLOCK_RECIPE).isEmpty()) {
            ResourceLocation recipeKey = ResourceLocation.tryParse(entityData.get(MULTIBLOCK_RECIPE));
            if (recipeKey != null) {
                var multiblock = level().getRecipeManager().byKey(recipeKey).orElse(null);
                if (multiblock instanceof MultiblockRecipe multiblockRecipe) {
                    return Either.left(multiblockRecipe);
                }
            }
        } else if (!entityData.get(TRANSMUTATION_RECIPE).isEmpty()) {
            ResourceLocation recipeKey = ResourceLocation.tryParse(entityData.get(TRANSMUTATION_RECIPE));
            if (recipeKey != null) {
                var transmutation = level().getRecipeManager().byKey(recipeKey).orElse(null);
                if (transmutation instanceof TransmutationRecipe transmutationRecipe) {
                    return Either.right(transmutationRecipe);
                }
            }
        }
        return null;
    }

    public void incrementProcessTime() {
        setProcessTime(getProcessTime() + 1);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level() instanceof ServerLevel serverLevel) {
            Either<MultiblockRecipe, TransmutationRecipe> recipe = getRecipe();
            if (recipe != null) {
                if (recipe.left().isPresent()) {
                    MultiblockRecipe multiblockRecipe = recipe.left().get();
                    if (multiblockRecipe.multiblock().validateMultiblock(recipeOrigin, serverLevel, false)) {
                        if (getProcessTime() < multiblockRecipe.duration()) {
                            incrementProcessTime();
                            if (getProcessTime() % 10 == 0) {
                                level().addParticle(ParticleTypes.SOUL_FIRE_FLAME, getX(), getY() + 0.5, getZ(), 0, 0, 0);
                            }
                        } else {
                            multiblockRecipe.result().onRitualComplete(level(), blockPosition(), catalyst);
                            level().addParticle(ParticleTypes.SOUL, getX(), getY() + 0.5, getZ(), 0, 0, 0);
                            discard();
                        }
                    } else {
                        Block.popResource(level(), blockPosition(), catalyst);
                        discard();
                    }
                }
                else if (recipe.right().isPresent()) {
                    TransmutationRecipe transmutationRecipe = recipe.right().get();
                    if (ritualManager == null) {
                        ritualManager = RitualManager.of(serverLevel, recipeOrigin, transmutationRecipe);

                        if (ritualManager == null) {
                            Block.popResource(level(), blockPosition(), catalyst);
                            discard();
                        } else {
                            ritualManager.beginRitual(serverLevel);
                        }
                    }

                    if (getProcessTime() < transmutationRecipe.duration()) {
                        incrementProcessTime();
                        if (getProcessTime() % 10 == 0) {
                            boolean ritualMatches = ritualManager.validateComponents(serverLevel);

                            if (!ritualMatches) {
                                ritualManager.abortRitual(serverLevel);
                                Block.popResource(level(), blockPosition(), catalyst);
                                discard();
                            }
                        }
                    } else {
                        if (ritualManager.validateComponents(serverLevel)) {
                            ritualManager.completeRitual(serverLevel);
                            transmutationRecipe.result().onRitualComplete(level(), blockPosition(), catalyst);
                            serverLevel.sendParticles(ParticleTypes.END_ROD, getX(), getY() + 1, getZ(), 10, 0.3, 0.3, 0.3, 0.1);
                            discard();
                        } else {
                            ritualManager.abortRitual(serverLevel);
                            Block.popResource(level(), blockPosition(), catalyst);
                            discard();
                        }
                    }
                }
            } else {
                Block.popResource(level(), recipeOrigin, catalyst);
                discard();
            }
        }
    }

    public int getRecipeDuration() {
        var recipe = getRecipe();
        if (recipe != null) {
            if (recipe.left().isPresent()) {
                return recipe.left().get().duration();
            } else if (recipe.right().isPresent()) {
                return recipe.right().get().duration();
            }
        }
        return 0;
    }

    @Nullable
    public RitualResult<?> getRecipeResult() {
        var recipe = getRecipe();
        if (recipe != null) {
            if (recipe.left().isPresent()) {
                return recipe.left().get().result();
            } else if (recipe.right().isPresent()) {
                return recipe.right().get().result();
            }
        }
        return null;
    }

    public Entity getOrCreateEntityResult() {
        if (entityResult == null) {
            RitualResult<?> recipeResult = getRecipeResult();
            if (recipeResult instanceof EntityResult result) {
                this.entityResult = result.entityType().create(level());
            }
        }
        return entityResult;
    }
}
