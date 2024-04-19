package earth.terrarium.spirit.api.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import earth.terrarium.spirit.api.rituals.results.RitualResult;
import earth.terrarium.spirit.api.rituals.results.impl.ItemResult;
import earth.terrarium.spirit.client.renderer.utils.RenderUtils;
import earth.terrarium.spirit.common.entity.SoulReceptacle;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;

@FunctionalInterface
public interface ReceptacleRenderer {
    ReceptacleRenderer ITEM_RENDERER = (context, entity, result, partialTick, poseStack, buffer, packedLight) -> {
        if (!(result instanceof ItemResult itemResult)) return;
        var degrees = entity.getProcessTime() - RenderUtils.ANIMATION_TIME;
        if (degrees + RenderUtils.ANIMATION_TIME < RenderUtils.ANIMATION_TIME) return;
        poseStack.pushPose();
        var oldTick = Math.max(entity.getProcessTime() - 1, 0);
        poseStack.translate(0, 0.5D, 0);
        double angle = (2 * Math.PI * Mth.lerp(partialTick, oldTick, degrees)) / entity.getRecipeDuration();
        double x = Math.cos(angle) * 0.2;
        double z = Math.sin(angle) * 0.2;

        int alpha = (int) (Math.sin(entity.getProcessTime() / 4.0) * 165);
        TranslucentItemRenderTypeBuffer buffer1 = new TranslucentItemRenderTypeBuffer(buffer, alpha);

        poseStack.translate((Math.random() - 0.5) * 0.1, (Math.random() - 0.5) * 0.1, 0);

        context.getItemRenderer().renderStatic(itemResult.item(), ItemDisplayContext.GROUND, packedLight, OverlayTexture.NO_OVERLAY, poseStack, buffer1, entity.level(), 0);

        if (alpha > 50 && Math.random() < 0.3) {
            entity.level().addParticle(ParticleTypes.CRIT, entity.position().x() + (Math.random() - 0.5) * .2, entity.position().y() + 0.5, entity.position().z() + (Math.random() - 0.5) * .2, (Math.random() - 0.5) * 0.2, (Math.random() - 0.5) * 0.2, (Math.random() - 0.5) * 0.2);
        }

        poseStack.popPose();
    };

    ReceptacleRenderer ENTITY_RENDERER = (context, receptacle, result, g, poseStack, multiBufferSource, i) -> {
        var entity = receptacle.getOrCreateEntityResult();
        var degrees = receptacle.getProcessTime() - RenderUtils.ANIMATION_TIME;
        if (degrees + RenderUtils.ANIMATION_TIME < RenderUtils.ANIMATION_TIME) return;
        entity.tickCount = receptacle.getProcessTime();
        entity.tick();
        float scale = 0.75F;
        float h = Math.max(entity.getBbWidth(), entity.getBbHeight());
        if ((double) h > 1.0D) {
            scale /= h;
        }

        poseStack.pushPose();
        poseStack.translate(0.5D, .75D, 0.5D);
        var oldTick = Math.max(degrees - 1, 0);
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(g, oldTick, degrees) % 360));
        poseStack.scale(scale, scale, scale);
        poseStack.translate(0, Math.sin(degrees * .1) * 0.05 + 0.05,0);
        context.getEntityRenderDispatcher().render(entity, 0.0D, 0.0D, 0.0D, 0.0F, g, poseStack, multiBufferSource, i);
        poseStack.popPose();
    };

    void render(EntityRendererProvider.Context context, SoulReceptacle entity, RitualResult<?> result, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight);
}
