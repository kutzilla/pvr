package de.fhms.pvr.trafficsimulator.system;

import de.fhms.pvr.trafficsimulator.system.measure.TimeMeasureController;
import de.fhms.pvr.trafficsimulator.system.worker.AccelerateWorker;
import de.fhms.pvr.trafficsimulator.system.worker.DecelerateWorker;

import java.util.SplittableRandom;

import static de.fhms.pvr.trafficsimulator.system.measure.TimeMeasureType.*;

public class TrafficSimulator {

    volatile private Vehicle[][] street;

    private SplittableRandom randomGenerator;

    private double fastDawdleProbability;

    private double slowDawdleProbability;

    private double switchProbability;

    private int iteration;

    private int workerAmount;

    private TimeMeasureController timeMeasureController;

    private TrafficSimulator(TrafficSimulatorBuilder builder) {
        this.switchProbability = builder.switchProbability;
        this.slowDawdleProbability = builder.slowDawdleProbability;
        this.fastDawdleProbability = builder.fastDawdleProbability;
        this.randomGenerator = new SplittableRandom();
        this.timeMeasureController = new TimeMeasureController();
        this.workerAmount = 1;

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
        if (street[0].length > 1) {
            simulateTrackSwitching();
        }
        try {
            simulateAcceleration();
            simulateDeceleration();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        simulateDawdling();
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

    protected void simulateAcceleration() throws InterruptedException {
        AccelerateWorker workers[] = new AccelerateWorker[workerAmount];
        int bound = street[0].length / workerAmount;
        int index;
        for (int i = 0; i < workers.length; i++) {
            index = i * bound;
            workers[i] = new AccelerateWorker(this.street, index, index + bound);
        }

        timeMeasureController.startOrResume(ACCELERATION);
        for (AccelerateWorker worker : workers) {
            worker.start();
            worker.join();
        }
        timeMeasureController.suspend(ACCELERATION);
    }

    protected void simulateDeceleration() throws InterruptedException {
        int index, bound = street[0].length / workerAmount;
        DecelerateWorker workers[] = new DecelerateWorker[workerAmount];
        for (int i = 0; i < workers.length; i++) {
            index = i * bound;
            workers[i] = new DecelerateWorker(this.street, index, index + bound);
        }

        timeMeasureController.startOrResume(DECELERATION);
        for (DecelerateWorker worker : workers) {
            worker.start();
            worker.join();
        }
        timeMeasureController.suspend(DECELERATION);
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
        timeMeasureController.startOrResume(MOVEMENT);
        Vehicle tmp;
        int tmpIndex, tmpSpeed;
        for (int y = 0; y < street.length; y++) {
            for (int x = 0; x < street[y].length; x++) {
                if ((tmp = street[y][x]) != null && tmp.getMoveCount() < iteration) {
                    if ((tmpSpeed = tmp.getCurrentSpeed()) > 0) {
                        tmpIndex = (x + tmpSpeed) % street[y].length;
                        street[y][x] = null;
                        street[y][tmpIndex] = tmp;
                    }
                    tmp.incrementMoveCount();
                }
            }
        }
        timeMeasureController.suspend(MOVEMENT);
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
