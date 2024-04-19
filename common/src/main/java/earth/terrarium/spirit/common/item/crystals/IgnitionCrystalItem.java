package earth.terrarium.spirit.common.item.crystals;

import earth.terrarium.spirit.common.block.DungeonPortalShape;
import earth.terrarium.spirit.common.registry.SpiritBlocks;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;

public class IgnitionCrystalItem extends Item {
	public IgnitionCrystalItem(Item.Properties properties) {
		super(properties);
	}

	public @NotNull InteractionResult useOn(UseOnContext useOnContext) {
		Player player = useOnContext.getPlayer();
		Level level = useOnContext.getLevel();
		BlockPos blockPos = useOnContext.getClickedPos();
		BlockState blockState = level.getBlockState(blockPos);
		if (!CampfireBlock.canLight(blockState) && !CandleBlock.canLight(blockState) && !CandleCakeBlock.canLight(blockState)) {
			BlockPos blockPos2 = blockPos.relative(useOnContext.getClickedFace());
			if (BaseFireBlock.canBePlacedAt(level, blockPos2, useOnContext.getHorizontalDirection()) && blockState.is(BlockTags.SOUL_FIRE_BASE_BLOCKS)) {
				level.playSound(player, blockPos2, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.4F + 0.8F);
				BlockState blockState2 = SpiritBlocks.RAGING_SOUL_FIRE.get().defaultBlockState();
				level.setBlock(blockPos2, blockState2, 11);
				level.gameEvent(player, GameEvent.BLOCK_PLACE, blockPos);
				ItemStack itemStack = useOnContext.getItemInHand();
				if (player instanceof ServerPlayer) {
					CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer)player, blockPos2, itemStack);
					itemStack.hurtAndBreak(1, player, (playerx) -> {
						playerx.broadcastBreakEvent(useOnContext.getHand());
					});
				}

				return InteractionResult.sidedSuccess(level.isClientSide());
			} else {
				return InteractionResult.FAIL;
			}
		} else {
			level.playSound(player, blockPos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.4F + 0.8F);
			level.setBlock(blockPos, (BlockState)blockState.setValue(BlockStateProperties.LIT, true), 11);
			level.gameEvent(player, GameEvent.BLOCK_CHANGE, blockPos);
			if (player != null) {
				useOnContext.getItemInHand().hurtAndBreak(1, player, (playerx) -> {
					playerx.broadcastBreakEvent(useOnContext.getHand());
				});
			}

			return InteractionResult.sidedSuccess(level.isClientSide());
		}
	}

	private static boolean isPortal(Level level, BlockPos pos, Direction direction) {
		BlockPos.MutableBlockPos mutableBlockPos = pos.mutable();
		boolean bl = false;

        for (Direction direction2 : Direction.values()) {
            BlockState blockState = level.getBlockState(mutableBlockPos.set(pos).move(direction2));
            if (blockState.is(SpiritBlocks.SOUL_SLATE.get()) || blockState.is(SpiritBlocks.DUNGEON_FRAME.get())) {
                bl = true;
                break;
            }
        }

		if (!bl) {
			return false;
		} else {
			Direction.Axis axis = direction.getAxis().isHorizontal() ? direction.getCounterClockWise().getAxis() : Direction.Plane.HORIZONTAL.getRandomAxis(level.random);
			return DungeonPortalShape.findEmptyPortalShape(level, pos, axis).isPresent();
		}
	}
}
