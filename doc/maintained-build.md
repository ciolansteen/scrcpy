# Maintained build

Development uses the `iAdrian` branch. Upstream branches and tags remain unchanged.

- Canonical source: https://git.ciolan.net/iAdrian/scrcpy
- Pristine upstream pull mirror: https://git.ciolan.net/github-mirrors/scrcpy
- GitHub fork for explicit upstream contributions: https://github.com/ciolansteen/scrcpy
- Arch package automation: https://git.ciolan.net/archRepo/scrcpy-git

Pushes to the maintained branch dispatch the packaging build. Native Gitea push mirroring keeps the GitHub fork synchronized; it does not create upstream PRs automatically.

The experimental BOOX backend is opt-in. See [BOOX pen input](boox-pen.md) for calibration, safety limits, building and manual acceptance. Both client and Android server are compiled from this checkout. No upstream server binary substitutes for maintained server source.

Install `scrcpy-git` from the configured Pacman repository with a full system upgrade. It conflicts with and replaces `scrcpy`. CI runs upstream assertion tests in a separate debug build, host-only BOOX fixtures and installed release/payload checks before publication. Those checks do not establish tablet mirroring or editable BOOX Notes ink; integrated hardware acceptance is manual.
