# QuantumCompressor ⚡

A modern, high-performance, and lightweight inventory compression plugin for Minecraft Paper/Spigot servers (1.20 - 1.21+).

## 🚀 Key Features

- **Zero Configuration Needed:** Automatically discovers and indexes all valid 3x3 and 2x2 crafting recipes from the server's native registry on startup.
- **Async & Duplication-Proof:** Inventory calculations run off-thread while inventory state transitions are applied atomically on the main thread, preventing race conditions and item duplication exploits.
- **NBT & Custom Item Safe:** Preserves custom names, lore, enchantments, and custom model data by ignoring non-vanilla items.
- **Modern Text Formatting:** Built using native Kyori Adventure / MiniMessage.

## 🛠 Commands & Permissions

| Command | Description | Permission | Default |
|---|---|---|---|
| `/compress` | Instantly compresses matching items in your inventory | `quantumcompressor.use` | `true` |
| `/autocompress` | Toggles auto-compression on item pickup | `quantumcompressor.auto` | `true` |
| `/qc reload` | Reloads the dynamic recipe index cache | `quantumcompressor.admin` | `op` |

## 📦 Building from Source

Requires **JDK 21** and **Maven**:

```bash
git clone [https://github.com/your-username/QuantumCompressor.git](https://github.com/your-username/QuantumCompressor.git)
cd QuantumCompressor
mvn clean package