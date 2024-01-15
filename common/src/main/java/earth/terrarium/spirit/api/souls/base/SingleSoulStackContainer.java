package earth.terrarium.spirit.api.souls.base;

import earth.terrarium.spirit.api.souls.InteractionMode;
import earth.terrarium.spirit.api.souls.stack.SoulStack;

import java.util.List;

public abstract class SingleSoulStackContainer implements SoulContainer {
    @Override
    public List<SoulStack> getSouls() {
        return List.of(getSoulStack(0));
    }

    @Override
    public int insert(SoulStack stack, InteractionMode mode) {
        return insertIntoSlot(stack, 0, mode);
    }

    @Override
    public SoulStack extract(SoulStack stack, InteractionMode mode) {
        return extractFromSlot(stack, 0, mode);
    }
}
