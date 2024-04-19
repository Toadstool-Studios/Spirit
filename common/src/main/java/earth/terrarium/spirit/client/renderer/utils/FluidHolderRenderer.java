package earth.terrarium.spirit.client.renderer.utils;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.architectury.injectables.annotations.ExpectPlatform;
import earth.terrarium.botarium.common.fluid.base.FluidHolder;
import earth.terrarium.botarium.common.fluid.utils.ClientFluidHooks;
import earth.terrarium.spirit.Spirit;
import earth.terrarium.spirit.common.util.ClientUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

public class FluidHolderRenderer {
    public static void renderFluid(FluidHolder fluid, long maxAmount, PoseStack stack, MultiBufferSource source, int light) {
        float percent = (float) fluid.getFluidAmount() / (float) maxAmount;
        if (percent > 0) {
            stack.pushPose();
            int color = ClientFluidHooks.getFluidColor(fluid);
            TextureAtlasSprite sprite = ClientFluidHooks.getFluidSprite(fluid);
            RenderType type = Minecraft.useShaderTransparency() ? RenderType.translucentMovingBlock() : RenderType.translucentNoCrumbling();
            createQuad(stack, source.getBuffer(type), Mth.lerp(percent, (float)bounds().minY, (float)bounds().maxY), light, color, sprite);
            stack.popPose();
        }
    }

    private static void createQuad(PoseStack stack, VertexConsumer builder, float verticalOffset, int light, int color, TextureAtlasSprite texture) {
        Matrix4f matrix4f = stack.last().pose();
        float x1 = (float) bounds().minX;
        float x2 = (float) bounds().maxX;
        float z1 = (float) bounds().minZ;
        float z2 = (float) bounds().maxZ;
        int r = FastColor.ARGB32.red(color);
        int g = FastColor.ARGB32.green(color);
        int b = FastColor.ARGB32.blue(color);
        int a = FastColor.ARGB32.alpha(color);
        builder.vertex(matrix4f, x1, verticalOffset, z2).color(r, g, b, a).uv(texture.getU(0), texture.getV(0)).uv2(light).normal(0, 0F, 0).endVertex();
        builder.vertex(matrix4f, x2, verticalOffset, z2).color(r, g, b, a).uv(texture.getU(16), texture.getV(0)).uv2(light).normal(0, 0F, 0).endVertex();
        builder.vertex(matrix4f, x2, verticalOffset, z1).color(r, g, b, a).uv(texture.getU(16), texture.getV(16)).uv2(light).normal(0, 0F, 0).endVertex();
        builder.vertex(matrix4f, x1, verticalOffset, z1).color(r, g, b, a).uv(texture.getU(0), texture.getV(16)).uv2(light).normal(0, 0F, 0).endVertex();
    }

