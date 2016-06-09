package de.fhms.pvr.trafficsimulator.system.util;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;

public class SimulationTaskSplitter {

    public static ArrayList<Pair<Integer, Integer>> getSimulationTaskBordersFor(int sectionAmount, int taskAmount) {
        ArrayList<Pair<Integer, Integer>> taskBorders = new ArrayList<>();
        int bound = sectionAmount / taskAmount;
        if (sectionAmount % taskAmount != 0) {
            bound = bound + 1;
        }
        int upperBorder, lowerBorder = 0;
        for (int i = 0; i < taskAmount; i++) {
            if (i < taskAmount - 1) {
                upperBorder = lowerBorder + bound - 1;
                taskBorders.add(Pair.of(lowerBorder, upperBorder));
            } else {
                taskBorders.add(Pair.of(lowerBorder, sectionAmount - 1));
            }
            lowerBorder += bound;
        }
        return taskBorders;
    }
}
