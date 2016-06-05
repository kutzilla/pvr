package de.fhms.pvr.trafficsimulator.system;

import de.fhms.pvr.trafficsimulator.system.measure.TimeMeasureController;
import de.fhms.pvr.trafficsimulator.system.task.AbstractSimulationTask;
import de.fhms.pvr.trafficsimulator.system.task.DriveActionSimulationTask;
import de.fhms.pvr.trafficsimulator.system.task.MovementSimulationTask;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static de.fhms.pvr.trafficsimulator.system.measure.TimeMeasureType.DRIVE_ACTION;
import static de.fhms.pvr.trafficsimulator.system.measure.TimeMeasureType.ITERATION;
import static de.fhms.pvr.trafficsimulator.system.measure.TimeMeasureType.MOVEMENT;

public class TrafficSimulator {

    private static final Logger LOG = LogManager.getLogger(TrafficSimulator.class);

    private static final SplittableRandom randomGenerator = new SplittableRandom();

    volatile private Vehicle[][] street;

    private double fastDawdleProbability;

    private double slowDawdleProbability;

    private double switchProbability;

    private TimeMeasureController timeMeasureController;

    private ExecutorService executorService;

    private ArrayList<AbstractSimulationTask> driveActionSimulationTasks;

    private ArrayList<AbstractSimulationTask> movementSimulationTasks;

    private TrafficSimulator(TrafficSimulatorBuilder builder) {
        this.switchProbability = builder.switchProbability;
        this.slowDawdleProbability = builder.slowDawdleProbability;
        this.fastDawdleProbability = builder.fastDawdleProbability;
        this.driveActionSimulationTasks = new ArrayList<>();
        this.movementSimulationTasks = new ArrayList<>();
        this.timeMeasureController = new TimeMeasureController();
        this.executorService = Executors.newFixedThreadPool(builder.workerAmount);

        if (builder.street != null) {
            this.street = builder.street;
        } else {
            createRandomStreet(builder.trackAmount, builder.sectionAmount, builder.vehicleDensity);
        }
        fillTaskLists(builder.sectionAmount, builder.taskAmount);
    }

    private void createRandomStreet(int trackAmount, int sectionAmount, double vehicleDensity) {
        street = new Vehicle[trackAmount][sectionAmount];
        int randomTrackIndex, randomSectionIndex;
        int vehicleCount = (int) ((double) trackAmount * sectionAmount * vehicleDensity);
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

    private void fillTaskLists(int sectionAmount, int taskAmount) {
        int bound = sectionAmount / taskAmount;
        if (sectionAmount % taskAmount != 0) {
            bound += 1;
        }
        int index = 0;
        for (int i = 0; i < taskAmount; i++) {
            if (i < taskAmount - 1) {
                driveActionSimulationTasks.add(new DriveActionSimulationTask(street, index, index + bound - 1,
                        fastDawdleProbability, slowDawdleProbability, switchProbability));
                movementSimulationTasks.add(new MovementSimulationTask(street, index, index + bound - 1));
                LOG.debug("Task von " + index + " bis " + (index + bound - 1) + " angelegt");
            } else {
                driveActionSimulationTasks.add(new DriveActionSimulationTask(street, index, street[0].length - 1,
                        fastDawdleProbability, slowDawdleProbability, switchProbability));
                movementSimulationTasks.add(new MovementSimulationTask(street, index, street[0].length - 1));
                LOG.debug("Task von " + index + " bis " + (street[0].length - 1) + " angelegt");
            }
            index += bound;
        }
    }

    public void iterate() {
        timeMeasureController.startOrResume(ITERATION);
        simulateDriveAction();
        simulateMovement();
        timeMeasureController.suspend(ITERATION);
    }

    protected void simulateDriveAction() {
        LOG.debug(driveActionSimulationTasks.toString());
        timeMeasureController.startOrResume(DRIVE_ACTION);
        try {
            executorService.invokeAll(driveActionSimulationTasks);
        } catch (InterruptedException e) {
            LOG.error(e.getMessage());
        }
        timeMeasureController.suspend(DRIVE_ACTION);
        LOG.debug(driveActionSimulationTasks.toString());
    }
    
    protected void simulateMovement() {
        timeMeasureController.startOrResume(MOVEMENT);
        try {
            executorService.invokeAll(movementSimulationTasks);
        } catch (InterruptedException e) {
            LOG.error(e.getMessage());
        }
        timeMeasureController.suspend(MOVEMENT);
    }

    public void shutdown() {
        this.executorService.shutdown();
    }

    public Vehicle[][] getStreet() {
        return street;
    }

    public TimeMeasureController getTimeMeasureController() {
        return timeMeasureController;
    }


    public static class TrafficSimulatorBuilder {

        private int trackAmount;

        private int sectionAmount;

        private Vehicle[][] street;

        private double switchProbability;

        private double fastDawdleProbability;

        private double slowDawdleProbability;

        private double vehicleDensity;

        private int workerAmount;

        private int taskAmount;

        public TrafficSimulatorBuilder(Vehicle[][] street) {
            this.street = street;
            this.trackAmount = this.street.length;
            this.sectionAmount = this.street[0].length;
            this.workerAmount = 1;
            this.taskAmount = 1;
        }

        public TrafficSimulatorBuilder(int trackAmount, int sectionAmount, double vehicleDensity) {
            this.trackAmount = trackAmount;
            this.sectionAmount = sectionAmount;
            this.vehicleDensity = vehicleDensity;
            this.workerAmount = 1;
            this.taskAmount = 1;
        }

        public TrafficSimulatorBuilder withSwitchProbability(double switchProbability) {
            this.switchProbability = switchProbability;
            return this;
        }

        public TrafficSimulatorBuilder withFastDawdleProbability(double fastDawdleProbability) {
            this.fastDawdleProbability = fastDawdleProbability;
            return this;
        }

        public TrafficSimulatorBuilder withSlowDawdleProbability(double slowDawdleProbability) {
            this.slowDawdleProbability = slowDawdleProbability;
            return this;
        }

        public TrafficSimulatorBuilder withWorkerAmount(int workerAmount) {
            this.workerAmount = workerAmount;
            return this;
        }

        public TrafficSimulatorBuilder withTaskAmount(int taskAmount) {
            this.taskAmount = taskAmount;
            return this;
        }

        public TrafficSimulator build() {
            return new TrafficSimulator(this);
        }
    }
}
