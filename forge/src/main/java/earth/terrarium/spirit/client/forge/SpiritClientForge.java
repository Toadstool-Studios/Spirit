package earth.terrarium.spirit.client.forge;

import earth.terrarium.spirit.Spirit;
import earth.terrarium.spirit.client.SpiritClient;
import earth.terrarium.spirit.client.entity.SoulReceptacleRenderer;
import earth.terrarium.spirit.common.registry.SpiritEntities;
import earth.terrarium.spirit.common.registry.SpiritItems;
import earth.terrarium.spirit.common.util.ClientUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = Spirit.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SpiritClientForge {

    public static void init() {
        SpiritClient.init();
        MinecraftForge.EVENT_BUS.addListener((TickEvent.ClientTickEvent event) -> {
            if (event.phase == TickEvent.Phase.START) {
                ClientUtils.onStartTick(Minecraft.getInstance());
            }
        });
    }

    @SubscribeEvent
    public void onRegisterLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        SpiritClient.registerEntityLayers(new SpiritClient.LayerDefinitionRegistry() {
            @Override
            public void register(ModelLayerLocation location, Supplier<LayerDefinition> definition) {
                event.registerLayerDefinition(location, definition);
            }
        });

    }

    @SubscribeEvent
    public static void registerColor(RegisterColorHandlersEvent.Item event) {
        event.register(SpiritClient.ARMOR_COLOR, SpiritItems.SOUL_STEEL_HELMET.get(), SpiritItems.SOUL_STEEL_CHESTPLATE.get(), SpiritItems.SOUL_STEEL_LEGGINGS.get(), SpiritItems.SOUL_STEEL_BOOTS.get());
        event.register(SpiritClient.TOOL_COLOR, SpiritItems.SOUL_STEEL_HAMMER.get(), SpiritItems.SOUL_STEEL_EXCAVATOR.get(), SpiritItems.SOUL_STEEL_BATTLEAXE.get());
    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(SpiritEntities.SOUL_RECEPTACLE.get(), SoulReceptacleRenderer::new);
    }

    @SubscribeEvent
    public static void registerParticleRenderers(RegisterParticleProvidersEvent event) {
        SpiritClient.registerParticleRenderers(event::registerSprite);
    }
}
