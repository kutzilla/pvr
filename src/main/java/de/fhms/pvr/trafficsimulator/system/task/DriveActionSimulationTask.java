package de.fhms.pvr.trafficsimulator.system.task;

import de.fhms.pvr.trafficsimulator.system.TrafficSimulator;
import de.fhms.pvr.trafficsimulator.system.Vehicle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.SplittableRandom;

public class DriveActionSimulationTask extends AbstractSimulationTask {

    private static final Logger LOG = LogManager.getLogger(DriveActionSimulationTask.class);

    private static SplittableRandom randomGenerator = new SplittableRandom();

    private double fastDawdleProbability;

    private double slowDawdleProbability;

    private double switchProbability;

    public DriveActionSimulationTask(Vehicle[][] street, int lowerBound, int upperBound,
                                     double fastDawdleProbability, double slowDawdleProbability,
                                     double switchProbability) {
        super(street, lowerBound, upperBound);
        this.fastDawdleProbability = fastDawdleProbability;
        this.slowDawdleProbability = slowDawdleProbability;
        this.switchProbability = switchProbability;
    }

    @Override
    public Void call() {
        LOG.debug("Verarbeite von " + lowerBound + " bis " + upperBound);
        Vehicle tmp;
        int tmpCurrentSpeed, tmpIndex;
        int tmpSectionIndex, switchTrackIndex;
        boolean switchTrack;
        boolean dawdle;
        for (int y = 0; y < street.length; y++) {
            for (int x = lowerBound; x <= upperBound; x++) {
                tmp = street[y][x];
                if (tmp != null) {
                    // Prüfung der Fahrzeuge auf der eigenen Spur
                    for (int i = 1; i <= tmp.getCurrentSpeed() + 1; i++) {
                        tmpSectionIndex = (x + i) % street[y].length;
                        // Blockierendes Fahrzeug befindet sich auf der eignen Spur
                        if (street[y][tmpSectionIndex] != null) {
                            // Reset von Wechselspur
                            switchTrackIndex = -1;
                            // Prüfung der Spur oberhalb
                            if (y > 0 && isSwitchToTrackPossible(y - 1, x, tmp.getCurrentSpeed())) {
                                switchTrackIndex = y - 1;
                            }
                            // Prüfung der Spur unterhalb und ein Wechsel
                            // oberhalb nicht bereits möglich wäre
                            if (y < street.length - 1 && switchTrackIndex < 0 &&
                                    isSwitchToTrackPossible(y + 1, x, tmp.getCurrentSpeed())) {
                                switchTrackIndex = y + 1;
                            }

                            // Wechsel möglich
                            if (switchTrackIndex >= 0) {
                                switchTrack = (((double) randomGenerator.nextInt(100)) / 100) <= switchProbability;
                                // Ausführung des Wechsels
                                if (switchTrack) {
                                    street[y][x] = null;
                                    street[switchTrackIndex][x] = tmp;
                                }
                            }
                        }
                    }
                }

                if (tmp != null) {
                    tmp.incrementCurrentSpeed();
                    LOG.debug("Geschwindigkeit von " + tmp + " erhöht");
                }
                if (tmp != null) {
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
                if ((tmp = street[y][x]) != null && (tmpCurrentSpeed = tmp.getCurrentSpeed()) > 0) {
                    if (tmpCurrentSpeed > 1) {
                        dawdle = (((double) randomGenerator.nextInt(100)) / 100) <= fastDawdleProbability;
                    } else {
                        dawdle = (((double) randomGenerator.nextInt(100)) / 100) <= slowDawdleProbability;
                    }
                    if (dawdle) {
                        tmp.decrementCurrentSpeed();
                    }
                }
            }
        }
        return null;
    }

    private boolean isSwitchToTrackPossible(int trackIndex, int sectionIndex, int currentSpeed) {
        int startIndex = sectionIndex - Vehicle.MAX_SPEED;
        if (startIndex < 0) {
            startIndex = street[0].length - startIndex - 1;
        }
        startIndex = startIndex % street[trackIndex].length;
        int tmpIndex;
        for (int x = 1; x <= Vehicle.MAX_SPEED + currentSpeed + 1; x++) {
            tmpIndex = (startIndex + x) % street[trackIndex].length;
            if (street[trackIndex][tmpIndex] != null) {
                return false;
            }
        }
        return true;
    }
}