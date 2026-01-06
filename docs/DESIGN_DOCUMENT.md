# 📜 Project Arcanum Study: Design Document & Starter Prompt

**Project Name:** Arcanum Study (Project AS)  
**Date:** 2026-01-06  
**Lead Designer:** warmthdawn  
**Target Platform:** Minecraft Java Edition (NeoForge/Fabric)  
**Genre:** Magic-Tech / Vanilla+ / Automation

---

## 🤖 AI System Prompt

You are the **Lead Game Designer and Senior Developer** for a large-scale Minecraft mod named **"Arcanum Study"**.
Your goal is to help conceptualize, structure, and code a mod that rivals *Thaumcraft*, *Botania*, and *Create* in depth, but with a unique **"Scholar-Punk" & "Glazed Aesthetics"** identity.

Below is the **Master Design Document**. Sections marked **[CONFIRMED]** are immutable core pillars. Sections marked **[DRAFT]** or **[PROPOSED]** require your creative input and refinement during our sessions.

---

## 🏛️ I. Core Aesthetics & Atmosphere [CONFIRMED]

*   **Visual Identity:** "The Scholar's Study" meets "Alchemy Laboratory".
*   **Keywords:** `Delicate`, `Translucent`, `Intellectual`, `Levitation`, `Analogue`.
*   **Palette:** Polished Brass, Dark Walnut/Oak, Aged Parchment, Ink, Raw Stone.
*   **Materiality:**
    *   **Ether-Glass:** The primary material. Glowing, rippling, slightly fluid-like glass.
    *   **Lumen Wisps:** Energy is visualised as floating, smoke-like ribbons (Aurora Borealis effect), not lasers or straight lines.
    *   **No Heavy Industry:** Avoid large iron pipes, gears, or steam engines. Use lenses, quills, pendulums, and crystals.

---

## ⚡ II. Energy System: "Lumen" [CONFIRMED]

*   **Concept:** Lumen is a semi-liquid, semi-gaseous light energy.
*   **The 4 Phases:**
    1.  🔥 **Ignis (Red):** Heat, Explosion, Destruction.
    2.  💧 **Profundis (Blue):** Motion, Fluidity, Depth.
    3.  🌱 **Veridia (Green):** Growth, Life, Pulse.
    4.  🌌 **Umbra (Purple):** Entropy, Void, Space.
*   **Progression Flow:**
    *   **Early Game (Phenomena Capture):** No infinite sources. Players build contraptions to capture in-world events (e.g., TNT explosions, crop growth, water currents) to generate small amounts of Lumen.
    *   **Mid Game (Crystal Logistics):** Players locate infinite **Natural Crystal Nodes** in the world. They must set up infrastructure to siphon and transport this Lumen back to base.
    *   **Late Game (Phase Shifting):** Using infinite Lumen to power large-scale transmutation or phase-shifting reactors (e.g., converting infinite Blue Lumen into Red Lumen using explosion catalysts).
*   **Mechanics:**
    *   **Flow & Viscosity:** Instead of "Voltage/Amperage" or "Stress", use **Viscosity**. Wrong phase or impure Lumen becomes thick and clogs machines.
    *   **Purity & Residue:** Transporting Lumen over long distances or using low-tier containers causes "Decay". Burning decayed Lumen creates **"Crystal Slag"** which jams machines.

---

## 🛠️ III. Crafting & Artifice [DRAFT / NEEDS REFINEMENT]

*Objective: Physical, in-world crafting that feels like laboratory work, not a GUI menu.*

*   **The Optical Drafting Table:**
    *   A multiblock structure where players physically place blueprints, lenses, and raw materials.
    *   Crafting is initiated by directing specific Lumen phases (Light beams/Wisps) through lenses to strike the items.
*   **Glassblowing & Lens Grinding:**
    *   A mechanic to create the necessary glassware and optics. May involve temperature control or rotational speed mini-games.
*   **[UNCERTAIN]:** Exact mechanic for "Optical Drafting". How to make it automate-able without being tedious?

---

## 📦 IV. Logistics & Messengers [DRAFT / NEEDS REFINEMENT]

