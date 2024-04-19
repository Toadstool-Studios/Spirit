package earth.terrarium.spirit.common.registry;

import earth.terrarium.spirit.api.abilities.ColorPalette;
import earth.terrarium.spirit.api.abilities.armor.ArmorAbility;
import earth.terrarium.spirit.api.abilities.armor.ArmorAbilityManager;
import earth.terrarium.spirit.api.abilities.tool.ToolAbility;
import earth.terrarium.spirit.api.abilities.tool.ToolAbilityManager;
import earth.terrarium.spirit.common.abilities.armor.*;
import earth.terrarium.spirit.common.abilities.tool.LifestealAbility;

import javax.tools.Tool;

public class SpiritAbilities {
    //please do not register "spirit:blank" armor or tool abilities, those are already registered <- note for GitHub Copilot
    public static final ArmorAbility DRAGON = ArmorAbilityManager.registerAbility("spirit:dragon", new DragonWingsAbility());
    public static final ArmorAbility STRIDER = ArmorAbilityManager.registerAbility("spirit:strider", new LavaWalkingAbility());
    public static final ArmorAbility BLAZING_HEIGHTS = ArmorAbilityManager.registerAbility("spirit:blazing_heights", new BlazingHeightsAbility());
    public static final ArmorAbility BOUNCE = ArmorAbilityManager.registerAbility("spirit:bounce", new BounceAbility());
    public static final ArmorAbility BUILT_DIFFERENT = ArmorAbilityManager.registerAbility("spirit:built_different", new BuiltDifferentAbility());
    public static final ArmorAbility POCKET_ARSENAL = ArmorAbilityManager.registerAbility("spirit:pocket_arsenal", new PocketArsenalAbility());
    public static final ArmorAbility AWEARABLE = ArmorAbilityManager.registerAbility("spirit:awearable", () -> new ColorPalette(0x00304E));

    public static final ToolAbility LIFESTEAL = ToolAbilityManager.registerAbility("spirit:lifesteal", new LifestealAbility());

    public static void init() {
    }
}
