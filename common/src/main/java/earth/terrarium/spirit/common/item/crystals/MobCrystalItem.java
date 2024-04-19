package earth.terrarium.spirit.common.item.crystals;

import earth.terrarium.spirit.api.souls.base.SoulContainingItem;
import earth.terrarium.spirit.api.souls.impl.SingleMobContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MobCrystalItem extends Item implements SoulContainingItem<SingleMobContainer> {

    public MobCrystalItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, level, list, tooltipFlag);
        SingleMobContainer container = getContainer(itemStack);
        if (container != null) {
            list.add(container.toComponent());
        }
    }

    @Override
    public @Nullable SingleMobContainer getContainer(ItemStack object) {
        return new SingleMobContainer(object);
    }

    @Override
    public InteractionResult useOn(UseOnContext useOnContext) {
        if (!useOnContext.getLevel().isClientSide) {
            Player player = useOnContext.getPlayer();
            if (player != null) {
                ItemStack stack = player.getItemInHand(useOnContext.getHand());
                if (stack.getItem() instanceof MobCrystalItem) {
                    SingleMobContainer container = getContainer(stack);
                    if (container != null) {
                        LivingEntity entity = container.extractMob(useOnContext.getLevel(), false);
                        if (entity != null) {
                            BlockPos offset = useOnContext.getClickedPos().relative(useOnContext.getClickedFace());
                            entity.setPos(offset.getX() + 0.5, offset.getY(), offset.getZ() + 0.5);
                            useOnContext.getLevel().addFreshEntity(entity);
                            return InteractionResult.SUCCESS;
                        }
                    }
                }
            }
        }
        return super.useOn(useOnContext);
    }
}
