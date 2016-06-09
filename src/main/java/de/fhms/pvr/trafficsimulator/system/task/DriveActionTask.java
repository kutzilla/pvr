package de.fhms.pvr.trafficsimulator.system.task;

import de.fhms.pvr.trafficsimulator.system.Vehicle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.SplittableRandom;

public class DriveActionTask extends SimulationTask {

    private static final Logger LOG = LogManager.getLogger(DriveActionTask.class);

    private static SplittableRandom randomGenerator = new SplittableRandom();

    private double fastDawdleProbability;

    private double slowDawdleProbability;

    private double switchProbability;

    public DriveActionTask(Vehicle[][] street, int lowerBorder, int upperBorder,
                           double fastDawdleProbability, double slowDawdleProbability,
                           double switchProbability) {
        super(street, lowerBorder, upperBorder);
        this.fastDawdleProbability = fastDawdleProbability;
        this.slowDawdleProbability = slowDawdleProbability;
        this.switchProbability = switchProbability;
    }

    protected void simulateTrackSwitching(int x, int y) {

    }

    protected void simulateAcceleration(int x, int y) {
        Vehicle v;
        if ((v = street[y][x]) != null) {
            v.incrementCurrentSpeed();
            LOG.debug("Geschwindigkeit von " + v + " erhöht");
        }
    }

    protected void simulateDeceleration(int x, int y) {
        Vehicle tmp;
        int tmpCurrentSpeed, tmpIndex;
        if ((tmp  = street[y][x]) != null) {
            tmpCurrentSpeed = tmp.getCurrentSpeed();
            if (tmpCurrentSpeed > 0) {
                for (int i = 1; i <= tmp.getCurrentSpeed(); i++) {
                    tmpIndex = (x + i) % street[y].length;
                    if (street[y][tmpIndex] != null) {
                        tmp.setCurrentSpeed(i - 1);
                        LOG.debug("Geschwindigkeit von " + tmp + " um " + (i - 1) + " verringert");
                        break;
                    }
                }
            }
        }
    }

    protected void simulateDawdling(int x, int y) {
        Vehicle tmp;
        int tmpCurrentSpeed;
        boolean dawdle;
        if ((tmp = street[y][x]) != null && (tmpCurrentSpeed = tmp.getCurrentSpeed()) > 0) {
            if (tmpCurrentSpeed > 1) {
                dawdle = (((double) randomGenerator.nextInt(100)) / 100) <= fastDawdleProbability;
            } else {
                dawdle = (((double) randomGenerator.nextInt(100)) / 100) <= slowDawdleProbability;
            }
            if (dawdle) {
                tmp.decrementCurrentSpeed();
                LOG.debug(tmp + " trödelt");
            }
        }
    }

    @Override
    public Void call() {
        LOG.debug("Verarbeite von " + lowerBorder + " bis " + upperBorder);

        for (int y = 0; y < street.length; y++) {
            for (int x = lowerBorder; x <= upperBorder; x++) {
                if (street[y][x] != null) {
                    if (street.length > 1) {
                        simulateTrackSwitching(x, y);
                    }
                    simulateAcceleration(x, y);
                    simulateDeceleration(x, y);
                    simulateDawdling(x, y);
                }
            }
        }
        return null;
    }
}