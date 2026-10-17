# Graph Report - Peterwolfs-Groundworks-excavated-material-gradation-screen  (2026-10-08)

## Corpus Check
- Corpus is ~7,322 words - fits in a single context window. You may not need a graph.

## Summary
- 212 nodes · 470 edges · 14 communities (4 shown, 10 thin omitted)
- Extraction: 95% EXTRACTED · 5% INFERRED · 0% AMBIGUOUS · INFERRED: 22 edges (avg confidence: 0.88)
- Token cost: 1,050 input · 560 output

## Community Hubs (Navigation)
- Block & Entity Interaction
- Client Model & Renderer
- Conveyor Stream Rendering
- Routing Logic & Tests
- Mod Registration & Items
- Discharge & Chained Receivers
- Entity State & Lifecycle
- CI & Build Configuration
- Architecture & Overview

## God Nodes (most connected - your core abstractions)
1. `GradationScreenEntity` - 61 edges
2. `GranularConveyorStreamRenderer` - 16 edges
3. `GroundworksGradationScreenMod` - 14 edges
4. `GradationScreenRenderer` - 13 edges
5. `GradationScreenModel` - 12 edges
6. `GradationScreenRenderState` - 9 edges
7. `GradationScreenItem` - 7 edges
8. `GradationRouting` - 6 edges
9. `GradationRoutingTest` - 6 edges
10. `GroundworksGradationScreenClient` - 5 edges

## Surprising Connections (you probably didn't know these)
- `Volumetric Flow Visualization` --conceptually_related_to--> `GranularConveyorStreamRenderer`  [INFERRED]
  README.md → src/client/java/com/piotrek/groundworksscreen/client/render/GranularConveyorStreamRenderer.java
- `Dual Conveyor Material Routing` --conceptually_related_to--> `GradationRouting`  [INFERRED]
  README.md → src/main/java/com/piotrek/groundworksscreen/logic/GradationRouting.java
- `Gradation Screen Entity Texture` --references--> `GradationScreenModel`  [INFERRED]
  src/main/resources/assets/pw_groundworks_gradation_screen/textures/entity/gradation_screen.png → src/client/java/com/piotrek/groundworksscreen/client/model/GradationScreenModel.java
- `Gradation Screen Entity Texture` --references--> `GradationScreenRenderer`  [INFERRED]
  src/main/resources/assets/pw_groundworks_gradation_screen/textures/entity/gradation_screen.png → src/client/java/com/piotrek/groundworksscreen/client/render/GradationScreenRenderer.java
- `Gradation Screen Item Texture` --references--> `GradationScreenItem`  [INFERRED]
  src/main/resources/assets/pw_groundworks_gradation_screen/textures/item/gradation_screen.png → src/main/java/com/piotrek/groundworksscreen/item/GradationScreenItem.java

## Import Cycles
- None detected.

## Hyperedges (group relationships)
- **Excavated Material Gradation Pipeline** — readme_overview, readme_dual_conveyor_routing, readme_buffer_capacity [INFERRED 0.85]

## Communities (14 total, 10 thin omitted)

### Community 1 - "Client Model & Renderer"
Cohesion: 0.08
Nodes (5): GroundworksGradationScreenClient, GradationScreenModel, GradationScreenRenderer, GradationScreenRenderState, Gradation Screen Entity Texture

### Community 3 - "Routing Logic & Tests"
Cohesion: 0.18
Nodes (4): Dual Conveyor Material Routing, Batch, GradationRouting, GradationRoutingTest

### Community 5 - "Mod Registration & Items"
Cohesion: 0.16
Nodes (3): GroundworksGradationScreenMod, GradationScreenItem, Gradation Screen Item Texture

### Community 10 - "CI & Build Configuration"
Cohesion: 0.40
Nodes (4): Groundworks Checkout & Setup, Gradle Build & Test Pipeline, CI Build Workflow, Minecraft 26.3 & Java 25 Prerequisites

## Knowledge Gaps
- **8 isolated node(s):** `Groundworks Checkout & Setup`, `Gradation Screen Plant Overview`, `Dual Conveyor Material Routing`, `Volumetric Flow Visualization`, `Material Selection Interaction` (+3 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 55 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **10 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `GradationScreenEntity` connect `Entity State & Lifecycle` to `Block & Entity Interaction`, `Client Model & Renderer`, `Volumetric Storage Buffer`, `Mod Registration & Items`, `Discharge & Chained Receivers`, `Processing & Data Persistence`, `Spatial Geometry & Outlets`?**
  _High betweenness centrality (0.437) - this node is a cross-community bridge._
- **What connects `Groundworks Checkout & Setup`, `Gradation Screen Plant Overview`, `Dual Conveyor Material Routing` to the rest of the system?**
  _8 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Block & Entity Interaction` be split into smaller, more focused modules?**
  _Cohesion score 0.07017543859649122 - nodes in this community are weakly interconnected._
- **Why does `GradationScreenRenderer` connect `Client Model & Renderer` to `Conveyor Stream Rendering`, `Entity State & Lifecycle`?**
  _High betweenness centrality (0.102) - this node is a cross-community bridge._
- **Should `Client Model & Renderer` be split into smaller, more focused modules?**
  _Cohesion score 0.07827260458839407 - nodes in this community are weakly interconnected._
- **Should `Volumetric Storage Buffer` be split into smaller, more focused modules?**
  _Cohesion score 0.13450292397660818 - nodes in this community are weakly interconnected._