package com.piotrek.groundworksscreen.logic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class GradationRoutingTest {

    @Test
    void selectedMaterialNeverLeaksIntoRemainderOutput() {
        int[] units = new int[] {0, 20, 60, 40, 12};

        GradationRouting.Batch batch =
                GradationRouting.plan(units, 3, 32);

        assertEquals(32, batch.selectedUnits());
        assertArrayEquals(
                new int[] {0, 20, 12, 0, 0},
                batch.remainderUnits()
        );
    }

    @Test
    void outputBudgetsAreIndependent() {
        int[] units = new int[] {0, 80, 80, 80};

        GradationRouting.Batch batch =
                GradationRouting.plan(units, 2, 32);

        assertEquals(32, batch.selectedUnits());
        assertArrayEquals(
                new int[] {0, 32, 0, 0},
                batch.remainderUnits()
        );
    }

    @Test
    void missingSelectedMaterialDoesNotStopRemainderStream() {
        int[] units = new int[] {0, 10, 0, 18};

        GradationRouting.Batch batch =
                GradationRouting.plan(units, 2, 32);

        assertEquals(0, batch.selectedUnits());
        assertArrayEquals(
                new int[] {0, 10, 0, 18},
                batch.remainderUnits()
        );
    }
}
