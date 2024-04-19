package earth.terrarium.spirit.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import earth.terrarium.spirit.client.renderer.utils.RenderUtils;
import earth.terrarium.spirit.common.blockentity.PedestalBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.NotNull;

public class PedestalRenderer implements BlockEntityRenderer<PedestalBlockEntity> {
    private final ItemRenderer itemRenderer;

    public PedestalRenderer(BlockEntityRendererProvider.Context context) {
        itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(PedestalBlockEntity blockEntity, float f, @NotNull PoseStack matrixStack, @NotNull MultiBufferSource multiBufferSource, int i, int j) {
        if (!blockEntity.hasLevel() || blockEntity.isEmpty()) return;
        if (blockEntity.getItem(0).isEmpty()) return;

        if (blockEntity.getItem(0).getCount() > 1 || blockEntity.maxProcessTime == 0) {
            matrixStack.pushPose();
            matrixStack.translate(0.5D, 0.65D, 0.5D);
            matrixStack.mulPose(Axis.XP.rotationDegrees(90));
            matrixStack.scale(0.55f, 0.55f, 0.55f);
            itemRenderer.renderStatic(blockEntity.getItem(0), ItemDisplayContext.FIXED, i, OverlayTexture.NO_OVERLAY, matrixStack, multiBufferSource, blockEntity.getLevel(), 0);
            matrixStack.popPose();
        }

        if (blockEntity.processTime > 0) {
            matrixStack.pushPose();
            if (blockEntity.processTime < RenderUtils.ANIMATION_TIME) {
                var progression = (blockEntity.processTime * 1.5f) / (RenderUtils.ANIMATION_TIME * 1.5f);
                var easedInOut = RenderUtils.easeInOut(progression);
                matrixStack.translate(0.5D, 0.65D + easedInOut / 2, 0.5D);
                matrixStack.mulPose(Axis.XP.rotationDegrees( 90 + easedInOut * -90));
            } else {
                matrixStack.translate(0.5D, 1.15D, 0.5D);
                matrixStack.mulPose(Axis.YP.rotationDegrees((blockEntity.processTime - 40) % 360));
            }
            matrixStack.scale(0.55f, 0.55f, 0.55f);
            itemRenderer.renderStatic(blockEntity.getItem(0), ItemDisplayContext.FIXED, i, OverlayTexture.NO_OVERLAY, matrixStack, multiBufferSource, blockEntity.getLevel(), 0);
            matrixStack.popPose();
        }
    }
}
