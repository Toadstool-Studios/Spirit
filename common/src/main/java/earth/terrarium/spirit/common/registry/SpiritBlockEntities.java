package earth.terrarium.spirit.common.registry;

import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry;
import dev.architectury.injectables.annotations.ExpectPlatform;
import earth.terrarium.spirit.Spirit;
import earth.terrarium.spirit.common.blockentity.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.NotImplementedException;

import java.util.function.Supplier;

public class SpiritBlockEntities {

    public static final ResourcefulRegistry<BlockEntityType<?>> BLOCK_ENTITIES = ResourcefulRegistries.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Spirit.MODID);

    public static final RegistryEntry<BlockEntityType<PedestalBlockEntity>> PEDESTAL = BLOCK_ENTITIES.register("pedestal", () -> create(PedestalBlockEntity::new, SpiritBlocks.PEDESTAL.get()).build(null));

    public static final Supplier<BlockEntityType<SoulBasinBlockEntity>> SOUL_BASIN = BLOCK_ENTITIES.register("soul_basin", () -> create(SoulBasinBlockEntity::new, SpiritBlocks.SOUL_BASIN.get()).build(null));
    public static final Supplier<BlockEntityType<FlooFireBlockEntity>> FLOO_FIRE = BLOCK_ENTITIES.register("soul_fire", () -> create(FlooFireBlockEntity::new, SpiritBlocks.FLOO_FIRE.get()).build(null));
    public static final Supplier<BlockEntityType<SoulCrystalBlockEntity>> SOUL_CRYSTAL = BLOCK_ENTITIES.register("soul_crystal", () -> create(SoulCrystalBlockEntity::new, SpiritBlocks.SOUL_CRYSTAL.get()).build(null));

    public static final Supplier<BlockEntityType<SoulCageBlockEntity>> SOUL_CAGE = BLOCK_ENTITIES.register("soul_cage", () -> create(SoulCageBlockEntity::new, SpiritBlocks.SOUL_CAGE.get()).build(null));
    public static final Supplier<BlockEntityType<AttunementTableBlockEntity>> ATTUNEMENT_TABLE = BLOCK_ENTITIES.register("attunement_table", () -> create(AttunementTableBlockEntity::new, SpiritBlocks.ATTUNEMENT_TABLE.get()).build(null));
    public static final Supplier<BlockEntityType<DungeonFrameBlockEntity>> DUNGEON_FRAME = BLOCK_ENTITIES.register("dungeon_frame", () -> create(DungeonFrameBlockEntity::new, SpiritBlocks.DUNGEON_FRAME.get()).build(null));

    @ExpectPlatform
    public static <T extends BlockEntity> BlockEntityType.Builder<T> create(BlockEntitySupplier<T> supplier, Block... blocks) {
        throw new NotImplementedException();
    }

    @FunctionalInterface
    public interface BlockEntitySupplier<T extends BlockEntity> {
        T create(BlockPos blockPos, BlockState blockState);
    }
}
