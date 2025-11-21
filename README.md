<img alt="banner" src="https://github.com/user-attachments/assets/ab68b2ae-8350-4fb3-9773-a38ae8e7584c" />

Better Foliage - Legacy Edition
===============================

A modern fork of [BetterFoliage](https://www.curseforge.com/minecraft/mc-mods/better-foliage) by octarine_noise for Minecraft 1.7.10.
This mod alters the appearance of leaves, logs, grass and other natural miscellanea.

Requires [UniMixins](https://github.com/LegacyModdingMC/UniMixins/releases) and [Forgelin](https://github.com/GTNewHorizons/Forgelin/releases).

**Important:**
If you're upgrading from a previous version of the mod or from the original Better Foliage mod you must delete the config file located at `config/BetterFoliage.cfg` and let the mod regenerate it on the next game launch for any updated fixes to be applied in-game.
Make a backup of any settings you've changed before doing so.

This mod aims to enhance the visuals of your world by adding more detail to nature related blocks.

Better Foliage - Legacy Edition is a 100% client-side mod, and does not need to be installed on the server.
It also aims to be compatible with other graphics mods, such as [Angelica](https://modrinth.com/mod/angelica), [SwanSong](https://modrinth.com/mod/swansong) and [Optifine](https://optifine.net/home).

## Features

All the features this mod adds can be individually configured and toggled based on preference by accessing the configuration menu (F8 by default).
Currently, the mod adds the following:

- **Algae:** Add algae to dirt blocks in deep water
- **Better Cactus:** Add a more natural shape to cacti
- **Connected Grass Textures:** Full grass on block sides, as well as rendering grass in the dirt block underneath for a smoother texture transition. Also works for grass blocks covered in snow
- **Coral:** Add coral to sand blocks in deep water
- **Falling Leaves:** Leaf particle effects from the bottom of leaf blocks
- **Extra Leaves:** Increases the leaf density of Leaf Blocks. Optionally, enhances the texture of snow in leaves covered by it
- **Better Lilypad:** Enhances lilypads with roots and occasional flowers
- **Netherrack Vines:** Hanging vines under netherrack
- **Reeds:** Add reeds to dirt blocks in shallow water
- **Rising Souls:** Emit rising soul particle effect from the top of soul sand
- **Round Logs:** Swaps log block models for a rounded shape
- **Short Grass and Mycelium:** Add small tufts of grass or mycelium on top of appropriate blocks

Check out the [wiki](https://github.com/Kynake/BetterFoliage/wiki/Features) for more details about, and screenshots of, each feature.

## Installation
Place the mod's jar file inside the `mods` folder of your modded Minecraft instance.
Make sure you've also installed the latest [Forge](https://files.minecraftforge.net/net/minecraftforge/forge/index_1.7.10.html) for Minecraft 1.7.10, [UniMixins](https://github.com/LegacyModdingMC/UniMixins/releases) and [Forgelin](https://github.com/GTNewHorizons/Forgelin/releases).

## Contributing
You may contribute to this mod by reporting any [Issues](https://github.com/Kynake/BetterFoliage/issues?q=sort%3Aupdated-desc+is%3Aissue+is%3Aopen) you find while using it or by submitting [Pull Requests](https://github.com/Kynake/BetterFoliage/pulls?q=sort%3Aupdated-desc+is%3Apr+is%3Aopen) with fixes or improvements.

In particular, it'd be great to get the project translated into more languages.

To submit a pull request you should fork the project into your own GitHub account and build the project by cloning it locally and running this command in a terminal to build the initial project setup:

_(if you're using Windows, replace `./gradlew` with `.\gradlew.bat`)_

`./gradlew updateBuildScript setupDecompWorkspace`

If you use IntelliJ IDEA you should also run `./gradlew idea`, if you use Eclipse run `./gradlew eclipse`.

From this point forward you should have a working project, and should be able to run `./gradlew runClient` to test the mod in a development environment or `./gradlew build` to generate a jar file for testing in a normal modpack.

## License and Modpacks

Better Foliage - Legacy Edition is under the MIT License. You may use or fork and modify this mod, or use it in modpacks, without asking for permission.
I would appreciate being informed about it, for curiosity's sake.
