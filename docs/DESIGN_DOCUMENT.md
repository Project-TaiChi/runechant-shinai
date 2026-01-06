# 📜 Project Arcanum Study: Design Document & Starter Prompt

**Project Name:** Arcanum Study (Project AS / 星辉书斋)  
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

## 🚀 Immediate Tasks for AI

**Current Priority:** Phase 1 - Early Game "Phenomena Capture".

### Task 1: Design the "Phenomena Capture" Blocks

We need detailed mechanics for 4 specific devices (one for each Phase) that convert world events into Lumen:

1.  **Ignis Capture Device (Red Lumen):**
    *   **Trigger Events:** Explosions (TNT, Creepers), Fire, Lava contact
    *   **Mechanic:** Device must be placed near the event and "witness" it within a certain radius
    *   **Output:** Small amounts of Red Lumen per event
    *   **Challenge:** Events are finite and player-triggered, requiring resource investment

2.  **Profundis Capture Device (Blue Lumen):**
    *   **Trigger Events:** Water flow, Entity motion (minecarts, boats), Pistons
    *   **Mechanic:** Device measures kinetic energy or flow rate
    *   **Output:** Continuous but slow generation while motion occurs
    *   **Challenge:** Requires constant motion or flow setup

3.  **Veridia Capture Device (Green Lumen):**
    *   **Trigger Events:** Crop growth, Tree growth, Bone meal usage, Animal breeding
    *   **Mechanic:** Detects life energy during growth events
    *   **Output:** Small burst per growth event
    *   **Challenge:** Limited by tick speed and crop availability

4.  **Umbra Capture Device (Purple Lumen):**
    *   **Trigger Events:** Entity death, Block breaking, Item despawning, Darkness
    *   **Mechanic:** Absorbs entropy from decay events
    *   **Output:** Variable based on "value" of what decayed
    *   **Challenge:** Most controversial - may need to avoid feeling "evil"

### Task 2: Define the Code Architecture

Create a high-level folder structure and class hierarchy (Java/NeoForge) for the `LumenFluid` capability and the `CaptureDevice` block entity:

```
src/main/java/io/github/projecttaichi/arcanumStudy/
├── core/
│   ├── ArcanumStudy.java (Main mod class)
│   └── registration/
│       ├── ModBlocks.java
│       ├── ModItems.java
│       ├── ModFluids.java
│       └── ModBlockEntities.java
├── fluid/
│   ├── LumenFluid.java (Base fluid class)
│   ├── LumenFluidType.java
│   └── phases/
│       ├── IgnisLumen.java
│       ├── ProfundisLumen.java
│       ├── VeridiaLumen.java
│       └── UmbraLumen.java
├── block/
│   ├── capture/
│   │   ├── BaseCaptureDevice.java
│   │   ├── IgnisCaptureDevice.java
│   │   ├── ProfundisCaptureDevice.java
│   │   ├── VeridiaCaptureDevice.java
│   │   └── UmbraCaptureDevice.java
│   └── entity/
│       ├── BaseCaptureBlockEntity.java
│       └── [Specific capture block entities]
├── capability/
│   ├── LumenStorage.java
│   └── LumenHandler.java
└── event/
    ├── LumenEventHandler.java
    └── CaptureEventListener.java
```

### Task 3: Refine the "Viscosity" Mechanic

Explain mathematically how "Flow Rate vs. Viscosity" will work in code to simulate the "Machine Jamming" mechanic:

**Proposed Formula:**
```
Flow Rate = Base Flow Rate × (1 / (1 + Viscosity Factor))
Viscosity Factor = Impurity Level + Phase Mismatch Penalty + Distance Decay
```

**Parameters:**
*   **Base Flow Rate:** Determined by container/pipe tier (e.g., 100 mB/tick for basic, 500 mB/tick for advanced)
*   **Impurity Level:** 0.0 to 1.0, increases with contamination
*   **Phase Mismatch Penalty:** +0.5 if wrong Lumen phase for machine, +0.2 if mixed phases
*   **Distance Decay:** +0.01 per block distance without proper insulation

