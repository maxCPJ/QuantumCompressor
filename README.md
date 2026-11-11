# QuantumCompressor ⚡

High-performance, dynamic inventory compression plugin with zero configuration for Paper and Spigot servers (1.20 - 1.21+).

[![Modrinth](https://img.shields.io/badge/Modrinth-Download-00AF5C?style=for-the-badge&logo=modrinth)](https://modrinth.com/project/8PBq17k4)

---

## 🚀 Features

- **Dynamic Recipe Registry:** Automatically scans native 3x3 and 2x2 recipes from the server at startup — no manual item mapping needed.
- **Cascade Compression:** Fully condenses multi-tier items in a single pass (e.g. Iron Nuggets ➔ Iron Ingots ➔ Iron Blocks).
- **Async & Exploit-Proof:** Inventory calculations run off-thread with atomic main-thread commits to prevent item duplication exploits.
- **Metadata Protection:** Custom items with custom names, lore, or CustomModelData are safely preserved and ignored.

---

## ⌨️ Commands & Permissions

| Command | Description | Permission | Default |
|---|---|---|---|
| `/compress` (alias `/qc`) | Compresses items in the player's inventory | `quantumcompressor.use` | Everyone |
| `/autocompress` (alias `/ac`) | Toggles auto-compression on item pickup and chest close | `quantumcompressor.auto` | Everyone |
| `/quantumcompressor reload` | Reloads dynamic recipe caches | `quantumcompressor.admin` | OP |

---

## 🛠 Compilation & Build

Requirements:
- Java 21+
- Maven

```bash
git clone [https://github.com/maxCPJ/QuantumCompressor.git](https://github.com/maxCPJ/QuantumCompressor.git)
cd QuantumCompressor
mvn clean package
