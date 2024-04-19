package earth.terrarium.spirit.api.souls.impl;

import earth.terrarium.botarium.util.Serializable;
import earth.terrarium.spirit.api.souls.base.SoulContainer;
import earth.terrarium.spirit.api.utils.SoulfulCreature;
import earth.terrarium.spirit.api.souls.base.MobContainer;
import earth.terrarium.spirit.api.souls.stack.SoulStack;
import earth.terrarium.spirit.api.souls.SoulApi;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class SingleMobContainer implements SoulContainer, MobContainer, Serializable {
    @Nullable
    public CompoundTag entityData;
    @Nullable
    public SoulStack soulStack;
    public boolean soulless;
    public Consumer<SingleMobContainer> update;

    public static final String MOB_KEY = "Mob";
    public static final String ENTITY_KEY = "Type";
    public static final String DATA_KEY = "Data";

    public SingleMobContainer(ItemStack stack) {
        update = (container) -> container.serialize(stack.getOrCreateTag());
        deserialize(stack.getOrCreateTag());
    }

    @Override
    public int insertIntoSlot(int slot, SoulStack soulStack, boolean simulate) {
        return insert(soulStack, simulate);
    }

    @Override
    public int insert(SoulStack soulStack, boolean simulate) {
        if (soulStack == null) {
            return 0;
        } else {
            if (soulStack.is(this.soulStack)) {
                if (soulless) {
                    return 0;
                } else {
                    if (!simulate) {
                        soulless = true;
                        update.accept(this);
                    }
                    return 1;
                }
            }
        }
        return 0;
    }

    @Override
    public SoulStack extractFromSlot(int slot, int amount, boolean simulate) {
        return extract(amount, simulate);
    }

    @Override
    public SoulStack extract(int amount, boolean simulate) {
        if (soulStack == null || soulless) {
            return SoulStack.EMPTY;
        } else {
            if (!simulate) {
                soulless = true;
                update.accept(this);
            }
            return soulStack.copy();
        }
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
        return soulStack == null || soulless ? SoulStack.EMPTY : soulStack.copy();
    }

    @Override
    public boolean insertMob(LivingEntity mob, boolean simulate) {
        if (soulStack == null) {
            if (!simulate) {
                soulStack = SoulStack.of(mob.getType());
                entityData = mob.saveWithoutId(new CompoundTag());
                soulless = ((SoulfulCreature) mob).isSoulless();
                update.accept(this);
            }
            return true;
        }
        return false;
    }

    @Nullable
    public LivingEntity extractMob(Level level, boolean simulate) {
        if (soulStack != null && entityData != null && soulStack.getEntity() != null) {
            LivingEntity entity = (LivingEntity) soulStack.getEntity().create(level);
            if (entity != null) {
                entity.load(entityData);
                ((SoulfulCreature) entity).setIfSoulless(soulless);
                if (!simulate) {
                    soulStack = null;
                    entityData = null;
                    soulless = true;
                    update.accept(this);
                }
                return entity;
            }
        }
        return null;
    }

    @Override
    public CompoundTag serialize(CompoundTag tag) {
        CompoundTag entity = new CompoundTag();
        if (soulStack != null && soulStack.getEntity() != null) {
            entity.putString(ENTITY_KEY, EntityType.getKey(soulStack.getEntity()).toString());
        } else {
            entity.putString(ENTITY_KEY, "");
        }
        entity.put(DATA_KEY, entityData);
        entity.putBoolean(SoulApi.SOULLESS_TAG, soulless);
        tag.put(MOB_KEY, entity);
        return tag;
    }

    @Override
    public void deserialize(CompoundTag tag) {
        if (tag.contains(MOB_KEY)) {
            if (!tag.getCompound(MOB_KEY).getString(ENTITY_KEY).isBlank()) {
                soulStack = SoulStack.of(EntityType.byString(tag.getCompound(MOB_KEY).getString(ENTITY_KEY)).orElse(null));
            }
            CompoundTag entity = tag.getCompound(MOB_KEY);
            entityData = entity.getCompound(DATA_KEY);
            soulless = entity.getBoolean(SoulApi.SOULLESS_TAG);
        }
    }

    public MutableComponent toComponent() {
        SoulStack soul = getStackInSlot(0);
        if (soul != null && soul.getEntity() != null) {
            var component = soul.getEntity().getDescription();
            if (!soulless) {
                return Component.translatable("spirit.item.mob_container.soulful", component).withStyle(ChatFormatting.RED);
            } else {
                return Component.translatable("spirit.item.mob_container.soulless", component).withStyle(ChatFormatting.AQUA);
            }
        } else {
            return Component.translatable("spirit.item.crystal.tooltip_empty").withStyle(ChatFormatting.GRAY);
        }
    }
}
