package earth.terrarium.spirit.api.souls.stack;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public final class SoulStack {
    public static final SoulStack EMPTY = new SoulStack(null, 0);

    private static final String ENTITY_KEY = "Entity";
    private static final String AMOUNT_KEY = "Amount";

    private final EntityType<?> entity;
    private int amount;

    public static final Codec<SoulStack> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("entity").forGetter(SoulStack::getEntity),
            Codec.intRange(0, Integer.MAX_VALUE).fieldOf("amount").forGetter(SoulStack::getAmount)
    ).apply(instance, SoulStack::new));

    private SoulStack(@Nullable EntityType<?> entity, int amount) {
        this.entity = entity;
        this.amount = amount;
    }

    public static SoulStack of(EntityType<?> entity) {
        return new SoulStack(entity, 1);
    }

    public static SoulStack of(EntityType<?> entity, int amount) {
        return new SoulStack(entity, amount);
    }

    @Nullable
    public EntityType<?> getEntity() {
        return entity;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        throwIfEmpty(this);
        this.amount = Mth.clamp(amount, 0, Integer.MAX_VALUE);
    }

    public void shrink(int amount) {
        throwIfEmpty(this);
        this.amount -= amount;
    }

    public void grow(int amount) {
        throwIfEmpty(this);
        this.amount += amount;
    }

    public void shrink() {
        throwIfEmpty(this);
        this.shrink(1);
    }

    public void grow() {
        throwIfEmpty(this);
        this.grow(1);
    }

    public boolean isEmpty() {
        return amount == 0 || entity == null;
    }

    public SoulStack copy() {
        return isEmpty() ? EMPTY : new SoulStack(entity, amount);
    }

    public SoulStack copyWithAmount(int amount) {
        return entity == null ? EMPTY : new SoulStack(entity, amount);
    }

    public static SoulStack fromTag(CompoundTag tag) {
        return new SoulStack(EntityType.byString(tag.getString(ENTITY_KEY)).orElse(null), tag.getInt(AMOUNT_KEY));
    }

    public CompoundTag toTag(CompoundTag tag) {
        if (entity != null) {
            tag.putString(ENTITY_KEY, EntityType.getKey(entity).toString());
            tag.putInt(AMOUNT_KEY, amount);
        }
        return tag;
    }

    @Nullable
    public LivingEntity toEntity(Level level) {
        if (getEntity() == null) return null;
        return (LivingEntity) this.getEntity().create(level);
    }

    public MutableComponent toComponent() {
        if (getEntity() == null) return Component.translatable("item.spirit.soul_crystal.none");
        return Component.translatable("item.spirit.soul_crystal.entity_component", this.getEntity().getDescription(), this.getAmount());
    }

    public boolean is(EntityType<?> entity) {
        return this.getEntity() == entity;
    }

    public boolean is(@Nullable SoulStack soulStack) {
        return soulStack != null && this.is(soulStack.getEntity());
    }

    public boolean is(TagKey<EntityType<?>> tag) {
        return entity != null && entity.is(tag);
    }

    public static void throwIfEmpty(SoulStack stack) {
        if (stack.isEmpty()) {
            throw new IllegalArgumentException("Cannot modify empty SoulStack");
        }
    }
}
