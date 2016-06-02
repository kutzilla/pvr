package de.fhms.pvr.trafficsimulator.system;

import de.fhms.pvr.trafficsimulator.system.measure.TimeMeasureController;
import de.fhms.pvr.trafficsimulator.system.task.ActionSimulationTask;
import de.fhms.pvr.trafficsimulator.system.task.MovementSimulationTask;
import de.fhms.pvr.trafficsimulator.system.worker.AccelerateWorker;
import de.fhms.pvr.trafficsimulator.system.worker.DecelerateWorker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.SplittableRandom;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static de.fhms.pvr.trafficsimulator.system.measure.TimeMeasureType.*;

public class TrafficSimulator {

    private static final Logger LOG = LogManager.getLogger(TrafficSimulator.class);

    private static int threadAmount = 1;

    private static int taskAmount = 1;

    volatile private Vehicle[][] street;

    private SplittableRandom randomGenerator;

    private double fastDawdleProbability;

    private double slowDawdleProbability;

    private double switchProbability;

    private int iteration;

    private TimeMeasureController timeMeasureController;

    private ExecutorService executorService;

    protected TrafficSimulator(Vehicle[][] street) {
        this.street = street;
        this.iteration = 0;
        this.randomGenerator = new SplittableRandom();
        this.timeMeasureController = new TimeMeasureController();
    }

    public TrafficSimulator(int trackAmount, int sectionAmount, double vehicleDensity,
                            double slowDawdleProbability, double fastDawdleProbability, double switchProbability) {
        this.street = new Vehicle[trackAmount][sectionAmount];
        this.timeMeasureController = new TimeMeasureController();
        this.randomGenerator = new SplittableRandom();
        this.slowDawdleProbability = slowDawdleProbability;
        this.fastDawdleProbability = fastDawdleProbability;
        this.switchProbability = switchProbability;
        this.iteration = 0;
        initializeStreet(trackAmount, sectionAmount, vehicleDensity);
    }

    private void initializeStreet(int trackAmount, int sectionAmount, double vehicleDensity) {
        int vehicleCount = (int) ((double) trackAmount * sectionAmount * vehicleDensity);

        int randomTrackIndex, randomSectionIndex;
        Vehicle tmp;
        for (int i = 0; i < vehicleCount; i++) {
            tmp = new Vehicle(randomGenerator.nextInt(Vehicle.MAX_SPEED + 1));
            do {
                randomTrackIndex = randomGenerator.nextInt(trackAmount);
                randomSectionIndex = randomGenerator.nextInt(sectionAmount);
            } while (street[randomTrackIndex][randomSectionIndex] != null);
            street[randomTrackIndex][randomSectionIndex] = tmp;
        }
    }

    public void iterate() {
        timeMeasureController.startOrResume(ITERATION);
        this.iteration++;
        simulateAcceleration();
        simulateMovement();
        timeMeasureController.suspend(ITERATION);
    }

