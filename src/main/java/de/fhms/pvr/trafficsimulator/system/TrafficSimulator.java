package de.fhms.pvr.trafficsimulator.system;

import de.fhms.pvr.trafficsimulator.system.measure.TimeMeasureController;
import de.fhms.pvr.trafficsimulator.system.task.DriveActionSimulationTask;
import de.fhms.pvr.trafficsimulator.system.task.MovementSimulationTask;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.SplittableRandom;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    private int iteration;

    private int taskAmount;

    private int workerAmount;

    private TimeMeasureController timeMeasureController;

    private ExecutorService executorService;

    private TrafficSimulator(TrafficSimulatorBuilder builder) {
        this.switchProbability = builder.switchProbability;
        this.slowDawdleProbability = builder.slowDawdleProbability;
        this.fastDawdleProbability = builder.fastDawdleProbability;
        this.taskAmount = builder.taskAmount;
        this.workerAmount = builder.workerAmount;
        this.timeMeasureController = new TimeMeasureController();

        if (builder.street != null) {
            this.street = builder.street;
        } else {
            createRandomStreet(builder.trackAmount, builder.sectionAmount, builder.vehicleDensity);
        }
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

    public void iterate() {
        timeMeasureController.startOrResume(ITERATION);
        this.iteration++;
        simulateDriveAction();
        simulateMovement();
        timeMeasureController.suspend(ITERATION);
    }

    protected void simulateDriveAction() {
        ArrayList<DriveActionSimulationTask> tasks = new ArrayList<>();
        int bound;
        bound = street[0].length / taskAmount;
        if (street[0].length % taskAmount != 0) {
            bound += 1;
        }
        int index = 0;
        for (int i = 0; i < taskAmount; i++) {
            if (i < taskAmount - 1) {
                tasks.add(new DriveActionSimulationTask(street, index, index + bound - 1, fastDawdleProbability,
                        slowDawdleProbability, switchProbability));
                LOG.debug("Task von " + index + " bis " + (index + bound - 1) + " angelegt");
            } else {
                tasks.add(new DriveActionSimulationTask(street, index, street[0].length - 1, fastDawdleProbability,
                        slowDawdleProbability, switchProbability));
                LOG.debug("Task von " + index + " bis " + (street[0].length - 1) + " angelegt");
            }
            index += bound;
        }
        executorService = Executors.newFixedThreadPool(workerAmount);
        timeMeasureController.startOrResume(DRIVE_ACTION);
        try {
            executorService.invokeAll(tasks);
        } catch (InterruptedException e) {
            LOG.error(e.getMessage());
        } finally {
            executorService.shutdown();
        }
        timeMeasureController.suspend(DRIVE_ACTION);
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
                tasks.add(new MovementSimulationTask(street, index, index + bound - 1, iteration));
                LOG.debug("Task von " + index + " bis " + (index + bound - 1) + " angelegt");
            } else {
                tasks.add(new MovementSimulationTask(street, index, street[0].length - 1, iteration));
                LOG.debug("Task von " + index + " bis " + (street[0].length - 1) + " angelegt");
            }
            index += bound;
        }
        executorService = Executors.newFixedThreadPool(workerAmount);
        timeMeasureController.startOrResume(MOVEMENT);
        try {
            executorService.invokeAll(tasks);
        } catch (InterruptedException e) {
            LOG.error(e.getMessage());
        } finally {
            executorService.shutdown();
        }
        timeMeasureController.suspend(MOVEMENT);
    }

    public Vehicle[][] getStreet() {
        return street;
    }

    public TimeMeasureController getTimeMeasureController() {
        return timeMeasureController;
    }

    protected void setIteration(int iteration) {
        this.iteration = iteration;
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

        public TrafficSimulatorBuilder withVehicleDensity(double vehicleDensity) {
            this.vehicleDensity = vehicleDensity;
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
