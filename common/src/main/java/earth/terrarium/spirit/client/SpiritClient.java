package earth.terrarium.spirit.client;

import dev.architectury.injectables.annotations.ExpectPlatform;
import earth.terrarium.spirit.Spirit;
import earth.terrarium.spirit.api.abilities.armor.ArmorAbility;
import earth.terrarium.spirit.api.abilities.tool.ToolAbility;
import earth.terrarium.spirit.api.client.DripParticleRenderer;
import earth.terrarium.spirit.api.client.ReceptacleRenderer;
import earth.terrarium.spirit.api.client.SpiritClientApi;
import earth.terrarium.spirit.api.rituals.results.impl.EntityResult;
import earth.terrarium.spirit.api.rituals.results.impl.ItemResult;
import earth.terrarium.spirit.client.renderer.armor.ArmorRenderers;
import earth.terrarium.spirit.client.renderer.armor.SoulSteelArmorModel;
import earth.terrarium.spirit.client.renderer.block.PedestalRenderer;
import earth.terrarium.spirit.client.renderer.block.SoulBasinRenderer;
import earth.terrarium.spirit.client.renderer.block.SoulCageRenderer;
import earth.terrarium.spirit.client.renderer.block.SoulCrystalRenderer;
import earth.terrarium.spirit.common.handlers.MobCrystalHandler;
import earth.terrarium.spirit.common.handlers.SoulAbilityHandler;
import earth.terrarium.spirit.common.item.armor.SoulSteelArmor;
import earth.terrarium.spirit.common.item.tools.SoulSteelTool;
import earth.terrarium.spirit.common.registry.SpiritBlockEntities;
import earth.terrarium.spirit.common.registry.SpiritBlocks;
import earth.terrarium.spirit.common.registry.SpiritItems;
import earth.terrarium.spirit.common.registry.SpiritParticles;
import earth.terrarium.spirit.common.util.ClientUtils;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.DripParticle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluids;

import java.util.function.Supplier;

public class SpiritClient {
    public static final ItemColor ARMOR_COLOR = (itemStack, i) -> {
        for (int j = 1; j <= 3; j++) {
            if (itemStack.getItem() instanceof SoulSteelArmor armor && i == j) {
                ArmorAbility ability = armor.getAbility(itemStack);
                if (ability != null) {
                    return ability.getColor().asArray()[j - 1];
                }
            }
        }
        return -1;
    };

    public static final ItemColor TOOL_COLOR = (itemStack, i) -> {
        for (int j = 1; j <= 3; j++) {
            if (itemStack.getItem() instanceof SoulSteelTool armor && i == j) {
                ToolAbility ability = armor.getAbility(itemStack);
                if (ability != null) {
                    return ability.getColor().asArray()[j - 1];
                }
            }
        }
        return -1;
    };

    public static void init() {
        registerItemProperties(SpiritItems.MOB_CRYSTAL.get(), new ResourceLocation(Spirit.MODID, "mob"), (itemStack, clientLevel, livingEntity, i) -> MobCrystalHandler.mobCrystalProperty(itemStack));
        registerItemProperties(SpiritItems.SOUL_STEEL_HELMET.get(), new ResourceLocation(Spirit.MODID, "ability"), (itemStack, clientLevel, livingEntity, i) -> SoulAbilityHandler.soulArmorProperty(itemStack));
        registerItemProperties(SpiritItems.SOUL_STEEL_CHESTPLATE.get(), new ResourceLocation(Spirit.MODID, "ability"), (itemStack, clientLevel, livingEntity, i) -> SoulAbilityHandler.soulArmorProperty(itemStack));
        registerItemProperties(SpiritItems.SOUL_STEEL_LEGGINGS.get(), new ResourceLocation(Spirit.MODID, "ability"), (itemStack, clientLevel, livingEntity, i) -> SoulAbilityHandler.soulArmorProperty(itemStack));
        registerItemProperties(SpiritItems.SOUL_STEEL_BOOTS.get(), new ResourceLocation(Spirit.MODID, "ability"), (itemStack, clientLevel, livingEntity, i) -> SoulAbilityHandler.soulArmorProperty(itemStack));
        registerItemProperties(SpiritItems.SCYTHE.get(), new ResourceLocation(Spirit.MODID, "ability"), (itemStack, clientLevel, livingEntity, i) -> SoulAbilityHandler.toolAbilityProperty(itemStack));
        registerItemProperties(SpiritItems.SOUL_STEEL_HAMMER.get(), new ResourceLocation(Spirit.MODID, "ability"), (itemStack, clientLevel, livingEntity, i) -> SoulAbilityHandler.toolAbilityProperty(itemStack));
        registerItemProperties(SpiritItems.SOUL_STEEL_BATTLEAXE.get(), new ResourceLocation(Spirit.MODID, "ability"), (itemStack, clientLevel, livingEntity, i) -> SoulAbilityHandler.toolAbilityProperty(itemStack));
        registerItemProperties(SpiritItems.SOUL_STEEL_EXCAVATOR.get(), new ResourceLocation(Spirit.MODID, "ability"), (itemStack, clientLevel, livingEntity, i) -> SoulAbilityHandler.toolAbilityProperty(itemStack));
        registerBlockEntityRenderers(SpiritBlockEntities.PEDESTAL.get(), PedestalRenderer::new);
        registerBlockEntityRenderers(SpiritBlockEntities.SOUL_BASIN.get(), SoulBasinRenderer::new);
        registerBlockEntityRenderers(SpiritBlockEntities.SOUL_CRYSTAL.get(), SoulCrystalRenderer::new);
        registerBlockEntityRenderers(SpiritBlockEntities.SOUL_CAGE.get(), SoulCageRenderer::new);
        registerRenderLayer(SpiritBlocks.SOUL_GLASS.get(), RenderType.translucent());
        registerRenderLayer(SpiritBlocks.RAGING_SOUL_FIRE.get(), RenderType.cutout());
        registerRenderLayer(SpiritBlocks.SOUL_CRYSTAL.get(), RenderType.cutout());
        registerRenderLayer(SpiritBlocks.SOUL_CAGE.get(), RenderType.cutout());
        SpiritClientApi.registerReceptacleRenderer(ItemResult.class, ReceptacleRenderer.ITEM_RENDERER);
        SpiritClientApi.registerReceptacleRenderer(EntityResult.class, ReceptacleRenderer.ENTITY_RENDERER);
        ArmorRenderers.init();
        ClientUtils.init();
    }

    public static void registerEntityLayers(LayerDefinitionRegistry registry) {
        registry.register(SoulSteelArmorModel.LAYER_LOCATION, SoulSteelArmorModel::createBodyLayer);
    }

    @ExpectPlatform
    public static <E extends BlockEntity> void registerBlockEntityRenderers(BlockEntityType<E> blockEntityType, BlockEntityRendererProvider<? super E> blockEntityRendererFactory) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void registerItemProperties(Item item, ResourceLocation resourceLocation, ClampedItemPropertyFunction clampedItemPropertyFunction) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void registerRenderLayer(Block block, RenderType type) {
        throw new AssertionError();
    }

    public static void registerParticleRenderers(ParticleRendererRegistry registry) {
        registry.register(SpiritParticles.DRIP.get(), DripParticleRenderer::new);
    }

    public static abstract class LayerDefinitionRegistry {
        public abstract void register(ModelLayerLocation location, Supplier<LayerDefinition> definition);
    }

    @FunctionalInterface
    public interface ParticleRendererRegistry {
        <T extends ParticleOptions> void register(ParticleType<T> type, ParticleProvider.Sprite<T> provider);
    }
}
