package earth.terrarium.spirit.common.item.crystals;

import earth.terrarium.spirit.api.souls.base.SoulContainer;
import earth.terrarium.spirit.api.souls.base.SoulContainingBlock;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import org.jetbrains.annotations.NotNull;

public class InsightCrystalItem extends Item {
    public InsightCrystalItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext useOnContext) {
        if (useOnContext.getLevel().isClientSide) return InteractionResult.SUCCESS;
        SoulContainer container = SoulContainer.of(useOnContext.getLevel(), useOnContext.getClickedPos(), null);
        if (container != null) {
            if (useOnContext.getPlayer() != null) {
                useOnContext.getPlayer().displayClientMessage(container.getStackInSlot(0).toComponent(), true);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }
}