    public static void renderFlorb(AABB bounds, FluidHolder fluid, PoseStack stack, MultiBufferSource source, int light) {
        stack.pushPose();
        int color = ClientFluidHooks.getFluidColor(fluid);
        TextureAtlasSprite texture = ClientFluidHooks.getFluidSprite(fluid);
        RenderType type = Minecraft.useShaderTransparency() ? RenderType.translucentMovingBlock() : RenderType.translucentNoCrumbling();
        VertexConsumer buffer = source.getBuffer(type);
        Matrix4f matrix4f = stack.last().pose();
        int r = FastColor.ARGB32.red(color);
        int g = FastColor.ARGB32.green(color);
        int b = FastColor.ARGB32.blue(color);
        int a = FastColor.ARGB32.alpha(color);

        double uvMinX = (16 - bounds.getXsize() * 32) / 2f;
        double uvMinY = (16 - bounds.getYsize() * 32) / 2f;
        double uvMinZ = (16 - bounds.getZsize() * 32) / 2f;
        double uvMaxX = 16 - (16 - bounds.getXsize() * 32) / 2f;
        double uvMaxY = 16 - (16 - bounds.getYsize() * 32) / 2f;
        double uvMaxZ = 16 - (16 - bounds.getZsize() * 32) / 2f;

        //Front
        buffer.vertex(matrix4f, (float) bounds.minX, (float) bounds.maxY, (float) bounds.minZ).color(r, g, b, a).uv(texture.getU(uvMinX), texture.getV(uvMaxY)).uv2(light).normal(0, 0F, 0).endVertex();
        buffer.vertex(matrix4f, (float) bounds.maxX, (float) bounds.maxY, (float) bounds.minZ).color(r, g, b, a).uv(texture.getU(uvMaxX), texture.getV(uvMaxY)).uv2(light).normal(0, 0F, 0).endVertex();
        buffer.vertex(matrix4f, (float) bounds.maxX, (float) bounds.minY, (float) bounds.minZ).color(r, g, b, a).uv(texture.getU(uvMaxX), texture.getV(uvMinY)).uv2(light).normal(0, 0F, 0).endVertex();
        buffer.vertex(matrix4f, (float) bounds.minX, (float) bounds.minY, (float) bounds.minZ).color(r, g, b, a).uv(texture.getU(uvMinX), texture.getV(uvMinY)).uv2(light).normal(0, 0F, 0).endVertex();

        //Back
        buffer.vertex(matrix4f, (float) bounds.maxX, (float) bounds.maxY, (float) bounds.maxZ).color(r, g, b, a).uv(texture.getU(uvMaxX), texture.getV(uvMaxY)).uv2(light).normal(0, 0F, 0).endVertex();
        buffer.vertex(matrix4f, (float) bounds.minX, (float) bounds.maxY, (float) bounds.maxZ).color(r, g, b, a).uv(texture.getU(uvMinX), texture.getV(uvMaxY)).uv2(light).normal(0, 0F, 0).endVertex();
        buffer.vertex(matrix4f, (float) bounds.minX, (float) bounds.minY, (float) bounds.maxZ).color(r, g, b, a).uv(texture.getU(uvMinX), texture.getV(uvMinY)).uv2(light).normal(0, 0F, 0).endVertex();
        buffer.vertex(matrix4f, (float) bounds.maxX, (float) bounds.minY, (float) bounds.maxZ).color(r, g, b, a).uv(texture.getU(uvMaxX), texture.getV(uvMinY)).uv2(light).normal(0, 0F, 0).endVertex();

        //Top
        buffer.vertex(matrix4f, (float) bounds.minX, (float) bounds.maxY, (float) bounds.maxZ).color(r, g, b, a).uv(texture.getU(uvMinX), texture.getV(uvMaxZ)).uv2(light).normal(0, 0F, 0).endVertex();
        buffer.vertex(matrix4f, (float) bounds.maxX, (float) bounds.maxY, (float) bounds.maxZ).color(r, g, b, a).uv(texture.getU(uvMaxX), texture.getV(uvMaxZ)).uv2(light).normal(0, 0F, 0).endVertex();
        buffer.vertex(matrix4f, (float) bounds.maxX, (float) bounds.maxY, (float) bounds.minZ).color(r, g, b, a).uv(texture.getU(uvMaxX), texture.getV(uvMinZ)).uv2(light).normal(0, 0F, 0).endVertex();
        buffer.vertex(matrix4f, (float) bounds.minX, (float) bounds.maxY, (float) bounds.minZ).color(r, g, b, a).uv(texture.getU(uvMinX), texture.getV(uvMinZ)).uv2(light).normal(0, 0F, 0).endVertex();

        //Bottom
        buffer.vertex(matrix4f, (float) bounds.minX, (float) bounds.minY, (float) bounds.minZ).color(r, g, b, a).uv(texture.getU(uvMinX), texture.getV(uvMinZ)).uv2(light).normal(0, 0F, 0).endVertex();
        buffer.vertex(matrix4f, (float) bounds.maxX, (float) bounds.minY, (float) bounds.minZ).color(r, g, b, a).uv(texture.getU(uvMaxX), texture.getV(uvMinZ)).uv2(light).normal(0, 0F, 0).endVertex();
        buffer.vertex(matrix4f, (float) bounds.maxX, (float) bounds.minY, (float) bounds.maxZ).color(r, g, b, a).uv(texture.getU(uvMaxX), texture.getV(uvMaxZ)).uv2(light).normal(0, 0F, 0).endVertex();
        buffer.vertex(matrix4f, (float) bounds.minX, (float) bounds.minY, (float) bounds.maxZ).color(r, g, b, a).uv(texture.getU(uvMinX), texture.getV(uvMaxZ)).uv2(light).normal(0, 0F, 0).endVertex();

        //Left
        buffer.vertex(matrix4f, (float) bounds.minX, (float) bounds.maxY, (float) bounds.maxZ).color(r, g, b, a).uv(texture.getU(uvMaxZ), texture.getV(uvMaxY)).uv2(light).normal(0, 0F, 0).endVertex();
        buffer.vertex(matrix4f, (float) bounds.minX, (float) bounds.maxY, (float) bounds.minZ).color(r, g, b, a).uv(texture.getU(uvMinZ), texture.getV(uvMaxY)).uv2(light).normal(0, 0F, 0).endVertex();
        buffer.vertex(matrix4f, (float) bounds.minX, (float) bounds.minY, (float) bounds.minZ).color(r, g, b, a).uv(texture.getU(uvMinZ), texture.getV(uvMinY)).uv2(light).normal(0, 0F, 0).endVertex();
        buffer.vertex(matrix4f, (float) bounds.minX, (float) bounds.minY, (float) bounds.maxZ).color(r, g, b, a).uv(texture.getU(uvMaxZ), texture.getV(uvMinY)).uv2(light).normal(0, 0F, 0).endVertex();

        //Right
        buffer.vertex(matrix4f, (float) bounds.maxX, (float) bounds.maxY, (float) bounds.minZ).color(r, g, b, a).uv(texture.getU(uvMinZ), texture.getV(uvMaxY)).uv2(light).normal(0, 0F, 0).endVertex();
        buffer.vertex(matrix4f, (float) bounds.maxX, (float) bounds.maxY, (float) bounds.maxZ).color(r, g, b, a).uv(texture.getU(uvMaxZ), texture.getV(uvMaxY)).uv2(light).normal(0, 0F, 0).endVertex();
        buffer.vertex(matrix4f, (float) bounds.maxX, (float) bounds.minY, (float) bounds.maxZ).color(r, g, b, a).uv(texture.getU(uvMaxZ), texture.getV(uvMinY)).uv2(light).normal(0, 0F, 0).endVertex();
        buffer.vertex(matrix4f, (float) bounds.maxX, (float) bounds.minY, (float) bounds.minZ).color(r, g, b, a).uv(texture.getU(uvMinZ), texture.getV(uvMinY)).uv2(light).normal(0, 0F, 0).endVertex();
        stack.popPose();
    }

    public static @NotNull AABB bounds() {
        return new AABB(0.25, 0.6875, 0.25, 0.75, 0.875, 0.75);
    }
}