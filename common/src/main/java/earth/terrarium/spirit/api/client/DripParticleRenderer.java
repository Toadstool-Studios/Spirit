package earth.terrarium.spirit.api.client;

import earth.terrarium.botarium.common.fluid.base.FluidHolder;
import earth.terrarium.botarium.common.fluid.utils.ClientFluidHooks;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;

public class DripParticleRenderer extends TextureSheetParticle {
    private final FluidHolder type;
    private final double startingY;
    public final float u0, u1, v0, v1;

    public DripParticleRenderer(DripParticleOptions particleOptions, ClientLevel clientLevel, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        super(clientLevel, x, y, z);
        this.setSize(0.01F, 0.01F);
        this.startingY = y;
        this.gravity = 0.06F;
        this.type = particleOptions.fluid();
        this.setSprite(ClientFluidHooks.getFluidSprite(this.type));

        this.rCol = 0.8F;
        this.gCol = 0.8F;
        this.bCol = 0.8F;
        this.multiplyColor(ClientFluidHooks.getFluidColor(type));
        var width = (sprite.getU1() - sprite.getU0()) / 8.0F;
        var height = (sprite.getV1() - sprite.getV0()) / 8.0F;
        float xOffset = (float) (width * Math.random());
        float yOffset = (float) (height * Math.random());
        u0 = sprite.getU0() + xOffset;
        u1 = sprite.getU0() + xOffset + width;
        v0 = sprite.getV0() + yOffset;
        v1 = sprite.getV0() + yOffset + height;
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    protected void multiplyColor(int color) {
        this.rCol *= (float) (color >> 16 & 255) / 255.0F;
        this.gCol *= (float) (color >> 8 & 255) / 255.0F;
        this.bCol *= (float) (color & 255) / 255.0F;
    }

    @Override
    public int getLightColor(float partialTick) {
        int brightnessForRender = super.getLightColor(partialTick);
        int skyLight = brightnessForRender >> 20;
        int blockLight = (brightnessForRender >> 4) & 0xf;
        blockLight = Math.max(blockLight, ClientFluidHooks.getFluidLightLevel(type));
        return (skyLight << 20) | (blockLight << 4);
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        this.preMoveUpdate();
        if (!this.removed) {
            this.yd -= this.gravity;
            this.move(this.xd, this.yd, this.zd);
            if (!this.removed) {
                this.xd *= 0.9800000190734863;
                this.yd *= 0.9800000190734863;
                this.zd *= 0.9800000190734863;
                if (this.y > this.startingY + 1.5D) {
                    this.remove();
                }
            }
        }
    }

    protected void preMoveUpdate() {
        if (this.lifetime-- <= 0) {
            this.remove();
        }
    }

    @Override
    protected float getU0() {
        return u0;
    }

    @Override
    protected float getU1() {
        return u1;
    }

    @Override
    protected float getV0() {
        return v0;
    }

    @Override
    protected float getV1() {
        return v1;
    }
}
