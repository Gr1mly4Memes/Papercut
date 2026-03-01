<div align="center">
<img src="public/logo/logo.png" alt="Papercut">

# Papercut
Papercut is a fork of [Folia](https://github.com/PaperMC/Folia), which focuses on compatibility and optimization

**Papercut is still EXPERIMENTAL phase, may or may not cause instabilities!**
</div>

## ⚙️ Features
- **Based on [Folia](https://github.com/PaperMC/Folia)** - Adds multithreading your server
- **Linear Region File Format** - Optimize your world with the old V1/V2 linear format
- **Almost Compatible** - Added more support for Bukkit, Paper plugins

*...and some more coming soon*

## 📦 Building and setting up
Run the following commands in the root directory:

```bash
> ./gradlew applyAllPatches              # apply all patches
> ./gradlew createMojmapPaperclipJar     # build the server jar
```

## ⚖️ License
Papercut is licensed under the GNU General Public License v3.0. You can find the license [here](LICENSE).

## 📊 Plugin Compatibility
Papercut maintains multithreading capabilities while adding compatibility for Bukkit, Spigot, and Paper plugins. Based on our implementation approach, we estimate the following compatibility levels:

- **Working (60-75%)** - Plugins using proper event listeners, basic command handlers, and respecting async task patterns
- **Partially Working (15-25%)** - Plugins with assumptions about single-threaded behavior or cross-region entity/chunk operations
- **Broken (10-20%)** - Plugins directly manipulating chunks/entities from wrong threads or relying on global state without synchronization

### Factors Affecting Compatibility:
- Whether plugin authors used proper async patterns originally
- Assumptions made about single-threaded behavior
- Interaction with multiple regions simultaneously
- Complexity of entity/block manipulation logic

Most well-written plugins that follow best practices will work seamlessly on Papercut, while legacy plugins designed exclusively for single-threaded servers may require updates.

## 📜 Credits
Papercut includes patches and features from other projects, and without these projects, Papercut wouldn't exist today. Here is the list of projects that Papercut takes patches from:

- [Luminol](https://github.com/LuminolMC/Luminol)
- [Folia](https://github.com/PaperMC/Folia)
- [Pluto](https://github.com/Yive/Pluto)
- [Canva](https://github.com/CraftCanvasMC/Canvas)
- [Pufferfish](https://github.com/pufferfish-gg/Pufferfish)

