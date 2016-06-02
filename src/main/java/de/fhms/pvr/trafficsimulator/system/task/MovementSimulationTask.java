package de.fhms.pvr.trafficsimulator.system.task;

import de.fhms.pvr.trafficsimulator.system.TrafficSimulator;
import de.fhms.pvr.trafficsimulator.system.Vehicle;

public class MovementSimulationTask extends AbstractSimulationTask {
    public MovementSimulationTask(TrafficSimulator trafficSimulator, int lowerBound, int upperBound) {
        super(trafficSimulator, lowerBound, upperBound);
    }

    @Override
    public Boolean call() throws Exception {
        Vehicle[][] street = trafficSimulator.getStreet();
        Vehicle tmp;
        int tmpSpeed, tmpIndex, iteration = trafficSimulator.getIteration();
        for (int y = 0; y < street.length; y++) {
            for (int x = lowerBound; x <= upperBound; x++) {
                if ((tmp = street[y][x]) != null && tmp.getMoveCount() < iteration) {
                    if ((tmpSpeed = tmp.getCurrentSpeed()) > 0) {
                        tmpIndex = (x + tmpSpeed) % street[y].length;
                        street[y][x] = null;
                        street[y][tmpIndex] = tmp;
                    }
                    tmp.incrementMoveCount();
                }
            }
        }
        return true;
    }
}
