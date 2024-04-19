package earth.terrarium.spirit.common.block;

import earth.terrarium.spirit.common.registry.SpiritBlocks;
import net.minecraft.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Predicate;

public class DungeonPortalShape {
	private static final int MIN_WIDTH = 2;
	private static final int MIN_HEIGHT = 3;
	public static final int MAX_DIM = 21;
	private static final BlockBehaviour.StatePredicate FRAME = (blockState, blockGetter, blockPos) -> blockState.is(SpiritBlocks.DUNGEON_FRAME.get()) || blockState.is(SpiritBlocks.SOUL_SLATE.get());
	private static final BlockBehaviour.StatePredicate PORTAL = (blockState, blockGetter, blockPos) -> blockState.is(SpiritBlocks.PORTAL.get());
	private static final float SAFE_TRAVEL_MAX_ENTITY_XY = 4.0F;
	private static final double SAFE_TRAVEL_MAX_VERTICAL_DELTA = 1.0;
	private final LevelAccessor level;
	private final Direction.Axis axis;
	private final Direction rightDir;
	private int numPortalBlocks;
	@Nullable
	private BlockPos bottomLeft;
	private int height;
	private final int width;

	public static Optional<DungeonPortalShape> findEmptyPortalShape(LevelAccessor level, BlockPos bottomLeft, Direction.Axis axis) {
		return findPortalShape(level, bottomLeft, (portalShape) -> portalShape.isValid() && portalShape.numPortalBlocks == 0, axis);
	}

	public static Optional<DungeonPortalShape> findPortalShape(LevelAccessor level, BlockPos bottomLeft, Predicate<DungeonPortalShape> predicate, Direction.Axis axis) {
		Optional<DungeonPortalShape> optional = Optional.of(new DungeonPortalShape(level, bottomLeft, axis)).filter(predicate);
		if (optional.isPresent()) {
			return optional;
		} else {
			Direction.Axis axis2 = axis == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X;
			return Optional.of(new DungeonPortalShape(level, bottomLeft, axis2)).filter(predicate);
		}
	}

	public DungeonPortalShape(LevelAccessor levelAccessor, BlockPos blockPos, Direction.Axis axis) {
		this.level = levelAccessor;
		this.axis = axis;
		this.rightDir = axis == Direction.Axis.X ? Direction.WEST : Direction.SOUTH;
		this.bottomLeft = this.calculateBottomLeft(blockPos);
		if (this.bottomLeft == null) {
			this.bottomLeft = blockPos;
			this.width = 1;
			this.height = 1;
		} else {
			this.width = this.calculateWidth();
			if (this.width > 0) {
				this.height = this.calculateHeight();
			}
		}

	}

	@Nullable
	private BlockPos calculateBottomLeft(BlockPos pos) {
		for(int i = Math.max(this.level.getMinBuildHeight(), pos.getY() - MAX_DIM); pos.getY() > i && isEmpty(this.level.getBlockState(pos.below())); pos = pos.below()) {
		}

		Direction direction = this.rightDir.getOpposite();
		int j = this.getDistanceUntilEdgeAboveFrame(pos, direction) - 1;
		return j < 0 ? null : pos.relative(direction, j);
	}

	private int calculateWidth() {
		int i = this.getDistanceUntilEdgeAboveFrame(this.bottomLeft, this.rightDir);
		return i >= MIN_WIDTH && i <= MAX_DIM ? i : 0;
	}

	private int getDistanceUntilEdgeAboveFrame(BlockPos pos, Direction direction) {
		BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();

		for(int i = 0; i <= MAX_DIM; ++i) {
			mutableBlockPos.set(pos).move(direction, i);
			BlockState blockState = this.level.getBlockState(mutableBlockPos);
			if (!isEmpty(blockState)) {
				if (FRAME.test(blockState, this.level, mutableBlockPos)) {
					return i;
				}
				break;
			}

			BlockState blockState2 = this.level.getBlockState(mutableBlockPos.move(Direction.DOWN));
			if (!FRAME.test(blockState2, this.level, mutableBlockPos)) {
				break;
			}
		}

		return 0;
	}

	private int calculateHeight() {
		BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
		int i = this.getDistanceUntilTop(mutableBlockPos);
		return i >= MIN_HEIGHT && i <= MAX_DIM && this.hasTopFrame(mutableBlockPos, i) ? i : 0;
	}

	private boolean hasTopFrame(BlockPos.MutableBlockPos pos, int distanceToTop) {
		if (this.bottomLeft == null) return false;

		for(int i = 0; i < this.width; ++i) {
			BlockPos.MutableBlockPos mutableBlockPos = pos.set(this.bottomLeft).move(Direction.UP, distanceToTop).move(this.rightDir, i);
			if (!FRAME.test(this.level.getBlockState(mutableBlockPos), this.level, mutableBlockPos)) {
				return false;
			}
		}

		return true;
	}

