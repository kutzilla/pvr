package de.fhms.pvr.trafficsimulator.system.task;

import de.fhms.pvr.trafficsimulator.system.TrafficSimulator;
import de.fhms.pvr.trafficsimulator.system.Vehicle;

public class MovementSimulationTask extends AbstractSimulationTask {

    private int currentIteration;

    public MovementSimulationTask(Vehicle[][] street, int lowerBorder, int upperBorder) {
        super(street, lowerBorder, upperBorder);
        this.currentIteration = 0;
    }

    @Override
    public Void call() {
        Vehicle tmp;
        int tmpSpeed, tmpIndex;
        currentIteration++;
        for (int y = 0; y < street.length; y++) {
            for (int x = lowerBound; x <= upperBound; x++) {
                if ((tmp = street[y][x]) != null && tmp.getMoveCount() < currentIteration) {
                    if ((tmpSpeed = tmp.getCurrentSpeed()) > 0) {
                        tmpIndex = (x + tmpSpeed) % street[y].length;
                        street[y][x] = null;
                        street[y][tmpIndex] = tmp;
                    }
                    tmp.incrementMoveCount();
                }
            }
        }
        return null;
    }
}
