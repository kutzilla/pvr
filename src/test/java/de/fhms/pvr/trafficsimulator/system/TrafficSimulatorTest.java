package de.fhms.pvr.trafficsimulator.system;

import de.fhms.pvr.trafficsimulator.system.TrafficSimulator.TrafficSimulatorBuilder;
import de.fhms.pvr.trafficsimulator.system.measure.TimeMeasureType;
import org.junit.Ignore;
import org.junit.Test;


import static org.junit.Assert.*;

public class TrafficSimulatorTest {

    private TrafficSimulatorBuilder builder;

    private Vehicle[][] testStreet;

    private Vehicle[][] expectedStreet;

    private TrafficSimulator trafficSimulator;


    @Test
    public void testSimulateDriveActionSingleThreaded() {
        testStreet = new Vehicle[1][10];
        testStreet[0][0] = new Vehicle(4);
        testStreet[0][5] = new Vehicle(2);
        testStreet[0][9] = new Vehicle(5);

        expectedStreet = new Vehicle[1][10];
        expectedStreet[0][0] = new Vehicle(3);
        expectedStreet[0][5] = new Vehicle(2);
        expectedStreet[0][9] = new Vehicle(0);

        trafficSimulator = new TrafficSimulatorBuilder(testStreet).withFastDawdleProbability(1.0)
                .withSlowDawdleProbability(1.0).withSwitchProbability(1.0).build();
        trafficSimulator.getTimeMeasureController().startOrResume(TimeMeasureType.KAPPA);
        trafficSimulator.simulateDriveAction();
        assertArrayEquals(expectedStreet, trafficSimulator.getStreet());
    }

    @Test
    public void testSimulateDriveActionMultiThreadedEqualTasks() {
        testStreet = new Vehicle[1][10];
        testStreet[0][0] = new Vehicle(4);
        testStreet[0][5] = new Vehicle(2);
        testStreet[0][9] = new Vehicle(5);

        expectedStreet = new Vehicle[1][10];
        expectedStreet[0][0] = new Vehicle(3);
        expectedStreet[0][5] = new Vehicle(2);
        expectedStreet[0][9] = new Vehicle(0);

        trafficSimulator = new TrafficSimulatorBuilder(testStreet).withFastDawdleProbability(1.0)
                .withSlowDawdleProbability(1.0).withSwitchProbability(1.0)
                .withWorkerAmount(2).withTaskAmount(2).build();
        trafficSimulator.getTimeMeasureController().startOrResume(TimeMeasureType.KAPPA);
        trafficSimulator.simulateDriveAction();
        assertArrayEquals(expectedStreet, trafficSimulator.getStreet());
    }

    @Test
    public void testSimulateDriveActionMultiThreadedUnEqualTasks() {
        testStreet = new Vehicle[1][10];
        testStreet[0][0] = new Vehicle(4);
        testStreet[0][5] = new Vehicle(2);
        testStreet[0][9] = new Vehicle(5);

        expectedStreet = new Vehicle[1][10];
        expectedStreet[0][0] = new Vehicle(3);
        expectedStreet[0][5] = new Vehicle(2);
        expectedStreet[0][9] = new Vehicle(0);

        trafficSimulator = new TrafficSimulatorBuilder(testStreet).withFastDawdleProbability(1.0)
                .withSlowDawdleProbability(1.0).withSwitchProbability(1.0)
                .withWorkerAmount(2).withTaskAmount(3).build();

        trafficSimulator.getTimeMeasureController().startOrResume(TimeMeasureType.KAPPA);
        trafficSimulator.simulateDriveAction();
        assertArrayEquals(expectedStreet, trafficSimulator.getStreet());
    }


    @Test
    public void testSimulateMovementSingleThreaded() {
        testStreet = new Vehicle[1][10];
        testStreet[0][0] = new Vehicle(3);
        testStreet[0][4] = new Vehicle(2);
        testStreet[0][8] = new Vehicle(2);
        testStreet[0][9] = new Vehicle(3);

        expectedStreet = new Vehicle[1][10];
        expectedStreet[0][3] = new Vehicle(3);
        expectedStreet[0][6] = new Vehicle(2);
        expectedStreet[0][0] = new Vehicle(2);
        expectedStreet[0][2] = new Vehicle(3);

        trafficSimulator = new TrafficSimulatorBuilder(testStreet).build();
        trafficSimulator.getTimeMeasureController().startOrResume(TimeMeasureType.KAPPA);
        trafficSimulator.simulateMovement();
        assertArrayEquals(expectedStreet, trafficSimulator.getStreet());
    }

    @Test
    public void testSimulateMovementMultiThreadedEqualTasks() {
        testStreet = new Vehicle[1][10];
        testStreet[0][0] = new Vehicle(3);
        testStreet[0][4] = new Vehicle(2);
        testStreet[0][8] = new Vehicle(2);
        testStreet[0][9] = new Vehicle(3);

        expectedStreet = new Vehicle[1][10];
        expectedStreet[0][3] = new Vehicle(3);
        expectedStreet[0][6] = new Vehicle(2);
        expectedStreet[0][0] = new Vehicle(2);
        expectedStreet[0][2] = new Vehicle(3);

        trafficSimulator = new TrafficSimulatorBuilder(testStreet)
                .withWorkerAmount(2).withTaskAmount(2).build();

        trafficSimulator.getTimeMeasureController().startOrResume(TimeMeasureType.KAPPA);
        trafficSimulator.simulateMovement();
        assertArrayEquals(expectedStreet, trafficSimulator.getStreet());
    }

    @Test
    public void testSimulateMovementMultiThreadedUnequalTasks() {
        testStreet = new Vehicle[1][10];
        testStreet[0][0] = new Vehicle(3);
        testStreet[0][4] = new Vehicle(2);
        testStreet[0][8] = new Vehicle(2);
        testStreet[0][9] = new Vehicle(3);

        expectedStreet = new Vehicle[1][10];
        expectedStreet[0][3] = new Vehicle(3);
        expectedStreet[0][6] = new Vehicle(2);
        expectedStreet[0][0] = new Vehicle(2);
        expectedStreet[0][2] = new Vehicle(3);

        trafficSimulator = new TrafficSimulatorBuilder(testStreet)
                .withWorkerAmount(2).withTaskAmount(3).build();

        trafficSimulator.getTimeMeasureController().startOrResume(TimeMeasureType.KAPPA);
        trafficSimulator.simulateMovement();
        assertArrayEquals(expectedStreet, trafficSimulator.getStreet());
    }

}
