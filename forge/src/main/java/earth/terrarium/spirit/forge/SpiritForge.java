package earth.terrarium.spirit.forge;

import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import earth.terrarium.spirit.Spirit;
import earth.terrarium.spirit.api.abilities.armor.BlankArmorAbility;
import earth.terrarium.spirit.api.abilities.tool.BlankToolAbility;
import earth.terrarium.spirit.api.abilities.tool.ToolAbility;
import earth.terrarium.spirit.api.abilities.tool.ToolAbilityManager;
import earth.terrarium.spirit.client.forge.SpiritClientForge;
import earth.terrarium.spirit.common.handlers.MobCrystalHandler;
import earth.terrarium.spirit.common.handlers.SoulAbsorptionHandler;
import earth.terrarium.spirit.api.abilities.armor.ArmorAbility;
import earth.terrarium.spirit.api.abilities.armor.ArmorAbilityManager;
import earth.terrarium.spirit.common.item.armor.SoulSteelArmor;
import earth.terrarium.spirit.common.registry.SpiritAbilities;
import earth.terrarium.spirit.common.registry.SpiritItems;
import earth.terrarium.spirit.common.util.AbilityUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryBuilder;

@Mod(Spirit.MODID)
public class SpiritForge {
    public SpiritForge() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        Spirit.init();
        bus.addListener(SpiritForge::commonSetup);
        MinecraftForge.EVENT_BUS.addListener(this::onItemUse);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, this::afterDeath);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> SpiritClientForge::init);
        bus.addListener((BuildCreativeModeTabContentsEvent event) -> {
            if (event.getTab() == SpiritItems.SPIRIT_TAB.get()) SpiritItems.ITEMS.stream().map(RegistryEntry::get).forEach(event::accept);
        });
        bus.addListener((LivingEquipmentChangeEvent event) -> {
            if (event.getEntity().level().isClientSide()) return;
            if (event.getEntity() instanceof Player player) {
                if (event.getFrom().getItem() instanceof SoulSteelArmor) {
                    AbilityUtils.onArmorUnequip(player, event.getSlot(), event.getFrom());
                }
                if (event.getTo().getItem() instanceof SoulSteelArmor) {
                    AbilityUtils.onArmorEquip(player, event.getSlot(), event.getTo());
                }
            }
        });
        bus.addListener((LivingDamageEvent event) -> AbilityUtils.onEntityHit(event.getEntity(), event.getSource(), event.getAmount()));
    }

    public static void commonSetup(FMLCommonSetupEvent event) {
        Spirit.postInit();
    }

    public void afterDeath(LivingDeathEvent event) {
        if (!event.isCanceled()) {
            SoulAbsorptionHandler.onEntityDeath(event.getEntity(), event.getSource());
        }
    }

    public void onItemUse(PlayerInteractEvent.EntityInteract event) {
        if (!event.isCanceled()) {
            MobCrystalHandler.mobInteraction(event.getTarget(), event.getEntity(), event.getHand());
        }
    }
}