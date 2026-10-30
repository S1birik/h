# Wersize Pack Editor (Fabric, Minecraft 1.21.11)

Client-side mod. In a world type `/wersize`: pick a category (PvP / Blocks / Gear / HUD / Misc),
click an item to cycle its colour style, press **Apply**. The mod recolours the vanilla textures,
writes them to `.minecraft/resourcepacks/Wersize/` and enables that pack automatically.

Build: install JDK 21, then in this folder run
    gradle wrapper --gradle-version 9.2.1
    ./gradlew build        (Windows: gradlew.bat build)
The jar appears in build/libs/ (use wersize-1.0.0.jar, not the -sources one). Put it in mods/ with Fabric API.