    protected void simulateTrackSwitching() {
        timeMeasureController.startOrResume(TRACK_SWITCHING);
        Vehicle currentVehicle;
        int switchTrackIndex, tmpSectionIndex;
        boolean switchTrack;
        for (int y = 0; y < street.length; y++) {
            for (int x = 0; x < street[y].length; x++) {
                if ((currentVehicle = street[y][x]) != null) {
                    // Prüfung der Fahrzeuge auf der eigenen Spur
                    for (int i = 1; i <= currentVehicle.getCurrentSpeed() + 1; i++) {
                        tmpSectionIndex = (x + i) % street[y].length;
                        // Blockierendes Fahrzeug befindet sich auf der eignen Spur
                        if (street[y][tmpSectionIndex] != null) {
                            // Reset von Wechselspur
                            switchTrackIndex = -1;
                            // Prüfung der Spur oberhalb
                            if (y > 0 && isSwitchToTrackPossible(y - 1, x, currentVehicle.getCurrentSpeed())) {
                                switchTrackIndex = y - 1;
                            }
                            // Prüfung der Spur unterhalb und ein Wechsel
                            // oberhalb nicht bereits möglich wäre
                            if (y < street.length - 1 && switchTrackIndex < 0 &&
                                    isSwitchToTrackPossible(y + 1, x, currentVehicle.getCurrentSpeed())) {
                                switchTrackIndex = y + 1;
                            }

                            // Wechsel möglich
                            if (switchTrackIndex >= 0) {
                                switchTrack = (((double) randomGenerator.nextInt(100)) / 100) <= switchProbability;
                                // Ausführung des Wechsels
                                if (switchTrack) {
                                    street[y][x] = null;
                                    street[switchTrackIndex][x] = currentVehicle;
                                }
                            }
                        }
                    }
                }
            }
        }
        timeMeasureController.suspend(TRACK_SWITCHING);
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

    protected void simulateAcceleration() {
        ArrayList<ActionSimulationTask> tasks = new ArrayList<>();
        int bound;
        bound = street[0].length / taskAmount;
        if (street[0].length % taskAmount != 0) {
            bound += 1;
        }
        int index = 0;
        for (int i = 0; i < taskAmount; i++) {
            if (i < taskAmount - 1) {
                tasks.add(new ActionSimulationTask(this, index, index + bound - 1));
                LOG.debug("Task von " + index + " bis " + (index + bound - 1) + " angelegt");
            } else {
                tasks.add(new ActionSimulationTask(this, index, street[0].length - 1));
                LOG.debug("Task von " + index + " bis " + (street[0].length - 1) + " angelegt");
            }
            index += bound;
        }
        executorService = Executors.newFixedThreadPool(threadAmount);
        try {
            executorService.invokeAll(tasks);
        } catch (InterruptedException e) {
            LOG.error(e.getMessage());
        } finally {
            executorService.shutdown();
        }
    }

    protected void simulateDeceleration() throws InterruptedException {

    }

    protected void simulateDawdling() {
        timeMeasureController.startOrResume(DAWDLING);
        boolean dawdle;
        int tmpCurrentSpeed;
        Vehicle tmp;
        for (int y = 0; y < street.length; y++) {
            for (int x = 0; x < street[y].length; x++) {
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
        timeMeasureController.suspend(DAWDLING);
    }

    protected void simulateMovement() {
        ArrayList<MovementSimulationTask> tasks = new ArrayList<>();
        int bound;
        bound = street[0].length / taskAmount;
        if (street[0].length % taskAmount != 0) {
            bound += 1;
        }
        int index = 0;
        for (int i = 0; i < taskAmount; i++) {
            if (i < taskAmount - 1) {
                tasks.add(new MovementSimulationTask(this, index, index + bound - 1));
                LOG.debug("Task von " + index + " bis " + (index + bound - 1) + " angelegt");
            } else {
                tasks.add(new MovementSimulationTask(this, index, street[0].length - 1));
                LOG.debug("Task von " + index + " bis " + (street[0].length - 1) + " angelegt");
            }
            index += bound;
        }
        executorService = Executors.newFixedThreadPool(threadAmount);
        try {
            executorService.invokeAll(tasks);
        } catch (InterruptedException e) {
            LOG.error(e.getMessage());
        } finally {
            executorService.shutdown();
        }
    }

    public Vehicle[][] getStreet() {
        return street;
    }

    public TimeMeasureController getTimeMeasureController() {
        return timeMeasureController;
    }

    public int getIteration() {
        return iteration;
    }

    protected void setIteration(int iteration) {
        this.iteration = iteration;
    }

    protected void setStreet(Vehicle[][] street) {
        this.street = street;
    }

    protected void setFastDawdleProbability(double fastDawdleProbability) {
        this.fastDawdleProbability = fastDawdleProbability;
    }

    protected void setSlowDawdleProbability(double slowDawdleProbability) {
        this.slowDawdleProbability = slowDawdleProbability;
    }

    protected void setSwitchProbability(double switchProbability) {
        this.switchProbability = switchProbability;
    }

    public double getFastDawdleProbability() {
        return fastDawdleProbability;
    }

    public double getSlowDawdleProbability() {
        return slowDawdleProbability;
    }

    public static int getThreadAmount() {
        return threadAmount;
    }

    public static void setThreadAmount(int threadAmount) {
        TrafficSimulator.threadAmount = threadAmount;
    }

    public static int getTaskAmount() {
        return taskAmount;
    }

    public static void setTaskAmount(int taskAmount) {
        TrafficSimulator.taskAmount = taskAmount;
    }

    public SplittableRandom getRandomGenerator() {
        return randomGenerator;
    }

    public double getSwitchProbability() {
        return switchProbability;
    }
}