**Jamming Condition:**
```
If Flow Rate < Required Flow Rate:
    - Machine operates at reduced speed
    - Generates Crystal Slag over time
If Viscosity Factor > 2.0:
    - Machine completely jams
    - Requires cleaning before resuming operation
```

---

## 💡 Implementation Roadmap

### Phase 1: Foundation (Weeks 1-4)
- [ ] Set up basic Lumen fluid system
- [ ] Implement 4 capture devices with event listeners
- [ ] Create basic Lumen storage blocks
- [ ] Test early game loop (capture → store → use)

### Phase 2: Expansion (Weeks 5-8)
- [ ] Add Optical Drafting Table
- [ ] Implement Viscosity mechanics
- [ ] Create first set of Lumen-powered machines
- [ ] Add progression/observation system basics

### Phase 3: Mid-Game Content (Weeks 9-12)
- [ ] Generate Crystal Nodes in world
- [ ] Implement logistics (Envoys)
- [ ] Add phase-shifting mechanics
- [ ] Create Lost Studies structures

### Phase 4: Polish & Balance (Weeks 13-16)
- [ ] Refine all mechanics based on playtesting
- [ ] Add JEI integration
- [ ] Create documentation and guidebook
- [ ] Balance progression curve

---

## 🎨 Art Direction Notes

### Block Design Guidelines
*   **Capture Devices:** Delicate glass apparatus with brass fittings, resembling laboratory equipment
*   **Storage Containers:** Ether-glass vessels with visible Lumen swirling inside
*   **Machines:** Victorian-era scientific instruments meets magical aesthetics
*   **No Cubic Forms:** Where possible, use custom models with curves and elegant shapes

### Particle Effects
*   **Lumen Wisps:** Slow-moving, ribbon-like particles that fade gracefully
*   **Capture Events:** Burst of appropriate color with shimmer effect
*   **Machine Operation:** Gentle pulsing glow, not harsh blinking

### Sound Design
*   **Ambient:** Soft crystalline chimes, paper rustling, quill scratching
*   **Capture:** Ethereal "whoosh" as energy is absorbed
*   **Machine Work:** Quiet mechanical humming with glass resonance
*   **Avoid:** Loud industrial sounds, harsh metal clangs

---

## 🔧 Technical Considerations

### Performance
*   Particle effects must be optimized (LOD system, culling)
*   Entity-based logistics needs careful pathfinding optimization
*   Fluid simulation should be efficient for large-scale setups

### Compatibility
*   Should work alongside other major mods (Create, Botania, etc.)
*   JEI integration for all recipes
*   Consider Patchouli for in-game documentation

### Multiplayer
*   All mechanics must be server-friendly
*   No client-side only features that affect gameplay
*   Proper synchronization for all custom block entities

---

## 📝 Next Steps

1.  **Create Initial Repository Structure:** Set up the basic NeoForge mod project with the proposed package structure.
2.  **Implement Lumen Fluid System:** Start with the fluid registration and basic properties for all 4 phases.
3.  **Build First Capture Device:** Implement the Ignis Capture Device as a proof of concept.
4.  **Test Early Game Loop:** Verify that players can capture Lumen and see it stored in containers.
5.  **Iterate on Design:** Based on implementation experience, refine mechanics and add missing details to this document.

---

## 🤝 Collaboration Guidelines

*   **Design Decisions:** All major design changes must be documented in this file.
*   **Code Standards:** Follow NeoForge best practices and maintain consistent naming conventions.
*   **Commits:** Use clear, descriptive commit messages following conventional commits format.
*   **Issues:** Track features and bugs in GitHub Issues with appropriate labels.

---

*This is a living document. Update it as the project evolves.*

**Last Updated:** 2026-01-06  
**Version:** 1.0.0
