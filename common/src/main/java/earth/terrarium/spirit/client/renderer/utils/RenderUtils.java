package earth.terrarium.spirit.client.renderer.utils;

public class RenderUtils {
    public static final int ANIMATION_TIME = 20;

    public static float easeInOut(float t) {
        return t < 0.5 ? 2 * t * t : -1 + (4 - 2 * t) * t;
    }
}
