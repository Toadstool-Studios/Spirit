package earth.terrarium.spirit.common.item.crystals;

import earth.terrarium.spirit.api.souls.base.SoulContainingItem;
import earth.terrarium.spirit.api.utils.EntityRarity;
import earth.terrarium.spirit.api.souls.stack.SoulStack;
import earth.terrarium.spirit.common.blockentity.SoulCrystalBlockEntity;
import earth.terrarium.spirit.common.containers.SoulCrystalContainer;
import earth.terrarium.spirit.common.registry.SpiritBlocks;
import earth.terrarium.spirit.common.registry.SpiritItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SoulCrystalItem extends BlockItem implements SoulContainingItem<SoulCrystalContainer> {
    public SoulCrystalItem(Properties properties) {
        super(SpiritBlocks.SOUL_CRYSTAL.get(), properties);
    }

    @NotNull
    public SoulCrystalContainer getContainer(ItemStack object) {
        return new SoulCrystalContainer(object);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack itemStack, @Nullable Level level, @NotNull List<Component> list, @NotNull TooltipFlag tooltipFlag) {
        SoulStack container = getContainer(itemStack).getStackInSlot(0);
        Component component;
        if (container.isEmpty() || container.getEntity() == null) {
            component = Component.translatable("item.spirit.soul_crystal.none");
        } else {
            component = container.getEntity().getDescription().copy().withStyle(EntityRarity.getRarity(container.getEntity()).color);
        }
        list.add(Component.translatable("item.spirit.soul_crystal.tooltip", component).withStyle(ChatFormatting.GRAY));
    }

    @Override
    public @NotNull InteractionResult place(BlockPlaceContext blockPlaceContext) {
        InteractionResult place = super.place(blockPlaceContext);
        BlockEntity blockEntity = blockPlaceContext.getLevel().getBlockEntity(blockPlaceContext.getClickedPos());
        if (blockEntity instanceof SoulCrystalBlockEntity container) {
            container.getContainer().deserialize(blockPlaceContext.getItemInHand().getOrCreateTag());
        }
        return place;
    }

    public static ItemStack createSoulCrystal(EntityType<?> type) {
        ItemStack itemStack = new ItemStack(SpiritItems.SOUL_CRYSTAL.get());
        itemStack.getOrCreateTag().putString("Entity", EntityType.getKey(type).toString());
        return itemStack;
    }
}
