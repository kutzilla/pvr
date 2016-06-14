package de.fhms.pvr.trafficsimulator.system.task;

import de.fhms.pvr.trafficsimulator.system.TrafficSimulator;
import de.fhms.pvr.trafficsimulator.system.Vehicle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MovementTask extends SimulationTask {

    private static final Logger LOG = LogManager.getLogger(MovementTask.class);

    private int currentIteration;

    public MovementTask(Vehicle[][] street, int lowerBorder, int upperBorder) {
        super(street, lowerBorder, upperBorder);
        this.currentIteration = 0;
    }

    @Override
    public Void call() {
        Vehicle tmp;
        int tmpSpeed, tmpIndex;
        currentIteration++;
        for (int y = 0; y < street.length; y++) {
            for (int x = lowerBorder; x <= upperBorder; x++) {
                if ((tmp = street[y][x]) != null && tmp.getMoveCount() < currentIteration) {
                    if ((tmpSpeed = tmp.getCurrentSpeed()) > 0) {
                        tmpIndex = (x + tmpSpeed) % street[y].length;
                        if (street[y][tmpIndex] != null) {
                            LOG.error("Das Auto kommt von " + x + ":" + y + " mit der Geschwindigkeit " + tmpSpeed +
                                    " und das Auto an der Position " + tmpIndex + ":" + y + " wird überfahren");
                        }
                        tmp.incrementMoveCount();
                        street[y][x] = null;
                        street[y][tmpIndex] = tmp;
                    } else {
                        tmp.incrementMoveCount();
                    }
                }
            }
        }
        return null;
    }
}
