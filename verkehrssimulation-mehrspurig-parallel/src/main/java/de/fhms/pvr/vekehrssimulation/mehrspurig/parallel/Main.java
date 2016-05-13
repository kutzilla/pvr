package de.fhms.pvr.vekehrssimulation.mehrspurig.parallel;

/**
 * Created by Matthias on 29.04.16.
 */
public class Main {

    public static void main(String[] args) {
        Street street = new Street(2, 200, 0.5);
        Thread [] threads = Main.getSimulatorThreads(10, street);
        for (Thread t: threads) {
            t.start();
        }
    }

    private static Thread[] getSimulatorThreads(int amount, Street street) {
        int partLength = street.getSectionAmount() / amount;
        int lowerBound = 0, i = 0, higherBound = 0;
        Thread[] threads = new Thread[amount];
        while (lowerBound < street.getSectionAmount()) {
            higherBound = lowerBound + partLength;
            threads[i] = new Thread(new Simulator(street, lowerBound, higherBound));
            lowerBound = higherBound;
            i++;
        }
        return threads;
    }

}
