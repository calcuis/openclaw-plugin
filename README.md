# OpenClaw JetBrains Plugin

OpenClaw adds a status bar launcher for the OpenClaw CLI in JetBrains IDEs.

![screenshot](https://raw.githubusercontent.com/calcuis/openclaw-plugin/master/demo.gif)

## Features

- Status bar entry and `Tools -> OpenClaw: Show Menu` action
- Dashboard, package check, setup, gateway, and terminal commands
- Integrated terminal execution with WSL support on Windows
- Package checker for installing or updating the global `openclaw` npm package
- Pairing dialog for app approval codes
- Optional startup status check in `Settings -> Tools -> OpenClaw`

## Prerequisites

- JetBrains IDE compatible with build `243` through `261.*`
- OpenClaw CLI available in your shell or WSL Ubuntu environment
- `gguf-connector` is optional and only required for the `Terminal` menu item (`ggc oc`)

On Windows, OpenClaw commands are run through:

```bash
wsl -d Ubuntu
```

## Build

Use a JDK 17+ runtime:

```bash
./gradlew buildPlugin
```

The Marketplace-ready ZIP is written to:

```text
build/distributions/openclaw-0.3.7.zip
```

## License

MIT

## Reference

https://github.com/mochiyaki/claw
