package wtf.silhouette.shaeware.utils;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class Stopwatch {

    private long lastNS = System.nanoTime();

    public boolean hasTimeElapsed(long time, TimeUnit unit) {
        return hasTimeElapsed(time, unit, false);
    }

    public boolean hasTimeElapsed(long time, @NotNull TimeUnit unit, boolean reset) {
        long elapsedNS = System.nanoTime() - lastNS;
        long timeoutNS = unit.toNanos(time);

        if (elapsedNS >= timeoutNS) {
            if (reset) {
                reset();
            }
            return true;
        }
        return false;
    }

    public void reset() {
        lastNS = System.nanoTime();
    }

    public long getTime(@NotNull TimeUnit unit) {
        long elapsedNS = System.nanoTime() - lastNS;
        return unit.convert(elapsedNS, TimeUnit.NANOSECONDS);
    }
}