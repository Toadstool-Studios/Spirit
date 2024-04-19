package earth.terrarium.spirit.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import earth.terrarium.botarium.common.fluid.FluidConstants;
import earth.terrarium.botarium.common.fluid.base.FluidHolder;
import earth.terrarium.spirit.client.renderer.utils.FluidHolderRenderer;
import earth.terrarium.spirit.client.renderer.utils.RenderUtils;
import earth.terrarium.spirit.common.blockentity.SoulBasinBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

public class SoulBasinRenderer implements BlockEntityRenderer<SoulBasinBlockEntity> {
    public static final float UNIT = 1 / 16f;
    public static final int INITIAL_TIME = 12;
    public static final int OVERSHOOT = 16;

    public SoulBasinRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(SoulBasinBlockEntity blockEntity, float partialTicks, @NotNull PoseStack matrixStack, @NotNull MultiBufferSource multiBufferSource, int i, int j) {
        if (!blockEntity.hasLevel()) return;
        if (!blockEntity.getFluidContainer().isEmpty()) {
            float currentAge = blockEntity.age;
            float percent = (float) blockEntity.getFluidContainer().getFirstFluid().getFluidAmount() / (float) FluidConstants.fromMillibuckets(4000);
            FluidHolder fluid = blockEntity.getFluidContainer().getFluids().get(0);
            FluidHolderRenderer.renderFluid(blockEntity.hasRitual() ? fluid.copyWithAmount(fluid.getFluidAmount() - FluidConstants.getBucketAmount()) : fluid,
                    FluidConstants.fromMillibuckets(4000),
                    matrixStack,
                    multiBufferSource,
                    i);
            if (blockEntity.hasRitual()) {
                if (currentAge <= INITIAL_TIME) {
                    renderFlorbFrame(currentAge / INITIAL_TIME, Mth.lerp(percent, 11, 14), matrixStack, multiBufferSource, blockEntity, i, RenderingState.INITIAL_STATE);
                } else if (currentAge < OVERSHOOT) {
                    renderFlorbFrame((currentAge - INITIAL_TIME) / (OVERSHOOT - INITIAL_TIME), 17, matrixStack, multiBufferSource, blockEntity, i, RenderingState.OVERSHOOT_STATE);
                } else if (currentAge < RenderUtils.ANIMATION_TIME) {
                    renderFlorbFrame((currentAge - OVERSHOOT) / (RenderUtils.ANIMATION_TIME - OVERSHOOT), 18, matrixStack, multiBufferSource, blockEntity, i, RenderingState.BOUNCE_BACK_STATE);
                } else {
                    matrixStack.pushPose();
                    matrixStack.translate(0, 18 * UNIT, 0);
                    AABB florbBounds = new AABB(5 * UNIT, 0, 5 * UNIT, 11 * UNIT, 6 * UNIT, 11 * UNIT);
                    FluidHolderRenderer.renderFlorb(florbBounds, blockEntity.getFluidContainer().getFluids().get(0), matrixStack, multiBufferSource, i);
                    matrixStack.popPose();
                }
            }
        }
    }

    public void renderFlorbFrame(float timeFraction, float yTranslation, @NotNull PoseStack matrixStack, @NotNull MultiBufferSource multiBufferSource,
                                 SoulBasinBlockEntity blockEntity, int i, RenderingState renderingState) {
        var progression = RenderUtils.easeInOut(timeFraction);
        var min = Mth.lerp(progression, renderingState.minBegin() * UNIT, renderingState.minEnd() * UNIT);
        var max = Mth.lerp(progression, renderingState.maxBegin() * UNIT, renderingState.maxEnd() * UNIT);
        matrixStack.pushPose();
        matrixStack.translate(0, Mth.lerp(progression, yTranslation, renderingState.yTranslateEnd()) * UNIT, 0);
        AABB florbBounds = new AABB(min, 0, min, max, Mth.lerp(progression, renderingState.heightBegin(), renderingState.heightEnd()) * UNIT, max);
        FluidHolderRenderer.renderFlorb(florbBounds, blockEntity.getFluidContainer().getFluids().get(0), matrixStack, multiBufferSource, i);
        matrixStack.popPose();
    }

    public record RenderingState(float minBegin, float minEnd, float maxBegin, float maxEnd, float yTranslateEnd,
                                 float heightBegin, float heightEnd) {
        public static final RenderingState INITIAL_STATE = new RenderingState(4, 6, 12, 10, 17, 1, 8);
        public static final RenderingState OVERSHOOT_STATE = new RenderingState(6, 4, 10, 12, 18, 8, 4);
        public static final RenderingState BOUNCE_BACK_STATE = new RenderingState(4, 5, 12, 11, 18, 4, 6);
    }
}
