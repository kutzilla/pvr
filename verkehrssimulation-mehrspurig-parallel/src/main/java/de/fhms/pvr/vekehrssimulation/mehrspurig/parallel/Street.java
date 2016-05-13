package de.fhms.pvr.vekehrssimulation.mehrspurig.parallel;

import java.util.Random;

public class Street {

    private static final int MAX_VEHICLE_SPEED = 5;

    private int sections[][];

    public Street(int[][] sections) {
        this.sections = sections;
    }

    public Street(int trackAmount, int sectionAmount, double vehicleDensity) {
        this.sections = new int[trackAmount][sectionAmount];
        for (int i = 0; i < sections.length; i++) {
            for (int j = 0; j < sections[i].length; j++) {
                this.sections[i][j] = -1;
            }
        }
        int vehicleAmount = (int) (((double) sectionAmount) * vehicleDensity);
        Random random = new Random();
        int i = 0, trackIndex, sectionIndex;
        while (i < 10) {
            trackIndex = random.nextInt(trackAmount);
            sectionIndex = random.nextInt(sectionAmount);
            if (this.sections[trackIndex][sectionIndex] < 0) {
                this.sections[trackIndex][sectionIndex] = random.nextInt(MAX_VEHICLE_SPEED + 1);
                i++;
            }
        }
    }

    public int getTrackAmount() {
        return sections.length;
    }

    public int getSectionAmount() {
        return sections[0].length;
    }

    public int[][] getSections() {
        return sections;
    }

    public void printSections() {
        int val;
        for (int i = 0; i < sections.length; i++) {
            for (int j = 0; j < sections[i].length; j++) {
                val = sections[i][j];
                if (val >= 0) {
                    System.out.print("[" + val + "] ");
                } else {
                    System.out.print("[ ] ");
                }
            }
            System.out.println();
        }
    }

}
