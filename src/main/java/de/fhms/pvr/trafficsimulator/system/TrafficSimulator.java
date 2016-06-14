package de.fhms.pvr.trafficsimulator.system;

import de.fhms.pvr.trafficsimulator.system.measure.TimeMeasureController;
import de.fhms.pvr.trafficsimulator.system.task.SimulationTask;
import de.fhms.pvr.trafficsimulator.system.task.DriveActionTask;
import de.fhms.pvr.trafficsimulator.system.task.MovementTask;
import de.fhms.pvr.trafficsimulator.system.task.TrackSwitchingTask;
import de.fhms.pvr.trafficsimulator.system.util.SimulationTaskSplitter;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.SplittableRandom;
import java.util.concurrent.*;

import static de.fhms.pvr.trafficsimulator.system.measure.TimeMeasureType.*;

public class TrafficSimulator {

    private static final Logger LOG = LogManager.getLogger(TrafficSimulator.class);

    private static final SplittableRandom randomGenerator = new SplittableRandom();

    private Vehicle[][] street;

    private double fastDawdleProbability;

    private double slowDawdleProbability;

    private double switchProbability;

    private TimeMeasureController timeMeasureController;

    private ExecutorService executorService;

    private ArrayList<SimulationTask> driveActionTasks;

    private ArrayList<SimulationTask> movementTasks;

    private ArrayList<SimulationTask> trackSwitchingTasks;

    private TrafficSimulator(TrafficSimulatorBuilder builder) {
        this.switchProbability = builder.switchProbability;
        this.slowDawdleProbability = builder.slowDawdleProbability;
        this.fastDawdleProbability = builder.fastDawdleProbability;
        this.driveActionTasks = new ArrayList<>();
        this.movementTasks = new ArrayList<>();
        this.trackSwitchingTasks = new ArrayList<>();
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
        ArrayList<Pair<Integer, Integer>> pairs = SimulationTaskSplitter
                .getSimulationTaskBordersFor(sectionAmount, taskAmount);
        for (Pair<Integer, Integer> p : pairs) {
            trackSwitchingTasks.add(new TrackSwitchingTask(street, p.getLeft(), p.getRight(), switchProbability));
            driveActionTasks.add(new DriveActionTask(street, p.getLeft(), p.getRight(),
                    fastDawdleProbability, slowDawdleProbability));
            movementTasks.add(new MovementTask(street, p.getLeft(), p.getRight()));
        }
    }

    public void iterate() {
        timeMeasureController.startOrResume(ITERATION);
        if (street.length > 1) {
            simulateTrackSwitching();
        }
        simulateDriveAction();
        simulateMovement();
        timeMeasureController.suspend(ITERATION);
    }

    protected void simulateDriveAction() {
        timeMeasureController.startOrResume(DRIVE_ACTION);
        simulateTasks(driveActionTasks);
        timeMeasureController.suspend(DRIVE_ACTION);
    }
    
    protected void simulateMovement() {
        timeMeasureController.startOrResume(MOVEMENT);
        simulateTasks(movementTasks);
        timeMeasureController.suspend(MOVEMENT);
    }

    protected void simulateTrackSwitching() {
        timeMeasureController.startOrResume(TRACK_SWITCHING);
        simulateTasks(trackSwitchingTasks);
        timeMeasureController.suspend(TRACK_SWITCHING);
    }

    private void simulateTasks(ArrayList<SimulationTask> tasks) {
        try {
            executorService.invokeAll(tasks);
        } catch (InterruptedException e) {
            LOG.error(e.getMessage());
        }
    }

    public void shutdown() {
        this.executorService.shutdown();
        try {
            this.executorService.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
