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

    private int vehicleAmount;

    private int workerAmount;

    private TimeMeasureController timeMeasureController;

    private ExecutorService executorService;

    private ArrayList<SimulationTask> driveActionTasks;

    private ArrayList<SimulationTask> movementTasks;

    private ArrayList<SimulationTask> trackSwitchingTasks;

    private TrafficSimulator(TrafficSimulatorBuilder builder) {
        this.switchProbability = builder.switchProbability;
        this.slowDawdleProbability = builder.slowDawdleProbability;
        this.fastDawdleProbability = builder.fastDawdleProbability;
        this.timeMeasureController = new TimeMeasureController();
        this.driveActionTasks = new ArrayList<>();
        this.movementTasks = new ArrayList<>();
        this.trackSwitchingTasks = new ArrayList<>();
        this.workerAmount = builder.workerAmount;
        this.executorService = Executors.newFixedThreadPool(workerAmount);

        if(builder.vehicleAmount == 0) {
            this.vehicleAmount = (int) ((double) builder.trackAmount * builder.sectionAmount * builder.vehicleDensity);
        }else{
            this.vehicleAmount = builder.vehicleAmount;
        }

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
        Vehicle tmp;
        for (int i = 0; i < vehicleAmount; i++) {
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
        timeMeasureController.startOrResume(KAPPA);
        if (street.length > 1) {
            simulateTrackSwitching();
        }
        simulateDriveAction();
        simulateMovement();
        timeMeasureController.suspend(KAPPA);
    }

    protected void simulateDriveAction() {
        simulateTasks(driveActionTasks);
    }
    
    protected void simulateMovement() {
        simulateTasks(movementTasks);
    }

    protected void simulateTrackSwitching() {
        simulateTasks(trackSwitchingTasks);
    }

    private void simulateTasks(ArrayList<SimulationTask> tasks) {
        timeMeasureController.suspend(KAPPA);
        try {
            timeMeasureController.startOrResume(PHI);
            executorService.invokeAll(tasks);
            timeMeasureController.suspend(PHI);
        } catch (InterruptedException e) {
            LOG.error(e.getMessage());
        }
        timeMeasureController.startOrResume(KAPPA);
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

    public int getTrackAmount() {
        return street.length;
    }

    public int getSectionAmount() {
        return street[0].length;
    }

    public int getTotalSectionAmount() {
        return street.length * street[0].length;
    }

    public int getVehicleAmount() {
        return vehicleAmount;
    }

    public int getWorkerAmount() {
        return workerAmount;
    }


    public static class TrafficSimulatorBuilder {

        private int trackAmount;

        private int sectionAmount;

        private Vehicle[][] street;

        private double switchProbability;

        private double fastDawdleProbability;

        private double slowDawdleProbability;

        private double vehicleDensity;

        private int vehicleAmount;

        private int workerAmount;

        private int taskAmount;

        public TrafficSimulatorBuilder(Vehicle[][] street) {
            this.street = street;
            this.trackAmount = this.street.length;
            this.sectionAmount = this.street[0].length;
            this.workerAmount = 1;
            this.taskAmount = 1;
        }

        public TrafficSimulatorBuilder(int trackAmount, int sectionAmount) {
            this.trackAmount = trackAmount;
            this.sectionAmount = sectionAmount;
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

        public TrafficSimulatorBuilder withAbsoluteVehicleDensity(int  vehicleAmount){
            this.vehicleAmount = vehicleAmount;
            return this;
        }

        public TrafficSimulatorBuilder withRelativeVehicleDensity(double vehicleDensity){
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
