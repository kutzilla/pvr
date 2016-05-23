package de.fhms.pvr.trafficsimulator.system;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TrafficSimulatorTest {

    private Vehicle[][] testStreet;

    private Vehicle[][] expectedStreet;

    private TrafficSimulator trafficSimulator;

    @Test
    public void testAccelerateCarPixels() {
        testStreet = new Vehicle[1][10];
        testStreet[0][0] = new Vehicle(4);
        testStreet[0][5] = new Vehicle(2);
        testStreet[0][9] = new Vehicle(5);

        expectedStreet = new Vehicle[1][10];
        expectedStreet[0][0] = new Vehicle(5);
        expectedStreet[0][5] = new Vehicle(3);
        expectedStreet[0][9] = new Vehicle(5);

        trafficSimulator = new TrafficSimulator(testStreet);
        trafficSimulator.accelerateCarPixels();
        assertArrayEquals(expectedStreet, trafficSimulator.getStreet());
    }

    @Test
    public void testBreakCarPixels() {
        testStreet = new Vehicle[1][10];
        testStreet[0][0] = new Vehicle(5);
        testStreet[0][1] = new Vehicle(2);
        testStreet[0][3] = new Vehicle(4);
        testStreet[0][8] = new Vehicle(5);

        expectedStreet = new Vehicle[1][10];
        expectedStreet[0][0] = new Vehicle(0);
        expectedStreet[0][1] = new Vehicle(1);
        expectedStreet[0][3] = new Vehicle(4);
        expectedStreet[0][8] = new Vehicle(1);

        trafficSimulator = new TrafficSimulator(testStreet);
        trafficSimulator.breakCarPixels();
        assertArrayEquals(expectedStreet, trafficSimulator.getStreet());
    }

    @Test
    public void testSlowLinderCarPixels() {
        testStreet = new Vehicle[1][10];
        testStreet[0][0] = new Vehicle(1);
        testStreet[0][4] = new Vehicle(5);
        testStreet[0][7] = new Vehicle(2);

        expectedStreet = new Vehicle[1][10];
        expectedStreet[0][0] = new Vehicle(0);
        expectedStreet[0][4] = new Vehicle(5);
        expectedStreet[0][7] = new Vehicle(2);

        trafficSimulator = new TrafficSimulator(testStreet);
        trafficSimulator.setSlowLingerProbability(1.0);
        trafficSimulator.linderCarPixels();
        assertArrayEquals(expectedStreet, trafficSimulator.getStreet());
    }

    @Test
    public void testFastLinderCarPixels() {
        testStreet = new Vehicle[1][10];
        testStreet[0][0] = new Vehicle(1);
        testStreet[0][4] = new Vehicle(5);
        testStreet[0][7] = new Vehicle(2);

        expectedStreet = new Vehicle[1][10];
        expectedStreet[0][0] = new Vehicle(1);
        expectedStreet[0][4] = new Vehicle(4);
        expectedStreet[0][7] = new Vehicle(1);

        trafficSimulator = new TrafficSimulator(testStreet);
        trafficSimulator.setFastLingerProbability(1.0);
        trafficSimulator.linderCarPixels();
        assertArrayEquals(expectedStreet, trafficSimulator.getStreet());
    }

    @Test
    public void testMoveCarPixels() {
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

        trafficSimulator = new TrafficSimulator(testStreet);
        trafficSimulator.setIteration(1);
        trafficSimulator.moveCarPixels();
        assertArrayEquals(expectedStreet, trafficSimulator.getStreet());
    }
}
