package com.genymobile.scrcpy.control;

import java.io.IOException;

/** Observe boot-neutral discovery without opening or writing the digitizer. */
final class BooxPenInitialization {
    interface Probe {
        String read() throws IOException, InterruptedException;
    }

    interface Pause {
        void sleep(long millis) throws InterruptedException;
    }

    private BooxPenInitialization() {
    }

    static BooxDevice awaitIdle(Probe probe, Runnable notice, Pause pause) throws IOException, InterruptedException {
        BooxDevice initial = BooxDevice.snapshot(probe.read());
        if (initial.isIdle()) {
            return initial;
        }
        if (initial.x != 0 || initial.y != 0 || initial.pressure != 0 || initial.distance != 0) {
            throw new IllegalArgumentException("BOOX digitizer is active, not boot-neutral");
        }
        notice.run();
        while (true) {
            pause.sleep(1000);
            BooxDevice current = BooxDevice.snapshot(probe.read());
            if (!initial.sameDevice(current)) {
                throw new IllegalArgumentException("BOOX digitizer changed while waiting for initialization");
            }
            if (current.isIdle()) {
                return current;
            }
        }
    }
}
