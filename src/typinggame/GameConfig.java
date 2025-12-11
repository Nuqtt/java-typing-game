package typinggame;

/**
 * Central place for tweakable gameplay and rendering constants.
 */
public final class GameConfig {
    public static final int WIDTH = 800;
    public static final int HEIGHT = 300;
    public static final int GROUND_Y = 220;

    public static final int TIMER_DELAY_MS = 16;            // ~60 FPS
    public static final int INITIAL_SPAWN_INTERVAL = 120;   // frames
    public static final int MIN_SPAWN_INTERVAL = 60;        // frames
    public static final int SPAWN_STEP = 2;                 // frames per difficulty bump
    public static final int DIFFICULTY_INTERVAL_MS = 5000;  // how often to speed up

    public static final double INITIAL_SPEED = 4.0;
    public static final double SPEED_INCREMENT = 0.1;

    private GameConfig() {
        // no instances
    }
}
