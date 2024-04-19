package earth.terrarium.spirit.common.block;

import earth.terrarium.spirit.common.blockentity.PedestalBlockEntity;
import earth.terrarium.spirit.common.registry.SpiritBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PedestalBlock extends BaseEntityBlock {

    public static final VoxelShape SHAPE = Shapes.or(
            Block.box(4, 3, 4, 12, 7, 12),
            Block.box(2, 0, 2, 14, 3, 14),
            Block.box(2, 7, 2, 14, 10, 14)
    );

    public PedestalBlock(Properties properties) {
        super(properties);
    }

    public @NotNull InteractionResult use(@NotNull BlockState blockState, @NotNull Level level, @NotNull BlockPos blockPos, @NotNull Player player, @NotNull InteractionHand interactionHand, @NotNull BlockHitResult blockHitResult) {
        if (interactionHand == InteractionHand.MAIN_HAND) {
            if (!level.isClientSide) {
                ItemStack stack = player.getItemInHand(interactionHand);
                BlockEntity blockEntity = level.getBlockEntity(blockPos);
                if (blockEntity instanceof PedestalBlockEntity cage) {
                    if (cage.isEmpty() && !stack.isEmpty()) {
                        cage.setItem(0, stack);
                        player.setItemInHand(interactionHand, ItemStack.EMPTY);
                        cage.update();
                        return InteractionResult.SUCCESS;
                    } else if (stack.isEmpty()) {
                        player.setItemInHand(interactionHand, cage.removeItemNoUpdate(0));
                        cage.update();
                        return InteractionResult.SUCCESS;
                    }
                }
            } else {
                ItemStack stack = player.getItemInHand(interactionHand);
                BlockEntity blockEntity = level.getBlockEntity(blockPos);
                if (blockEntity instanceof PedestalBlockEntity cage) {
                    if ((cage.isEmpty() && !stack.isEmpty()) || (stack.isEmpty() && !cage.isEmpty())) {
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }
        return super.use(blockState, level, blockPos, player, interactionHand, blockHitResult);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState blockState, @NotNull BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, SpiritBlockEntities.PEDESTAL.get(), PedestalBlockEntity::tick);
    }

    @Override
    public @NotNull List<ItemStack> getDrops(@NotNull BlockState blockState, LootParams.Builder builder) {
        List<ItemStack> drops = super.getDrops(blockState, builder);
        BlockEntity blockE = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (blockE instanceof PedestalBlockEntity pedestalBlock) {
            drops.add(pedestalBlock.getItem(0));
        }

        return drops;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        return new PedestalBlockEntity(blockPos, blockState);
    }

    @Override
    public RenderShape getRenderShape(@NotNull BlockState blockState) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(@NotNull BlockState blockState, @NotNull BlockGetter blockGetter, @NotNull BlockPos blockPos, @NotNull CollisionContext collisionContext) {
        return SHAPE;
    }
}
