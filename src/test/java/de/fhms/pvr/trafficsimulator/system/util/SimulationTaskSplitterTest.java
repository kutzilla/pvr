package de.fhms.pvr.trafficsimulator.system.util;

import de.fhms.pvr.trafficsimulator.system.task.SimulationTask;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class SimulationTaskSplitterTest {

    private ArrayList<Pair<Integer, Integer>> expectedBorderPairs;

    @Before
    public void setUp() {
        this.expectedBorderPairs = new ArrayList<>();
    }

    @Test
    public void testEvenTaskAmount() {
        this.expectedBorderPairs.add(Pair.of(0, 4));
        this.expectedBorderPairs.add(Pair.of(5, 9));
        this.expectedBorderPairs.add(Pair.of(10, 14));
        this.expectedBorderPairs.add(Pair.of(15, 19));

        assertEquals(expectedBorderPairs, SimulationTaskSplitter.getSimulationTaskBordersFor(20, 4));
    }

    @Test
    public void testOddTaskAmount() {
        this.expectedBorderPairs.add(Pair.of(0, 57));
        this.expectedBorderPairs.add(Pair.of(58, 115));
        this.expectedBorderPairs.add(Pair.of(116, 173));
        this.expectedBorderPairs.add(Pair.of(174, 231));
        this.expectedBorderPairs.add(Pair.of(232, 289));
        this.expectedBorderPairs.add(Pair.of(290, 347));
        this.expectedBorderPairs.add(Pair.of(348, 399));

        assertEquals(expectedBorderPairs, SimulationTaskSplitter.getSimulationTaskBordersFor(400, 7));
    }

    @Test
    public void testOddStreetAmount() {
        this.expectedBorderPairs.add(Pair.of(0, 24));
        this.expectedBorderPairs.add(Pair.of(25, 49));
        this.expectedBorderPairs.add(Pair.of(50, 72));

        assertEquals(expectedBorderPairs, SimulationTaskSplitter.getSimulationTaskBordersFor(73, 3));
    }
}
