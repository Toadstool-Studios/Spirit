package earth.terrarium.spirit.common.item.block;

import earth.terrarium.spirit.api.souls.Atunable;
import earth.terrarium.spirit.common.blockentity.SoulCageBlockEntity;
import earth.terrarium.spirit.common.registry.SpiritBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

public class SoulCageBlockItem extends BlockItem implements Atunable {
    public SoulCageBlockItem(Properties properties) {
        super(SpiritBlocks.SOUL_CAGE.get(), properties);
    }

    @Override
    public @NotNull InteractionResult place(BlockPlaceContext blockPlaceContext) {
        InteractionResult place = super.place(blockPlaceContext);
        BlockEntity blockEntity = blockPlaceContext.getLevel().getBlockEntity(blockPlaceContext.getClickedPos());
        if (blockEntity instanceof SoulCageBlockEntity cage) {
            cage.setEntityId(EntityType.byString(blockPlaceContext.getItemInHand().getOrCreateTag().getString("Entity")).orElse(null));
            cage.setCritical(blockPlaceContext.getItemInHand().getOrCreateTag().getBoolean("Critical"));
        }
        return place;
    }

    @Override
    public boolean meetsRequirements(Level level, BlockPos centerPos, ItemStack atunnableItem) {
        return false;
    }

    @Override
    public void clearAttunement(ItemStack stack) {
        stack.getOrCreateTag().remove("Entity");
        stack.getOrCreateTag().remove("Critical");
    }

    @Override
    public boolean canAttune(ItemStack stack, EntityType<?> type, boolean critical) {
        if (isAttunedFor(stack, type)) {
            return isCritical(stack);
        }
        return true;
    }

    @Override
    public boolean isAttunedFor(ItemStack stack, EntityType<?> type) {
        return stack.getOrCreateTag().getString("Entity").equals(EntityType.getKey(type).toString());
    }

    @Override
    public boolean isCritical(ItemStack stack) {
        return stack.getOrCreateTag().getBoolean("Critical");
    }
}
