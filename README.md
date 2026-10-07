# Peterwolf's Groundworks Excavated Material Gradation Screen

Early-release stationary screening plant for Minecraft Java 26.3 / Fabric.

## Features

- Requires Peterwolf's Groundworks.
- Central machine body is about 2 x 3 x 2 blocks.
- Large open loading hopper adds the third block of height.
- Accepts Groundworks material directly from compatible excavator and loader buckets.
- Internal buffer capacity: 8 full Groundworks blocks = 4096 integer units.
- Supports dirt, sand, gravel, cobblestone and mixed Groundworks compositions.
- One selected material is routed to the side conveyor.
- Every other material is routed to the second conveyor.
- Both conveyors rise high enough to place a Groundworks dump truck or another screen below the discharge point.
- If no compatible receiver is below an outlet, material falls back into Groundworks terrain and forms a pile.
- Exact per-material volume is conserved.
- A blocked or full receiver never causes silent material deletion.
- Conveyor rollers and the eccentric screen drive animate while material is moving.

## Crafting and placement

The gradation screen is a normal survival item and can be crafted in a crafting table:

    I I I
    R H R
    I P I

- I = iron ingot
- R = redstone
- H = hopper
- P = piston

Right-click a block with the item to place the complete screen machine. The item is consumed in survival mode. Shift + right-click the empty machine with an empty hand retrieves it again.

## Selecting material

- Right-click while holding dirt, sand, gravel, cobblestone or stone to select its Groundworks material.
- Right-click with another item or with an empty hand to cycle through available Groundworks materials.
- Default selected material is gravel.
- Shift + right-click with an empty hand retrieves the machine only when the internal buffer is empty.

## Typical workflow

1. Dump mixed excavated material into the top hopper.
2. Select the material that should be screened out.
3. Park a Groundworks dump truck under the side outlet for the selected stream.
4. Park another truck or another screen under the second outlet for the remainder.
5. Chain multiple screens to progressively separate a mixed excavation stream.

## Build

The project uses Minecraft 26.3, Fabric Loader 0.19.5, Fabric API 0.160.7+26.3 and Java 25.

Publish Groundworks to Maven Local first:

    cd ../peterwolfs-groundworks
    ./gradlew publishToMavenLocal

Then build this mod with its own Gradle wrapper:

    ./gradlew clean test build

The resulting jar is written to build/libs.
