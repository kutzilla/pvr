package de.fhms.pvr.trafficsimulator.system;

import de.fhms.pvr.trafficsimulator.system.worker.AccelerateWorker;

import java.util.SplittableRandom;
import java.util.concurrent.ExecutorService;

public class TrafficSimulator {

    volatile private Vehicle[][] street;

    private SplittableRandom randomGenerator;

    private double fastDawdleProbability;

    private double slowDawdleProbability;

    private double switchProbability;

    private int iteration;

    private long totalDecelerationTime;

    private long totalSwitchingTrackTime;

    private long totalAccelerateTime;

    private long totalDawdleTime;

    private long totalMovementTime;

    private long totalSimulationTime;

    private int bound;

    private ExecutorService executorService;

    private int threadAmount;

    private AccelerateWorker[] accelerateWorkers;

    protected TrafficSimulator(Vehicle[][] street) {
        this.street = street;
        this.iteration = 0;
        this.randomGenerator = new SplittableRandom();
        this.threadAmount = 2;
        this.bound = street[0].length / threadAmount;
    }

    public TrafficSimulator(int trackAmount, int sectionAmount, double vehicleDensity,
                                double slowDawdleProbability, double fastDawdleProbability, double switchProbability,
                                    int threadAmount) {
        this.street = new Vehicle[trackAmount][sectionAmount];
        this.randomGenerator = new SplittableRandom();
        this.slowDawdleProbability = slowDawdleProbability;
        this.fastDawdleProbability = fastDawdleProbability;
        this.switchProbability = switchProbability;
        this.iteration = 0;
        int vehicleCount = (int) ((double) trackAmount * sectionAmount * vehicleDensity);

        int randomTrackIndex, randomSectionIndex;
        Vehicle tmp;
        for (int i = 0; i < vehicleCount; i++) {
            tmp = new Vehicle(randomGenerator.nextInt(Vehicle.MAX_SPEED + 1));
            do {
                randomTrackIndex = randomGenerator.nextInt(trackAmount);
                randomSectionIndex = randomGenerator.nextInt(sectionAmount);
            } while(street[randomTrackIndex][randomSectionIndex] != null);
            street[randomTrackIndex][randomSectionIndex] = tmp;
        }

        this.threadAmount = threadAmount;
        this.bound = sectionAmount / threadAmount;
    }

    public void iterate() {
        long before = System.currentTimeMillis();
        this.iteration++;
        if (street[0].length > 1) {
            simulateTrackSwitching();
        }
        try {
            simulateAcceleration();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        simulateDeceleration();
        simulateDawdling();
        simulateMovement();
        totalSimulationTime += System.currentTimeMillis() - before;
    }

    protected void simulateTrackSwitching() {
        long before = System.currentTimeMillis();
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
        totalSwitchingTrackTime += System.currentTimeMillis() - before;
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
        AccelerateWorker workers[] = new AccelerateWorker[threadAmount];
        int index;
        for (int i = 0; i < workers.length; i++) {
            index = i * bound;
            workers[i] = new AccelerateWorker(this.street, index, index + bound);
        }

        long beforeAccelerate = System.currentTimeMillis();
        for (AccelerateWorker worker: workers) {
            worker.start();
            worker.join();
        }

        totalAccelerateTime += System.currentTimeMillis() - beforeAccelerate;
    }

    protected void simulateDeceleration() {
        long beforeBreak = System.currentTimeMillis();
        Vehicle tmp;
        int tmpSpeed, tmpIndex;
        for (int y = 0; y < street.length; y++) {
            for (int x = 0; x < street[y].length; x++) {
                if ((tmp = street[y][x]) != null) {
                    tmpSpeed = tmp.getCurrentSpeed();
                    if (tmpSpeed > 0) {
                        for (int i = 1; i <= tmp.getCurrentSpeed(); i++) {
                            tmpIndex = (x + i) % street[y].length;
                            if (street[y][tmpIndex] != null) {
                                tmp.setCurrentSpeed(i - 1);
                                break;
                            }
                        }
                    }
                }
            }
        }
        totalDecelerationTime += System.currentTimeMillis() - beforeBreak;
    }

    protected void simulateDawdling() {
        long beforeLinder = System.currentTimeMillis();
        boolean linders;
        int tmpCurrentSpeed;
        Vehicle tmp;
        for (int y = 0; y < street.length; y++) {
            for (int x = 0; x < street[y].length; x++) {
                if ((tmp = street[y][x]) != null && (tmpCurrentSpeed = tmp.getCurrentSpeed()) > 0) {
                    if (tmpCurrentSpeed > 1) {
                        linders = (((double) randomGenerator.nextInt(100)) / 100) <= fastDawdleProbability;
                    } else {
                        linders = (((double) randomGenerator.nextInt(100)) / 100) <= slowDawdleProbability;
                    }
                    if (linders) {
                        tmp.decrementCurrentSpeed();
                    }
                }
            }
        }
        totalDawdleTime += System.currentTimeMillis() - beforeLinder;
    }

    protected void simulateMovement() {
        long beforeMove = System.currentTimeMillis();
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
        totalMovementTime += System.currentTimeMillis() - beforeMove;
    }

    public void printStreetField() {
        Vehicle tmp;
        for (int i = 0; i < street[0].length; i++) {
            System.out.print("|" + (i % 10) + "| ");
        }
        System.out.println("\r\n");
        for (int i = 0; i < street.length; i++) {
            for (int j = 0; j < street[i].length; j++) {
                tmp = street[i][j];
                if (tmp != null && tmp.getCurrentSpeed() >= 0) {
                    System.out.print("[" + tmp.getCurrentSpeed() + "] ");
                } else {
                    System.out.print("[ ] ");
                }
            }
            System.out.println();
        }
        System.out.println();
    }

    public Vehicle[][] getStreet() {
        return street;
    }

    public int getIteration() {
        return iteration;
    }

    public long getTotalSimulationTime() {
        return totalSimulationTime;
    }

    public long getTotalMovementTime() {
        return totalMovementTime;
    }

    public long getTotalDawdleTime() {
        return totalDawdleTime;
    }

    public long getTotalAccelerateTime() {
        return totalAccelerateTime;
    }

    public long getTotalDecelerationTime() {
        return totalDecelerationTime;
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

    public long getTotalSwitchingTrackTime() {
        return totalSwitchingTrackTime;
    }
}
