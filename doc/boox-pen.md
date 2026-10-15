# BOOX native pen input (experimental)

The downstream `--boox-pen=ROTATION` option connects SDL 3 pen input to the actual BOOX digitizer evdev stream. It reuses scrcpy's existing position/pressure control messages, reserving downstream pointer ID `-4` without adding a message type or field. Ordinary mouse and finger input continue through the Android SDK for UI navigation; they do not become raw pen strokes.

The option is disabled by default. It requires Android 10+, display 0, SDK mouse mode and exactly one idle, shell-writable `onyx_emp...Wacom` digitizer with valid X/Y/pressure/distance capabilities. It does not use root, add an APK/application, patch Notes or install a persistent service. A private `app_process` child from the same server archive owns the descriptor and survives parent termination long enough to release it, like scrcpy's existing cleanup process.

## Install and prepare

Install the maintained package with a full system upgrade:

    sudo pacman -Syu scrcpy-git
    pacman -Q scrcpy-git
    scrcpy --help

This conflicts with/replaces the distribution `scrcpy` package. The server is compiled from the same maintained checkout as the client; there is no upstream precompiled scrcpy server in this package.

Use a desktop session where SDL receives real pen events and pressure from the tablet/display driver. A mouse is not a pressure-capable substitute. SDL's synthetic pen-to-mouse/touch duplicates are disabled only in BOOX mode. Eraser and tilt are not implemented in this first candidate.

Before enabling input, use normal scrcpy/mouse navigation to create or select a disposable synthetic Notes notebook/page. Never test in an existing valuable note. Put the physical BOOX pen away, and do not use another writer or scrcpy session against the digitizer. The lock prevents other instances of this backend, not the physical stylus or unrelated tools.

## Calibrate explicitly

There is no guessed calibration. `ROTATION` is one of `0`, `90`, `180`, `270`: it maps the current unrotated scrcpy display coordinates into the raw digitizer axes, after scrcpy's own video/crop/window mapping. Normalized coordinate transforms are:

| Value | Raw X | Raw Y |
|---|---|---|
| 0 | x | y |
| 90 | y | 1-x |
| 180 | 1-x | 1-y |
| 270 | 1-y | x |

For example, after selecting the synthetic page:

    scrcpy --boox-pen=90 --no-audio

`90` is an example, not a verified calibration for every BOOX/firmware/orientation. Confirm alignment with a short central stroke, then small strokes in separate quadrants of the disposable page. If the mapping is reflected rather than rotated, stop: mirrored-axis calibration is not implemented. Do not rotate the device during a session; the backend closes on a detected display rotation change. Restart and recalibrate instead.

## Manual acceptance

1. Confirm the installed package revision and that startup reports the actual discovered digitizer. A failed/ambiguous discovery or permission failure must terminate the session, without falling back to another injection method.
2. On the synthetic page, test pen down/move/up and pressure changes. Mouse navigation must remain UI-only. A second pen must not take over an active stroke.
3. Test focus loss/proximity loss and a controlled scrcpy disconnect during a stroke. Confirm no stuck contact and that the physical pen works again after scrcpy closes. Do not kill the private writer child itself during a stroke.
4. With normal drawing paused, hold still: after approximately one second without a new pen packet, the safety watchdog releases. Lift and recontact to start again; a late move cannot resurrect the released stroke.
5. Reopen only the same synthetic notebook. Export only that notebook as `.note` and verify native shape/point records, pressure variation and persistence. A changing screen image alone is not acceptance.

The earlier standalone evdev proof established native saved brush points on Notes39011. It did not validate this integrated scrcpy implementation. Automated host/CI fixtures validate encoding, pressure, state transitions, EOF cleanup, discovery and SDL pen tracking; they do not prove end-to-end ink, alignment, latency or hardware fidelity.

## Failure boundaries

EOF, partial internal packets, timeout, normal close and controller errors attempt pressure/contact release and restoration of the captured idle X/Y/distance before releasing ownership. Startup itself does not inject events. No automatic reconnect/replay occurs after a writer error.

No software can guarantee restoration if the kernel refuses writes, the device disappears, power is lost, or the writer process itself is forcibly killed. Such errors are fatal and reported; do not continue drawing. This candidate's integrated parent/child lifecycle has been compiled and fixture-tested, but still requires the manual tablet checks above.

## Build

The packaging repository declares checksummed Android platform/build-tools archives and a JDK. `server/scripts/build-standalone.sh ANDROID_JAR D8_JAR OUTPUT` generates the repository's AIDL stubs, compiles all maintained Java sources, runs D8 and packages the dex archive loaded by `app_process`. It does not download dependencies. Use `bash server/scripts/test-boox-pen.sh` for host fixtures; Meson debug builds include the client pen-state and CLI regression tests.
