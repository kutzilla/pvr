package de.fhms.pvr.trafficsimulator.system;

import de.fhms.pvr.trafficsimulator.system.TrafficSimulator.TrafficSimulatorBuilder;
import org.junit.Ignore;
import org.junit.Test;


import static org.junit.Assert.*;

public class TrafficSimulatorTest {

    private TrafficSimulatorBuilder builder;

    private Vehicle[][] testStreet;

    private Vehicle[][] expectedStreet;

    private TrafficSimulator trafficSimulator;


    @Test
    public void testSimulateDriveAction() throws Exception {
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
        trafficSimulator.simulateDriveAction();
        assertArrayEquals(expectedStreet, trafficSimulator.getStreet());
    }

    @Test
    @Ignore
    public void testSimulateMovement() {
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
        trafficSimulator.setIteration(1);
        trafficSimulator.simulateMovement();
        assertArrayEquals(expectedStreet, trafficSimulator.getStreet());
    }



}