	private int getDistanceUntilTop(BlockPos.MutableBlockPos pos) {
		if (this.bottomLeft == null) return 0;

		for(int i = 0; i < MAX_DIM; ++i) {
			pos.set(this.bottomLeft).move(Direction.UP, i).move(this.rightDir, -1);
			if (!FRAME.test(this.level.getBlockState(pos), this.level, pos)) {
				return i;
			}

			pos.set(this.bottomLeft).move(Direction.UP, i).move(this.rightDir, this.width);
			if (!FRAME.test(this.level.getBlockState(pos), this.level, pos)) {
				return i;
			}

			for(int j = 0; j < this.width; ++j) {
				pos.set(this.bottomLeft).move(Direction.UP, i).move(this.rightDir, j);
				BlockState blockState = this.level.getBlockState(pos);
				if (!isEmpty(blockState)) {
					return i;
				}

				if (PORTAL.test(blockState, this.level, pos)) {
					++this.numPortalBlocks;
				}
			}
		}

		return MAX_DIM;
	}

	private static boolean isEmpty(BlockState state) {
		return state.isAir() || state.is(BlockTags.FIRE) || state.is(SpiritBlocks.PORTAL.get());
	}

	public boolean isValid() {
		return this.bottomLeft != null && this.width >= MIN_WIDTH && this.width <= MAX_DIM && this.height >= MIN_HEIGHT && this.height <= MAX_DIM;
	}

	public void createPortalBlocks() {
		if (this.bottomLeft == null) return;
		BlockState blockState = SpiritBlocks.PORTAL.get().defaultBlockState().setValue(NetherPortalBlock.AXIS, this.axis);
		BlockPos.betweenClosed(this.bottomLeft, this.bottomLeft.relative(Direction.UP, this.height - 1).relative(this.rightDir, this.width - 1)).forEach((blockPos) -> {
			this.level.setBlock(blockPos, blockState, 18);
		});
	}

	public boolean isComplete() {
		return this.isValid() && this.numPortalBlocks == this.width * this.height;
	}

	public static Vec3 getRelativePosition(BlockUtil.FoundRectangle foundRectangle, Direction.Axis axis, Vec3 pos, EntityDimensions entityDimensions) {
		double d = (double)foundRectangle.axis1Size - (double)entityDimensions.width;
		double e = (double)foundRectangle.axis2Size - (double)entityDimensions.height;
		BlockPos blockPos = foundRectangle.minCorner;
		double g;
		if (d > 0.0) {
			float f = (float)blockPos.get(axis) + entityDimensions.width / 2.0F;
			g = Mth.clamp(Mth.inverseLerp(pos.get(axis) - (double)f, 0.0, d), 0.0, SAFE_TRAVEL_MAX_VERTICAL_DELTA);
		} else {
			g = 0.5;
		}

		double h;
		Direction.Axis axis2;
		if (e > 0.0) {
			axis2 = Direction.Axis.Y;
			h = Mth.clamp(Mth.inverseLerp(pos.get(axis2) - (double)blockPos.get(axis2), 0.0, e), 0.0, SAFE_TRAVEL_MAX_VERTICAL_DELTA);
		} else {
			h = 0.0;
		}

		axis2 = axis == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X;
		double i = pos.get(axis2) - ((double)blockPos.get(axis2) + 0.5);
		return new Vec3(g, h, i);
	}

	public static PortalInfo createPortalInfo(ServerLevel level, BlockUtil.FoundRectangle portalPos, Direction.Axis axis, Vec3 relativePos, Entity entity, Vec3 velocity, float yRot, float xRot) {
		BlockPos blockPos = portalPos.minCorner;
		BlockState blockState = level.getBlockState(blockPos);
		Direction.Axis axis2 = blockState.getOptionalValue(BlockStateProperties.HORIZONTAL_AXIS).orElse(Direction.Axis.X);
		double d = portalPos.axis1Size;
		double e = portalPos.axis2Size;
		EntityDimensions entityDimensions = entity.getDimensions(entity.getPose());
		int i = axis == axis2 ? 0 : 90;
		Vec3 vec3 = axis == axis2 ? velocity : new Vec3(velocity.z, velocity.y, -velocity.x);
		double f = (double)entityDimensions.width / 2.0 + (d - (double)entityDimensions.width) * relativePos.x();
		double g = (e - (double)entityDimensions.height) * relativePos.y();
		double h = 0.5 + relativePos.z();
		boolean bl = axis2 == Direction.Axis.X;
		Vec3 vec32 = new Vec3((double)blockPos.getX() + (bl ? f : h), (double)blockPos.getY() + g, (double)blockPos.getZ() + (bl ? h : f));
		Vec3 vec33 = findCollisionFreePosition(vec32, level, entity, entityDimensions);
		return new PortalInfo(vec33, vec3, yRot + (float)i, xRot);
	}

	private static Vec3 findCollisionFreePosition(Vec3 pos, ServerLevel level, Entity entity, EntityDimensions dimensions) {
		if (!(dimensions.width > SAFE_TRAVEL_MAX_ENTITY_XY) && !(dimensions.height > SAFE_TRAVEL_MAX_ENTITY_XY)) {
			double d = (double)dimensions.height / 2.0;
			Vec3 vec3 = pos.add(0.0, d, 0.0);
			VoxelShape voxelShape = Shapes.create(AABB.ofSize(vec3, dimensions.width, 0.0, dimensions.width).expandTowards(0.0, SAFE_TRAVEL_MAX_VERTICAL_DELTA, 0.0).inflate(1.0E-6));
			Optional<Vec3> optional = level.findFreePosition(entity, voxelShape, vec3, dimensions.width, dimensions.height, dimensions.width);
			Optional<Vec3> optional2 = optional.map((vec3x) -> vec3x.subtract(0.0, d, 0.0));
			return optional2.orElse(pos);
		} else {
			return pos;
		}
	}
}
