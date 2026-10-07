package com.piotrek.groundworksscreen.logic;

import java.util.Arrays;

public final class GradationRouting {
    private GradationRouting() {}

    public static Batch plan(
            int[] unitsByMaterial,
            int selectedMaterialId,
            int perOutputLimit
    ) {
        int[] source = unitsByMaterial == null
                ? new int[0]
                : unitsByMaterial;
        int limit = Math.max(0, perOutputLimit);

        int selectedUnits = 0;
        if (selectedMaterialId > 0
                && selectedMaterialId < source.length
                && limit > 0) {
            selectedUnits = Math.min(
                    Math.max(0, source[selectedMaterialId]),
                    limit
            );
        }

        int[] remainder = new int[source.length];
        int remainingBudget = limit;

        for (int id = 1; id < source.length && remainingBudget > 0; id++) {
            if (id == selectedMaterialId) {
                continue;
            }

            int available = Math.max(0, source[id]);
            int amount = Math.min(available, remainingBudget);
            remainder[id] = amount;
            remainingBudget -= amount;
        }

        return new Batch(selectedUnits, remainder);
    }

    public record Batch(int selectedUnits, int[] remainderUnits) {
        public Batch {
            remainderUnits = remainderUnits == null
                    ? new int[0]
                    : Arrays.copyOf(remainderUnits, remainderUnits.length);
        }

        @Override
        public int[] remainderUnits() {
            return Arrays.copyOf(remainderUnits, remainderUnits.length);
        }
    }
}
