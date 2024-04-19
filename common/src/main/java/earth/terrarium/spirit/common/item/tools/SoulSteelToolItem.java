package earth.terrarium.spirit.common.item.tools;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import dev.architectury.injectables.annotations.PlatformOnly;
import earth.terrarium.spirit.api.abilities.tool.ToolAbility;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class SoulSteelToolItem extends DiggerItem implements SoulSteelTool {
    public SoulSteelToolItem(TagKey<Block> tagKey, Properties properties) {
        super(3, 4, SoulSteelItemTier.INSTANCE, tagKey, properties);
    }

    @PlatformOnly("fabric")
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(ItemStack stack, EquipmentSlot slot) {
        return abilityAttributes(slot, stack);
    }

    @PlatformOnly("forge")
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        return abilityAttributes(slot, stack);
    }

    public Multimap<Attribute, AttributeModifier> abilityAttributes(EquipmentSlot slot, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> base = HashMultimap.create(super.getDefaultAttributeModifiers(slot));
        var ability = getAbility(stack);
        if(ability != null && slot == EquipmentSlot.MAINHAND) {
            ability.modifyAttributes(stack, base);
        }
        return base;
    }

    @Override
    public void inventoryTick(ItemStack itemStack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(itemStack, level, entity, slotId, isSelected);
        if (entity instanceof Player player && (player.getMainHandItem() == itemStack || player.getOffhandItem() == itemStack)) {
            ToolAbility ability = getAbility(itemStack);
            if (ability != null) {
                ability.whileHeld(player, itemStack);
            }
        }
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity entity) {
        boolean b = super.mineBlock(stack, level, state, pos, entity);
        if (b) {
            var ability = getAbility(stack);
            if(ability != null && entity instanceof Player player) {
                ability.onBlockBreak(stack, level, state, pos, player);
            }
        }
        return b;
    }
}
