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

    private TrafficSimulator(TrafficSimulatorBuilder builder) {
        this.switchProbability = builder.switchProbability;
        this.slowDawdleProbability = builder.slowDawdleProbability;
        this.fastDawdleProbability = builder.fastDawdleProbability;
        this.randomGenerator = new SplittableRandom();
        this.timeMeasureController = new TimeMeasureController();

        if (builder.street != null) {
            this.street = builder.street;
        } else {
            createRandomStreet(builder.trackAmount, builder.sectionAmount, builder.vehicleDensity);
        }
    }

    private ExecutorService executorService;

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
        simulateAcceleration();
        simulateMovement();
        timeMeasureController.suspend(ITERATION);
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

    public SplittableRandom getRandomGenerator() {
        return randomGenerator;
    }

    public double getFastDawdleProbability() {
        return fastDawdleProbability;
    }

    public double getSlowDawdleProbability() {
        return slowDawdleProbability;
    }

    public double getSwitchProbability() {
        return switchProbability;
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
