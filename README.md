<h1 align="center">VoxelTales</h1>

<p align="center">
  <img src="src/main/resources/icon-256.png" alt="VoxelTales Icon" width="160">
</p>

<p align="center">
  A Hytale mod/plugin focused on custom combat progression, weapon forging, and UI-driven gameplay systems.
</p>

---

VoxelTales is a Hytale mod/plugin inspired by the Roblox game [VoxlBlade](https://www.roblox.com/it/games/8651781069/Voxlblade).

It is a work-in-progress project aiming to deliver a more progression-focused combat experience in Hytale, with custom weapons, UI flows, and data-driven gameplay systems.

## Features
- Custom weapon and stats systems
- Player XP / leveling logic
- UI pages for weapon configuration and forging
- HUD support for weapon progression
- Config-driven weapon/stat data
- Packet listeners, events, and registries for gameplay integration
- NPC-driven interaction systems

## Roadmap
### Near-term
- Expand the Sword Sage into a proper in-world guide NPC
- Add dialogue options and lore-driven interactions
- Add a command to open the forging UI directly
- Add a skill selector flow to the Sword Sage UI

### Weapon Progression
- Add upgrade paths and unlock trees for weapons
- Support upgrade chains such as blade variants unlocking new forms
- Add configuration support for weapon unlock relationships
- Add weapon compatibility rules for future forging paths

### Skills and Content Expansion
- Add skill pools to blades and handles
- Filter skills based on weapon type and forge result compatibility
- Add more blades, handles, and skills once the progression systems are in place

## Project Structure
- `Components` — entity/player state components
- `Configs` — serialized game and weapon configuration data
- `Controllers` — higher-level gameplay coordination
- `Events` — event callbacks
- `PacketListeners` — packet handling hooks
- `Registries` — registration/bootstrap helpers
- `Systems` — runtime gameplay systems
- `UI` — pages, HUDs, and UI components
- `Utils` — helper and utility classes

## Requirements
- Java 25+
- Hytale server API
- HyUI
- DynamicFloatingDamageFormatter

## Notes
This project is still under active development.
Documentation will continue to evolve as the codebase settles and more systems are finalized.

## License
This project is proprietary and all rights are reserved.

Source code is provided for viewing only. You may not copy, modify, redistribute, sublicense, or use this code without prior written permission from the copyright holder.