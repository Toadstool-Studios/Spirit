package earth.terrarium.spirit.client.forge;

import earth.terrarium.spirit.client.SpiritClient;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class SpiritClientImpl {
    public static <E extends BlockEntity> void registerBlockEntityRenderers(BlockEntityType<E> blockEntityType, BlockEntityRendererProvider<? super E> blockEntityRendererFactory) {
        BlockEntityRenderers.register(blockEntityType, blockEntityRendererFactory);
    }

    public static void registerItemProperties(Item item, ResourceLocation resourceLocation, ClampedItemPropertyFunction clampedItemPropertyFunction) {
        ItemProperties.register(item, resourceLocation, clampedItemPropertyFunction);
    }

    public static void registerRenderLayer(Block block, RenderType type) {
        ItemBlockRenderTypes.setRenderLayer(block, type);
    }

}
