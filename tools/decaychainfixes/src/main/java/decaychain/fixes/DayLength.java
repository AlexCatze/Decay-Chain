package decaychain.fixes;

/**
 * Decay Chain days are FACTOR times longer than vanilla (FACTOR x 20 minutes), so a new player has time to build a
 * shelter before the first night. The world clock advances only on every FACTOR-th tick, on the server
 * (mixin.DayLengthServerMixin) and on the client (mixin.client.DayLengthClientMixin) alike, so they stay in step and the
 * sun moves smoothly. Everything that reads the clock (day number, time of day, moon phase, hordes on the tenth night)
 * sees longer days; tick-based timers (crop growth, hunger, furnaces) keep their real-time speed.
 * SRP's own day counter is set to match in config/srparasites/SRParasites.cfg ("Day Tick Value" = FACTOR x 24000).
 */
public final class DayLength {
    public static final int FACTOR = 2;

    private DayLength() {}

    /** Whether the clock may advance on the tick with this total world time. */
    public static boolean advances(long totalWorldTime) {
        return totalWorldTime % FACTOR == 0;
    }
}
