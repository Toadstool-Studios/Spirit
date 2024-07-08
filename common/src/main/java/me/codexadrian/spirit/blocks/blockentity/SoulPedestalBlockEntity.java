package me.codexadrian.spirit.blocks.blockentity;

import me.codexadrian.spirit.Corrupted;
import me.codexadrian.spirit.recipe.PedestalRecipe;
import me.codexadrian.spirit.registry.SpiritBlocks;
import me.codexadrian.spirit.utils.RecipeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
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

import java.util.ArrayList;

public class SoulPedestalBlockEntity extends BlockEntity {

    public EntityType<?> type;
    private ItemStack item = ItemStack.EMPTY;

    @Nullable
    public Entity entity;

    @Nullable
    public PedestalRecipe containedRecipe;
    public int burnTime = 0;
    public int age;

    public SoulPedestalBlockEntity(BlockPos $$1, BlockState $$2) {
        super(SpiritBlocks.SOUL_PEDESTAL_ENTITY.get(), $$1, $$2);
    }

    public static void tick(Level level1, BlockPos blockPos, BlockState blockState1, BlockEntity blockEntity) {
        if (blockEntity instanceof SoulPedestalBlockEntity soulPedestal) {
            soulPedestal.age = (soulPedestal.age + 1) % Integer.MAX_VALUE;
            if (soulPedestal.containedRecipe != null) {
                if (!RecipeUtils.validatePedestals(blockPos, level1, new ArrayList<>(soulPedestal.containedRecipe.ingredients()), false)) {
                    soulPedestal.setRecipe(null);
                    return;
                }
                if (soulPedestal.burnTime < soulPedestal.containedRecipe.duration()) {
                    for (int i = 0; i < 5; i++) {
                        if(soulPedestal.burnTime < soulPedestal.containedRecipe.duration() * .5) {
                            double percentage = 2 * soulPedestal.burnTime / (double) soulPedestal.containedRecipe.duration();
                            level1.addParticle(ParticleTypes.SOUL,
                                    blockPos.getX() + (3 * Math.sin(percentage * 2 * Math.PI)) + 0.5,
                                    blockPos.getY() + 0.75,
                                    blockPos.getZ() + (3 * Math.cos(percentage * 2 * Math.PI)) + 0.5,
                                    0,
                                    0,
                                    0
                            );
                        } else {
                            double percentage = 2 * ((soulPedestal.burnTime - soulPedestal.containedRecipe.duration() * .5) / (double) soulPedestal.containedRecipe.duration());
                            level1.addParticle(ParticleTypes.SOUL,
                                    blockPos.getX() + (3.0 * (1 - percentage) * Math.sin(percentage * 2 * Math.PI)) + 0.5,
                                    blockPos.getY() + 0.75,
                                    blockPos.getZ() + (3.0 * (1 - percentage) * Math.cos(percentage * 2 * Math.PI)) + 0.5,
                                    0,
                                    0,
                                    0
                            );
                        }
                    }
                } else if (RecipeUtils.validatePedestals(blockPos, level1, new ArrayList<>(soulPedestal.containedRecipe.ingredients()), true)) {
                    Entity entity = soulPedestal.containedRecipe.entityOutput().create(level1);
                    if (entity != null) {
                        if(soulPedestal.containedRecipe.outputNbt().isPresent()) entity.load(soulPedestal.containedRecipe.outputNbt().get());
                        entity.setPos(blockPos.getX() + 0.5, blockPos.getY() + 0.75, blockPos.getZ() + 0.5);
                        level1.addFreshEntity(entity);
                        for (int i = 0; i < 10; i++) {
                            level1.addParticle(ParticleTypes.SOUL, entity.getX(), entity.getY(), entity.getZ(), 0, 0, 0);
                        }
                        soulPedestal.setType(null);
                    }
                    level1.sendBlockUpdated(blockPos, blockState1, blockState1, Block.UPDATE_ALL);
                    soulPedestal.setRecipe(null);
                }
                soulPedestal.burnTime++;
            }
        }
    }

    @Override
    public void load(@NotNull CompoundTag compoundTag) {
        super.load(compoundTag);
        if (compoundTag.contains("Soul")) {
            setType(Registry.ENTITY_TYPE.get(ResourceLocation.tryParse(compoundTag.getString("Soul"))));
        } else {
            setType(null);
        }
        if (compoundTag.contains("PedestalRecipe") && hasLevel()) {
            var recipe = PedestalRecipe.getEffect(compoundTag.getString("PedestalRecipe"), getLevel().getRecipeManager());
            recipe.ifPresent(pedestalRecipe -> containedRecipe = pedestalRecipe);
        } else {
            containedRecipe = null;
        }
        burnTime = compoundTag.getInt("BurnTime");
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag compoundTag) {
        super.saveAdditional(compoundTag);
        if (type != null) {
            compoundTag.putString("Soul", Registry.ENTITY_TYPE.getKey(type).toString());
        } else {
            compoundTag.remove("Soul");
        }
        if (containedRecipe != null) {
            compoundTag.putString("PedestalRecipe", containedRecipe.id().toString());
        } else {
            compoundTag.remove("PedestalRecipe");
        }
        compoundTag.putInt("BurnTime", burnTime);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag);
        return tag;
    }

    public Entity getOrCreateEntity() {
        if (this.entity == null && this.hasLevel() && this.type != null) {
            this.entity = this.type.create(getLevel());
            if (entity instanceof Corrupted corrupted) corrupted.setCorrupted();
        }
        return entity;
    }

    public void setType(EntityType<?> type) {
        this.type = type;
        this.entity = null;
        this.setChanged();
    }

    public void setRecipe(@Nullable PedestalRecipe recipe) {
        this.containedRecipe = recipe;
        if (recipe == null) burnTime = 0;
        this.setChanged();
    }
}
