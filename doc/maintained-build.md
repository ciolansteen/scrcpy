# Maintained build

Development uses the `iAdrian` branch. Upstream branches and tags remain unchanged.

- Canonical source: https://git.ciolan.net/iAdrian/scrcpy
- Pristine upstream pull mirror: https://git.ciolan.net/github-mirrors/scrcpy
- Public GitHub fork: https://github.com/ciolansteen/scrcpy
- Arch package: https://git.ciolan.net/archRepo/scrcpy-git

The GitHub fork receives exact Git refs through a native Gitea push mirror. Create upstream PRs explicitly from focused feature branches; upstream requests contributions based on `dev`.

Pushes to `iAdrian` dispatch the Arch package pipeline. The initial package is a mirroring/control baseline, not a BOOX stylus implementation. It builds this client and uses the checksum-verified matching upstream Android server. The package recipe refuses server-source changes until server compilation is integrated.

Install `scrcpy-git` from the configured Pacman repository with a full system upgrade. It conflicts with and replaces `scrcpy`. See the packaging repository for installation and manual test instructions. CI runs upstream assertion tests in a separate debug build and smoke-tests the installed release CLI and server payload before publication. These checks do not establish tablet mirroring or editable BOOX Notes ink.
