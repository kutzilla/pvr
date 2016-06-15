package de.fhms.pvr.trafficsimulator.system.task;

import de.fhms.pvr.trafficsimulator.system.Vehicle;
import de.fhms.pvr.trafficsimulator.system.util.RandomProbabilityGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DriveActionTask extends SimulationTask {

    private static final Logger LOG = LogManager.getLogger(DriveActionTask.class);

    private double fastDawdleProbability;

    private double slowDawdleProbability;

    private int newX;

    public DriveActionTask(Vehicle[][] street, int lowerBorder, int upperBorder,
                           double fastDawdleProbability, double slowDawdleProbability) {
        super(street, lowerBorder, upperBorder);
        this.fastDawdleProbability = fastDawdleProbability;
        this.slowDawdleProbability = slowDawdleProbability;
    }

    protected void simulateAcceleration(Vehicle v) {
        v.incrementCurrentSpeed();
    }

    protected int simulateDeceleration(Vehicle v, int x, int y) {
        newX = x;
        if (v.getCurrentSpeed() > 0) {
            for (int i = 1; i <= v.getCurrentSpeed(); i++) {
                if (street[y][(x + i) % street[y].length] != null) {
                    v.setCurrentSpeed(i - 1);
                    LOG.debug("Geschwindigkeit von " + v + " um " + (i - 1) + " verringert");
                    newX = x + i-1;
                    break;
                }
            }
        }
        return newX ;
    }

    protected void simulateDawdling(Vehicle v) {
        int tmpCurrentSpeed;
        boolean dawdle;
        if ((tmpCurrentSpeed = v.getCurrentSpeed()) > 0) {
            if (tmpCurrentSpeed > 1) {
                dawdle = RandomProbabilityGenerator.generate(fastDawdleProbability);
            } else {
                dawdle = RandomProbabilityGenerator.generate(slowDawdleProbability);
            }
            if (dawdle) {
                v.decrementCurrentSpeed();
                LOG.debug(v + " trödelt");
            }
        }
    }

    @Override
    public Void call() {
        LOG.debug("Verarbeite von " + lowerBorder + " bis " + upperBorder);
        Vehicle v;
        for (int y = 0; y < street.length; y++) {
            for (int x = lowerBorder; x <= upperBorder; x++) {
                if ((v = street[y][x]) != null) {
                    simulateAcceleration(v);
                    x = simulateDeceleration(v, x, y);
                    simulateDawdling(v);
                }
            }
        }
        return null;
    }
}
