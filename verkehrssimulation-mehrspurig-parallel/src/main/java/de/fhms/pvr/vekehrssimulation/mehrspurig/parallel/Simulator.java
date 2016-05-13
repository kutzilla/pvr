package de.fhms.pvr.vekehrssimulation.mehrspurig.parallel;

/**
 * Created by Matthias on 29.04.16.
 */
public class Simulator implements Runnable {

    private Street street;

    private int lowerBound;

    private int higherBound;

    public Simulator(Street street, int lowerBound, int higherBound) {
        this.street = street;
        this.lowerBound = lowerBound;
        this.higherBound = higherBound;
    }

    @Override
    public void run() {
        for (int i = 0; i < 10000; i++) {
            System.out.println(lowerBound + ":" + higherBound);
        }
    }
}
