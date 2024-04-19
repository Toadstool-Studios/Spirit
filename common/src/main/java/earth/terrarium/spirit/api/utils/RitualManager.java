package earth.terrarium.spirit.api.utils;

import earth.terrarium.spirit.api.rituals.components.RitualComponent;
import earth.terrarium.spirit.common.recipes.TransmutationRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RitualManager {
    private final Map<BlockPos, RitualComponent<?>> locationBasedComponents = new HashMap<>();
    private final List<RitualComponent<?>> nonLocationBasedComponents = new ArrayList<>();
    private final BlockPos centerPos;
    private final TransmutationRecipe recipe;

    private RitualManager(BlockPos centerPos, TransmutationRecipe recipe) {
        this.centerPos = centerPos;
        this.recipe = recipe;
    }

    @Nullable
    public static RitualManager of(ServerLevel level, BlockPos centerPos, TransmutationRecipe recipe) {
        RitualManager ritualManager = new RitualManager(centerPos, recipe);

        if (!ritualManager.mapComponents(level)) {
            return null;
        }

        return ritualManager;
    }

    private boolean mapComponents(ServerLevel level) {
        for (RitualComponent<?> input : recipe.inputs()) {
            boolean foundMatch = false;
            if (input.requiresSpecificPosition()) {
                for (BlockPos componentPos : BlockPos.betweenClosedStream(centerPos.offset(-3, 0, -3), centerPos.offset(3, 0, 3)).map(BlockPos::immutable).filter(pos -> !locationBasedComponents.containsKey(pos)).toList()) {
                    if (input.matches(recipe, level, componentPos.immutable(), centerPos)) {
                        locationBasedComponents.put(componentPos, input);
                        foundMatch = true;
                        break;
                    }
                }
                if (!foundMatch) {
                    locationBasedComponents.clear();
                    nonLocationBasedComponents.clear();
                    return false;
                }
            } else {
                if (input.matches(recipe, level, null, centerPos)) {
                    nonLocationBasedComponents.add(input);
                } else {
                    locationBasedComponents.clear();
                    nonLocationBasedComponents.clear();
                    return false;
                }
            }
        }
        return true;
    }

    public void beginRitual(ServerLevel level) {
        locationBasedComponents.forEach((pos, component) -> component.onRitualBegin(recipe, level, pos, centerPos));
        nonLocationBasedComponents.forEach(component -> component.onRitualBegin(recipe, level, null, centerPos));
    }

    public void abortRitual(ServerLevel level) {
        locationBasedComponents.forEach((pos, component) -> component.onRitualAbort(recipe, level, pos, centerPos));
        nonLocationBasedComponents.forEach(component -> component.onRitualAbort(recipe, level, null, centerPos));
        locationBasedComponents.clear();
        nonLocationBasedComponents.clear();
    }

    public boolean validateComponents(ServerLevel level) {
        for (Map.Entry<BlockPos, RitualComponent<?>> pair : locationBasedComponents.entrySet()) {
            if (!pair.getValue().matches(recipe, level, pair.getKey(), centerPos)) {
                return false;
            }
        }

        for (RitualComponent<?> component : nonLocationBasedComponents) {
            if (!component.matches(recipe, level, null, centerPos)) {
                return false;
            }
        }

        return true;
    }

    public void completeRitual(ServerLevel level) {
        locationBasedComponents.forEach((blockPos, ritualComponent) -> {
            level.sendParticles(ParticleTypes.SMOKE, blockPos.getX() + 0.5, blockPos.getY() + 1, blockPos.getZ() + 0.5, 10, 0.3, 0.3, 0.3, 0.01);
        });
        locationBasedComponents.forEach((pos, component) -> component.onRitualComplete(recipe, level, pos, centerPos));
        nonLocationBasedComponents.forEach(component -> component.onRitualComplete(recipe, level, null, centerPos));
        locationBasedComponents.clear();
        nonLocationBasedComponents.clear();
    }
}
