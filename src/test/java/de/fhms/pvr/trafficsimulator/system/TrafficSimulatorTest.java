package de.fhms.pvr.trafficsimulator.system;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TrafficSimulatorTest {

    private Vehicle[][] testStreet;

    private Vehicle[][] expectedStreet;

    private TrafficSimulator trafficSimulator;

    @Test
    public void testAccelerate() {
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
}
