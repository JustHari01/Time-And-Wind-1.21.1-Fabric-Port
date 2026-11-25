# Time & Wind Custom Ticker (UNOFFICIAL)

An unofficial Fabric compatibility port of Aiefu's Time & Wind Custom Ticker for Minecraft 1.21.1.

## Features

- Set separate day and night lengths per dimension.
- Synchronize the in-game clock to local/system time, with global or per-dimension sunrise, sunset, and timezone settings.
- Optionally accelerate the night when players sleep.
- Keep the server configuration synchronized with connected clients.
- Retain the original `/taw` configuration and diagnostic commands.

Configuration files are created in `config/time-and-wind/`. Most commands require operator permission level 4. After changing settings, use `/taw reload` to apply them.

## Attribution and license

This port is based on [Time & Wind by Aiefu](https://modrinth.com/mod/time-and-wind) and its [original source repository](https://github.com/CmdrJane/time-and-wind). The upstream source is licensed under GPL-3.0-only. This derivative is distributed under the same license; see [LICENSE](LICENSE).

The upstream CurseForge listing is [Time & Wind](https://www.curseforge.com/minecraft/mc-mods/time-wind). This repository is the source for the unofficial Minecraft 1.21.1 Fabric port.

## Port notes

The port updates the project to Minecraft 1.21.1 and Fabric API 0.116.18, including migration of its client/server messages to the current typed payload API. It preserves the upstream ticker, configuration, sleep, and command behavior. The Gradle build and an isolated dedicated-server start/stop check passed. Client rendering and in-game interaction have not yet been tested.

AI assistance was used while adapting the source and preparing the project description; the port is based on and attributes Aiefu's GPL-licensed work.
