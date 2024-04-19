package earth.terrarium.spirit.common.block;

import earth.terrarium.botarium.common.fluid.FluidApi;
import earth.terrarium.botarium.common.fluid.base.FluidContainer;
import earth.terrarium.botarium.common.fluid.base.FluidHolder;
import earth.terrarium.botarium.common.fluid.base.ItemFluidContainer;
import earth.terrarium.botarium.common.item.ItemStackHolder;
import earth.terrarium.spirit.common.blockentity.SoulBasinBlockEntity;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SoulBasinBlock extends BaseEntityBlock {
    public static final VoxelShape SHAPE = Shapes.or(
            Block.box(2, 9, 2, 14, 15, 14),
            Block.box(4, 7, 4, 12, 8, 12),
            Block.box(5, 3, 5, 11, 9, 11),
            Block.box(3, 0, 3, 13, 3, 13)
    );

    public SoulBasinBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new SoulBasinBlockEntity(blockPos, blockState);
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState blockState, @NotNull Level level, @NotNull BlockPos blockPos, @NotNull Player player, @NotNull InteractionHand interactionHand, @NotNull BlockHitResult blockHitResult) {
        if (interactionHand != InteractionHand.OFF_HAND) {
            ItemStack stack = player.getItemInHand(interactionHand);
            ItemStackHolder holder = new ItemStackHolder(stack);
            if (level.getBlockEntity(blockPos) instanceof SoulBasinBlockEntity soulPedestal) {
                if (FluidContainer.holdsFluid(stack)) {
                    FluidContainer basinContainer = soulPedestal.getFluidContainer();
                    ItemFluidContainer itemContainer = FluidContainer.of(holder);
                    if (itemContainer == null || basinContainer == null) {
                        return InteractionResult.PASS;
                    }
                    if (basinContainer.isEmpty() && !itemContainer.isEmpty()) {
                        FluidHolder extracted = itemContainer.extractFluid(itemContainer.getFluids().get(0), true);
                        var inserted = basinContainer.insertFluid(extracted, true);
                        if (!extracted.isEmpty() && inserted > 0) {
                            FluidHolder finalExtraction = itemContainer.extractFluid(itemContainer.getFluids().get(0).copyWithAmount(inserted), false);
                            basinContainer.insertFluid(finalExtraction, false);
                            if (holder.isDirty()) {
                                player.setItemInHand(interactionHand, holder.getStack());
                            }
                            return InteractionResult.SUCCESS;
                        }
                    } else if (!basinContainer.isEmpty() && itemContainer.isEmpty()) {
                        FluidHolder extracted = basinContainer.extractFluid(basinContainer.getFluids().get(0), true);
                        var inserted = itemContainer.insertFluid(extracted, true);
                        if (!extracted.isEmpty() && inserted > 0) {
                            FluidHolder finalExtraction = basinContainer.extractFluid(basinContainer.getFluids().get(0).copyWithAmount(inserted), false);
                            itemContainer.insertFluid(finalExtraction, false);
                            if (holder.isDirty()) {
                                player.setItemInHand(interactionHand, holder.getStack());
                            }
                            return InteractionResult.SUCCESS;
                        }
                    } else if (!basinContainer.isEmpty() && !itemContainer.isEmpty()) {
                        FluidHolder extracted = itemContainer.extractFluid(itemContainer.getFluids().get(0), true);
                        long inserted = basinContainer.insertFluid(extracted, true);
                        if (!extracted.isEmpty() && inserted > 0) {
                            FluidHolder finalExtraction = itemContainer.extractFluid(itemContainer.getFluids().get(0).copyWithAmount(inserted), false);
                            basinContainer.insertFluid(finalExtraction, false);
                            if (holder.isDirty()) {
                                player.setItemInHand(interactionHand, holder.getStack());
                            }
                            return InteractionResult.SUCCESS;
                        } else {
                            extracted = basinContainer.extractFluid(basinContainer.getFluids().get(0), true);
                            inserted = itemContainer.insertFluid(extracted, true);
                            if (!extracted.isEmpty() && inserted > 0) {
                                FluidHolder finalExtraction = basinContainer.extractFluid(basinContainer.getFluids().get(0).copyWithAmount(inserted), false);
                                itemContainer.insertFluid(finalExtraction, false);
                                if (holder.isDirty()) {
                                    player.setItemInHand(interactionHand, holder.getStack());
                                }
                                return InteractionResult.SUCCESS;
                            }
                        }
                    }
                }
            }
        }
        return InteractionResult.PASS;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, SpiritBlockEntities.SOUL_BASIN.get(), (level1, blockPos, blockState1, blockEntity) -> blockEntity.tick());
    }

    @Override
    public RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(@NotNull BlockState blockState, @NotNull BlockGetter blockGetter, @NotNull BlockPos blockPos, @NotNull CollisionContext collisionContext) {
        return SHAPE;
    }
}