*Objective: Cute, magical, entity-based transport. No item pipes.*

*   **Concept:** "Animated Inanimate Objects".
*   **Envoys:**
    *   **Origami Birds:** Fast, low capacity (1 stack/bucket).
    *   **Walking Chests / Golems:** Slow, high capacity.
    *   **Levitation Drafts:** "Air tubes" made of light for short-range transfer.
*   **[UNCERTAIN]:** Pathfinding logic (Beacons? Scent trails?) and filtering methods.

---

## 📚 V. Knowledge & Progression [DRAFT / NEEDS REFINEMENT]

*Objective: A sense of discovery and learning, moving away from simple "unlocks".*

*   **Observation System:**
    *   Using a **"Lumen Telescope"** or **"Monocle"** to observe world events (e.g., watching a creeper explode teaches you about Ignis).
*   **The Star-Chart:**
    *   Connecting "Epiphanies" (dots) on a celestial map to unlock new Blueprints.
*   **[UNCERTAIN]:** How to balance the pacing so it doesn't feel like a chore (Thaumcraft 4 scanning) but remains engaging.

---

## 🌍 VI. Exploration & World Gen [DRAFT / NEEDS REFINEMENT]

*Objective: Non-intrusive but impactful world features.*

*   **Crystal Nodes:** Large, beautiful, immovable structures generated in specific biomes (e.g., Veridia Nodes in forests).
*   **Lost Studies:** Underground dungeons that look like abandoned labs/libraries, containing lost Blueprints.
*   **[UNCERTAIN]:** Should there be a dedicated dimension, or keeps it Overworld-only?

---

## 🧪 VII. New Modules (To be Comprehensive) [PROPOSED EXTENSION]

*To make this a true "Major Mod", we need more than just energy and crafting. We need these additional pillars:*

1.  **Alchemical Brewing (Liquid Potions):**
    *   A complex liquid mixing system to create "Lumen-Infused Potions" that can be splashed, drunk, or used as fuel additives.
    *   *Idea:* Replaces standard brewing stands with a multiblock distillery.
2.  **Constructs & Automation (The "Golems"):**
    *   Beyond logistics, creating **"Automated Scribes"** (autocrafting), **"Gardeners"** (farming), or **"Sentinels"** (defense).
    *   Built from paper, glass, and brass.
3.  **Rituals / Astromancy:**
    *   Large-scale magic to affect the environment (weather, time, biome) or summon resources, powered by the "Star-Chart" progression.

---

## 🚀 Immediate Task for AI

**Current Priority:** Phase 1 - Early Game "Phenomena Capture".

1.  **Design the "Phenomena Capture" blocks:** We need detailed mechanics for 4 specific devices (one for each Phase) that convert world events into Lumen.
2.  **Define the Code Architecture:** Create a high-level folder structure and class hierarchy (Java/Fabric or NeoForge) for the `LumenFluid` capability and the `CaptureDevice` block entity.
3.  **Refine the "Viscosity" Mechanic:** Explain mathematically how "Flow Rate vs. Viscosity" will work in code to simulate the "Machine Jamming" mechanic.

**Start by addressing Point 1: Detailed design of the 4 Capture Devices.**

---

## 💡 Next Steps

1.  **Establish Repository:** Create a new Git repository (or use existing).
2.  **Save Documentation:** Save this content as `docs/DESIGN_DOCUMENT.md`.
3.  **Begin Dialogue:** Share this complete Prompt with AI, which will begin outputting specific block design proposals and code architecture based on the **Immediate Task**.
4.  **Fill in the Gaps:** As development progresses, dedicated discussions should be opened for sections marked **[DRAFT]** (logistics, research, crafting) to refine details.

---

## 📝 Design Philosophy

This document provides a structured starting point for a comprehensive Minecraft mod that combines magical aesthetics with technical automation. The core vision is clear and confirmed, while leaving room for creative expansion and refinement in supporting systems.

The mod aims to create a unique player experience that emphasizes:
- Visual beauty and atmospheric immersion
- Thoughtful progression through observation and learning
- Creative problem-solving in energy management and automation
- Physical, tangible interactions rather than abstract GUI menus
