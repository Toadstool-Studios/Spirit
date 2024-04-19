package earth.terrarium.spirit.client.fabric;

import earth.terrarium.spirit.client.SpiritClient;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class SpiritClientImpl {
    public static <E extends BlockEntity> void registerBlockEntityRenderers(BlockEntityType<E> blockEntityType, BlockEntityRendererProvider<? super E> blockEntityRendererFactory) {
        BlockEntityRendererRegistry.register(blockEntityType, blockEntityRendererFactory);
    }

    public static void registerItemProperties(Item item, ResourceLocation resourceLocation, ClampedItemPropertyFunction clampedItemPropertyFunction) {
        ItemProperties.register(item, resourceLocation, clampedItemPropertyFunction);
    }

    public static void registerRenderLayer(Block block, RenderType type) {
        BlockRenderLayerMap.INSTANCE.putBlock(block, type);
    }
}
