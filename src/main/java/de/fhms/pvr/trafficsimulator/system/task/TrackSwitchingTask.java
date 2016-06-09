package de.fhms.pvr.trafficsimulator.system.task;

import de.fhms.pvr.trafficsimulator.system.Vehicle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.SplittableRandom;

public class TrackSwitchingTask extends SimulationTask {

    private static final Logger LOG = LogManager.getLogger(TrackSwitchingTask.class);

    private static final SplittableRandom randomGenerator = new SplittableRandom();

    private double switchProbability;

    public TrackSwitchingTask(Vehicle[][] street, int lowerBorder, int upperBorder, double switchProbability) {
        super(street, lowerBorder, upperBorder);
        this.switchProbability = switchProbability;
    }

    private boolean isSwitchToTrackPossible(int trackIndex, int sectionIndex, int currentSpeed) {
        if (street[trackIndex][sectionIndex] != null) {
            return false;
        }

        int startIndex = sectionIndex - Vehicle.MAX_SPEED;
        if (startIndex < 0) {
            startIndex = street[0].length + startIndex;
        }
        startIndex = startIndex % street[trackIndex].length;

        int tmpIndex;
        for (int x = 0; x <= Vehicle.MAX_SPEED; x++) {
            tmpIndex = (startIndex + x) % street[trackIndex].length;
            if (street[trackIndex][tmpIndex] != null) {
                return false;
            }
        }
        for (int x = 1; x <= currentSpeed + 1; x++) {
            tmpIndex = (sectionIndex + x) % street[trackIndex].length;
            if (street[trackIndex][tmpIndex] != null) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Void call() {
        for (int y = 0; y < street.length; y++) {
            for (int x = lowerBorder; x <= upperBorder; x++) {
                Vehicle tmp = street[y][x];
                int tmpSectionIndex, switchTrackIndex;
                boolean switchTrack;
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
                                    LOG.debug(tmp + " wechselt von " + y + " auf die Spur " + switchTrackIndex);
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
}
